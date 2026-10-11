/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import com.sarangem.zenwell.CRASH_LOG_FILE
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.tables.AppNames
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

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        // set error logging to file
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

        supervisorJob.cancelChildren() // close all windows to prevent crashes

        // load ACTIVE schedules WITH APPS into scheduleInfoList
        val schedulesList = schedulesRepository.getAllSchedules().first().filter { it.isActive && it.weekDays.isNotEmpty() }
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

        // Change service info and log
        val info = serviceInfo
        info.eventTypes = when {
            scheduleInfoList.isEmpty() -> 0
            scheduleInfoList.any { scheduleInfo ->
                scheduleInfo.appNamesList.any { appRule -> appRule.viewId != null }
            } -> {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            }
            else -> AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        }
        serviceInfo = info
        ServiceLogger.i { "Service fully initiated with $serviceInfo" }

        onAccessibilityEvent(null) // open overlay window if required
    }


    val openedWindows = mutableListOf<OverlayWindow>()
    var lastCheckedTime: Long = 0L
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        // prevent repeated window content changed events
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (event.contentChangeTypes != AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE) return
            val time = System.currentTimeMillis()
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

            if (
                windowInfo.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD &&
                event?.windowId == windowInfo.id
            ) return // probably a keyboard typing

            // only if application window
            if (windowInfo.type != AccessibilityWindowInfo.TYPE_APPLICATION) continue

            // get current root and package name && terminate if null
            val root = windowInfo.root ?: continue
            val currentPackage = root.packageName ?: continue
            if (currentPackage == packageName && root.className == "android.view.ViewGroup") continue

            // open window to block
            currentlyActiveSchedules.forEach { scheduleInfo ->
                scheduleInfo.appsByPackage[currentPackage]?.forEach { appRule ->
                    if (
                        appRule.viewId != null &&
                        root.findAccessibilityNodeInfosByViewId(appRule.viewId).isEmpty()
                    ) return@forEach
                    currentVisibleTargets.add(appRule)
                    val blockingWindow = scheduleInfo.overlayWindowList.firstOrNull { it.targetApp == appRule } ?: return@forEach
                    ServiceLogger.i { "Opening window for schedule ${scheduleInfo.schedule.id} and ${blockingWindow.targetApp}" }
                    blockingWindow.open()
                }
            }

            windowInfo.recycle()
        }

        // close the window
        openedWindows
            .filter { it.targetApp !in currentVisibleTargets }
            .forEach { it.close() }
        ServiceLogger.d { "Accessibility Event completed." }
    }


    fun getPomodoroWindow(scheduleId: Int) = scheduleInfoList.firstOrNull { it.schedule.id == scheduleId }?.pomodoroWindow


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
