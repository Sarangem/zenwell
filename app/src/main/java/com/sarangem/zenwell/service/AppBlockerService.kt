/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.sarangem.zenwell.CRASH_LOG_FILE
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.service.pomodoro.PomodoroManager
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.deleteAllNotificationChannel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.Locale

@SuppressLint("AccessibilityPolicy")
@AndroidEntryPoint
class AppBlockerService : AccessibilityService() {

    companion object { var instance: AppBlockerService? = null }
    val supervisorJob = SupervisorJob()
    val scheduleInfoList: MutableList<ScheduleInfo> = mutableListOf()
    @Inject lateinit var schedulesRepository: SchedulesRepository
    @Inject lateinit var pomodoroManager: PomodoroManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val logFile = File(filesDir, CRASH_LOG_FILE)
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                logFile.writeText(
                    "\n === AppBlockerService Crash at $timestamp === \n ${throwable.stackTraceToString()}"
                )
            } catch (e: Exception) {
                ServiceLogger.e({ "Unable to save crash to file." }, e)
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }

        CoroutineScope(Dispatchers.IO).launch {
            initializeRepository()
        }
    }

    suspend fun initializeRepository() {
        supervisorJob.cancelChildren()

        val schedulesList = schedulesRepository.getAllSchedules().first()
            .filter { it.isActive && it.weekDays.isNotEmpty() }
        scheduleInfoList.clear()
        schedulesList.forEach { schedule ->
            scheduleInfoList.add(
                ScheduleInfo(
                    service = this,
                    schedule = schedule,
                    appNamesList = schedulesRepository.getAppNamesById(schedule.id).first(),
                    supervisorJob = supervisorJob
                )
            )
        }
        scheduleInfoList.removeAll { !it.schedule.isPomodoro && it.appNamesList.isEmpty() }

        val info = serviceInfo
        info.eventTypes = when {
            scheduleInfoList.isEmpty() -> 0
            scheduleInfoList.any { scheduleInfo ->
                scheduleInfo.appNamesList.any { appRule -> appRule.viewId != null || appRule.viewKeyword != null }
            } -> {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            }
            else -> AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        }
        serviceInfo = info
        ServiceLogger.i { "Service fully initiated with $serviceInfo" }
        ServiceLogger.i { "Loaded: $scheduleInfoList" }

        withContext(Dispatchers.Main) {
            onAccessibilityEvent(null)
        }
    }

    val openedWindows = mutableListOf<OverlayWindow>()
    var lastCheckedTime: Long = 0L
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if ( (event.contentChangeTypes and (
                AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE or
                AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT or
                AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION
            )) == 0 ) return
            val time = SystemClock.elapsedRealtime()
            if (time < lastCheckedTime) return else lastCheckedTime = time + 2000L
        }

        ServiceLogger.d { "There are ${windows.size} and they are $windows" }
        val currentVisibleTargets = mutableListOf<AppNames>()

        val currentTime = LocalTime.now().toSecondOfDay() / 60
        val todayDay = LocalDate.now().dayOfWeek.value

        val currentlyActiveSchedules = scheduleInfoList.filter { scheduleInfo ->
            if (todayDay !in scheduleInfo.schedule.weekDays) return@filter false
            val (start, end) = scheduleInfo.schedule.startTimeInMinutes to scheduleInfo.schedule.endTimeInMinutes
            if (start <= end) currentTime in start..end else (currentTime !in (end + 1)..<start)
        }

        for (windowInfo in windows) {
            if (windowInfo.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD && event?.windowId == windowInfo.id) return
            if (windowInfo.type != AccessibilityWindowInfo.TYPE_APPLICATION) continue

            val root = windowInfo.root ?: continue
            val currentPackage = root.packageName?.toString() ?: continue
            if (currentPackage == packageName && root.className == "android.view.ViewGroup") continue
            ServiceLogger.d { "Checking schedule for app $currentPackage" }

            currentlyActiveSchedules.forEach { scheduleInfo ->
                scheduleInfo.appsByPackage[currentPackage]?.forEach { appRule ->
                    if (!root.matches(appRule)) return@forEach
                    val blockingWindow = scheduleInfo.overlayWindowList.firstOrNull { it.targetApp.id == appRule.id } ?: return@forEach
                    currentVisibleTargets.add(blockingWindow.targetApp)
                    ServiceLogger.i { "Opening window for schedule ${scheduleInfo.schedule.id} and ${blockingWindow.targetApp}" }
                    blockingWindow.open(pomodoroManager)
                }
            }

            windowInfo.recycle()
        }

        openedWindows
            .filter { it.targetApp !in currentVisibleTargets }
            .forEach { it.close() }
        ServiceLogger.d { "Accessibility Event completed." }
    }

    private fun AccessibilityNodeInfo.matches(rule: AppNames): Boolean {
        val nodes = rule.viewId?.let(::findAccessibilityNodeInfosByViewId) ?: listOf(this)
        if (nodes.isEmpty()) return false
        val keyword = rule.viewKeyword ?: return true
        return nodes.any { node ->
            node.text?.contains(keyword, ignoreCase = true) == true ||
                node.contentDescription?.contains(keyword, ignoreCase = true) == true ||
                node.findAccessibilityNodeInfosByText(keyword).isNotEmpty()
        }
    }

    override fun onInterrupt() {
        scheduleInfoList.forEach { scheduleInfo ->
            scheduleInfo.overlayWindowList.forEach { blockingWindow ->
                blockingWindow.close()
            }
        }
        deleteAllNotificationChannel(this)
        supervisorJob.cancel()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }
}