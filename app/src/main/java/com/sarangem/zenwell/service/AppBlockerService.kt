package com.sarangem.zenwell.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.data.NotificationChannels
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.deleteNotificationByChannel
import com.sarangem.zenwell.utils.getCurrentTimeInMinutes
import com.sarangem.zenwell.utils.getTodayDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@SuppressLint("AccessibilityPolicy")
class AppBlockerService : AccessibilityService() {

    val scheduleInfoList: MutableList<ScheduleInfo> = mutableListOf()

    companion object {
        var instance: AppBlockerService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        CoroutineScope(Dispatchers.IO).launch {
            initializeRepository()
            ServiceLogger.v { "Service fully initiated with $serviceInfo" }
        }

    }

    suspend fun initializeRepository() {

        val schedulesRepository = (application as ZenwellApplication).container

        val schedulesList = schedulesRepository.getAllSchedules().first()
        scheduleInfoList.clear()
        schedulesList.forEach { schedule ->

            scheduleInfoList.add(
                ScheduleInfo(
                    service = this,
                    schedule = schedule,
                    appSet = schedulesRepository.getAppNames(schedule.id).first().toSet()
                )
            )

        }

        recheckApp()

    }

    var previousApp = listOf<CharSequence>()
    val openedWindows = mutableListOf<OverlayWindow>()
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        // get list of application windows
        val applicationWindows =
            windows.filter { it.type == AccessibilityWindowInfo.TYPE_APPLICATION }
        ServiceLogger.d { "There are ${applicationWindows.size} and they are $applicationWindows" }

        ServiceLogger.d { "Previous app(s) were $previousApp" }

        val currentVisibleApps = mutableListOf<CharSequence>()
        var index = 1

        for (windowInfo in applicationWindows) {

            // get current root and package name && terminate if null
            val root = windowInfo.root ?: continue
            val currentApp = root.packageName ?: continue
            currentVisibleApps.add(currentApp)
            ServiceLogger.d { "Processing app $currentApp and is in $index position." }

            // check for duplicate entries
            if (currentApp in previousApp) continue

            // get window bounds
            val windowBounds = Rect()
            root.getBoundsInScreen(windowBounds)
            if (windowBounds.width() <= 0 || windowBounds.height() <= 0) {
                ServiceLogger.w { "Invalid window bounds received: $windowBounds for app $currentApp. Skipping overlay." }
                windowInfo.recycle()
                continue
            }

            // open the window
            for (scheduleInfo in scheduleInfoList) {

                if (!scheduleInfo.schedule.isEnabled) continue
                if (getTodayDay() !in scheduleInfo.schedule.weekDays) continue
                if (getCurrentTimeInMinutes() !in scheduleInfo.schedule.startTimeInMinutes..scheduleInfo.schedule.endTimeInMinutes) continue

                if (currentApp in scheduleInfo.appSet) {

                    val blockingWindow =
                        scheduleInfo.overlayWindowList.firstOrNull { it.appName == currentApp }
                            ?: continue
                    ServiceLogger.i { "Opening window for schedule ${scheduleInfo.schedule.id} and ${blockingWindow.appName}" }
                    blockingWindow.open(windowBounds)

                }
            }
            windowInfo.recycle()
            index++

        }

        // close the window
        val iterator = openedWindows.iterator() // use iterator to avoid concurrent modification exception
        while (iterator.hasNext()) {
            val blockingWindow = iterator.next()
            if (blockingWindow.appName !in currentVisibleApps) {
                ServiceLogger.i { "Closing window for ${blockingWindow.appName} for schedule ${blockingWindow.schedule.id}." }
                blockingWindow.close()
            }
        }

        previousApp = currentVisibleApps
        ServiceLogger.d { "Accessibility Event completed." }
    }

    fun recheckApp() {
        previousApp = listOf()
        ServiceLogger.d { "Rechecking to open blocking window" }
        onAccessibilityEvent(null)
    }

    fun getPomodoroWindow(scheduleId: Int): PomodoroWindow? {
        return scheduleInfoList.firstOrNull { it.schedule.id == scheduleId}?.pomodoroWindow
    }

    override fun onInterrupt() {
        scheduleInfoList.forEach { scheduleInfo ->
            scheduleInfo.overlayWindowList.forEach { blockingWindow ->
                blockingWindow.close()
            }
        }
        deleteNotificationByChannel(NotificationChannels.BlockNotification, this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }
}
