/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import com.sarangem.zenwell.CRASH_LOG_FILE
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.deleteAllNotificationChannel
import com.sarangem.zenwell.utils.getCurrentTimeInMinutes
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
import java.util.Calendar
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
            scheduleInfoList.any { it.viewsMap.isNotEmpty() } -> {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            }
            else -> AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        }
        serviceInfo = info
        ServiceLogger.i { "Service fully initiated with $serviceInfo" }
        ServiceLogger.i { "Apps to block are: ${scheduleInfoList.map { it.appSet }}"}

        onAccessibilityEvent(null) // open overlay window if required
    }


    val openedWindows = mutableListOf<OverlayWindow>()
    var lastCheckedTime: Long = 0L
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        // only continue if major content change
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val isPaneChange =
                    (event.contentChangeTypes and AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_APPEARED != 0) ||
                            (event.contentChangeTypes and AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED != 0)
                if (!isPaneChange) return
            } else {
                val time = System.currentTimeMillis()
                if (time < lastCheckedTime) return else lastCheckedTime = time + 2000L
            }
        }

        ServiceLogger.d { "There are ${windows.size} and they are $windows" }
        val currentVisibleApps = mutableListOf<CharSequence>()

        val calendar = Calendar.getInstance(Locale.getDefault())
        val currentTime = getCurrentTimeInMinutes(calendar)
        val todayDay = calendar.get(Calendar.DAY_OF_WEEK)

        for (windowInfo in windows) {

            // only if application window
            if (windowInfo.type != AccessibilityWindowInfo.TYPE_APPLICATION) continue

            // get current root and package name && terminate if null
            val root = windowInfo.root ?: continue
            val currentApp = root.packageName ?: continue
            if (currentApp == packageName && root.className == "android.view.ViewGroup") continue
            ServiceLogger.d { "Processing app $currentApp (${root.className})" }
            currentVisibleApps.add(currentApp)
            scheduleInfoList.forEach { scheduleInfo ->
                scheduleInfo.viewsMap[currentApp]?.forEach {
                    if (root.findAccessibilityNodeInfosByViewId(it).isNotEmpty()) {
                        currentVisibleApps.add(it)
                        ServiceLogger.d { "Processing view id: $it" }
                    }
                }
            }

            // open the window
            for (scheduleInfo in scheduleInfoList) {
                if (todayDay !in scheduleInfo.schedule.weekDays) continue
                if (currentTime !in scheduleInfo.schedule.startTimeInMinutes..scheduleInfo.schedule.endTimeInMinutes) continue
                if (currentVisibleApps.any { it in scheduleInfo.appSet }) {
                    val blockingWindow =
                        scheduleInfo.overlayWindowList.firstOrNull {
                            it.packageName in currentVisibleApps
                        } ?: continue
                    ServiceLogger.i { "Opening window for schedule ${scheduleInfo.schedule.id} and ${blockingWindow.appName}" }
                    blockingWindow.open()
                }
            }
            windowInfo.recycle()
        }

        // close the window
        openedWindows
            .filter { it.packageName !in currentVisibleApps } // create a copy to avoid modification exceptions
            .forEach { overlayWindow ->
                ServiceLogger.i { "Closing window for ${overlayWindow.packageName} for schedule ${overlayWindow.schedule.id}." }
                overlayWindow.close()
            }
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
