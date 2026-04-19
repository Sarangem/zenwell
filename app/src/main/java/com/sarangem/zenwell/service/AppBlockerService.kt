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
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.deleteAllNotificationChannel
import com.sarangem.zenwell.utils.getCurrentTimeInMinutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@SuppressLint("AccessibilityPolicy")
class AppBlockerService : AccessibilityService() {

    companion object {
        var instance: AppBlockerService? = null
    }
    val supervisorJob = SupervisorJob()

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        CoroutineScope(Dispatchers.IO).launch {
            initializeRepository()
        }
    }

    val scheduleInfoList: MutableList<ScheduleInfo> = mutableListOf()
    suspend fun initializeRepository() {
        supervisorJob.cancelChildren()
        val schedulesRepository = (application as ZenwellApplication).container
        val schedulesList = schedulesRepository.getAllSchedules().first().filter { it.isActive }
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
            scheduleInfoList.any { it.viewsMap.isNotEmpty() } -> {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            }
            else -> AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        }
        serviceInfo = info
        ServiceLogger.i { "Service fully initiated with $serviceInfo" }
        ServiceLogger.i { "Apps to block are: ${scheduleInfoList.map { it.appSet }}"}
        onAccessibilityEvent(null)
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

            // if blocked app in PiP mode, the overlay window never closes forcing a reboot
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                windowInfo.isInPictureInPictureMode) continue

            // get current root and package name && terminate if null
            val root = windowInfo.root ?: continue
            val currentApp = root.packageName ?: continue
            ServiceLogger.d { root.className.toString() }
            currentVisibleApps.add(currentApp)
            scheduleInfoList.forEach { scheduleInfo ->
                scheduleInfo.viewsMap[currentApp]?.forEach {
                    if (root.findAccessibilityNodeInfosByViewId(it).isNotEmpty()) {
                        currentVisibleApps.add(it)
                        ServiceLogger.d { "Processing view id: $it" }
                    }
                }
            }
            ServiceLogger.d { "Processing app $currentApp" }

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
        val iterator = openedWindows.iterator() // avoid concurrent modification exception
        while (iterator.hasNext()) {
            val blockingWindow = iterator.next()
            if (blockingWindow.packageName !in currentVisibleApps) {
                ServiceLogger.i { "Closing window for ${blockingWindow.packageName} for schedule ${blockingWindow.schedule.id}." }
                blockingWindow.close()
            }
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
