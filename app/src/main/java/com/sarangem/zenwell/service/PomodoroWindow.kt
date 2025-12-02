package com.sarangem.zenwell.service

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.PomodoroBlockScreen
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.createNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.NotificationChannels
import com.sarangem.zenwell.utils.deleteNotificationById
import com.sarangem.zenwell.utils.secondsToString

class PomodoroWindow(
    private val schedule: Schedules,
    private val overlayWindowList: List<OverlayWindow> = listOf(),
    private val context: Context,
    private val recheckApp: () -> Unit = {}
) {
    var coroutineScope = CoroutineScope(Dispatchers.IO)
    val classContext = this

    var sessions = schedule.pomodoroSessionNumber
    var isActive = false
    var startTime = 0L
    var isWorkTime = true
    var currentSegmentTime = 0L

    var isPaused = false
    var timeBeforePaused = 0L

    fun onPomodoroStart() {
        ServiceLogger.d { "Pomodoro session with schedule id ${schedule.id} has started." }
        isActive = true
        coroutineScope.launch {
            while (sessions > 0) {
                isPaused = false

                if(isWorkTime) {

                    startTime = System.currentTimeMillis()
                    currentSegmentTime = schedule.pomodoroWorkTimeInMinutes * 60 * 1000L
                    overlayWindowList.forEach { window ->
                        window.content = {
                            PomodoroBlockScreen(
                                modifier = Modifier.fillMaxSize(),
                                message = schedule.message,
                                pomodoroWindow = classContext
                            )
                        }
                        window.isAppOpened = false
                    }
                    val currentDelayTime = (currentSegmentTime - timeBeforePaused) / 1000
                    deleteNotificationById(schedule.id, context)
                    repeat (currentDelayTime.toInt()){
                        createNotification(
                            message = secondsToString(getElapsedTimeInSeconds()) + context.getString(R.string.work_time_notification),
                            id = schedule.id,
                            context = context,
                            notificationChannel = NotificationChannels.PomodoroNotification
                        )
                        delay(1000L)
                    }

                    isPaused = false
                    timeBeforePaused = 0L
                }

                // rest time

                startTime = System.currentTimeMillis()
                isWorkTime = false
                currentSegmentTime = schedule.pomodoroRestTimeInMinutes * 60 * 1000L
                overlayWindowList.forEach { window ->
                    window.isAppOpened = true
                }
                withContext(Dispatchers.Main) {
                    overlayWindowList.forEach { window ->
                        window.close()
                    }
                }
                val currentDelayTime = (currentSegmentTime - timeBeforePaused) / 1000
                deleteNotificationById(schedule.id, context)
                repeat (currentDelayTime.toInt()){
                    createNotification(
                        message = secondsToString(getElapsedTimeInSeconds()
                        ) + context.getString(R.string.rest_time_notification),
                        id = schedule.id,
                        context = context,
                        notificationChannel = NotificationChannels.PomodoroNotification
                    )
                    delay(1000L)
                }
                isPaused = false
                timeBeforePaused = 0L
                isWorkTime = true
                sessions--

                // re-trigger opening window
                ServiceLogger.d { "Rechecking the app." }
                overlayWindowList.forEach { window ->
                    window.isAppOpened = false
                }
                withContext(Dispatchers.Main) {
                    recheckApp()
                }
            }
            onPomodoroEnd()

        }
    }

    fun onPomodoroEnd() {
        ServiceLogger.d { "Stopping pomodoro session with schedule id ${schedule.id}." }
        isActive = false
        sessions = schedule.pomodoroSessionNumber
        startTime = 0L
        isPaused = false
        timeBeforePaused = 0L
        isWorkTime = true
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.IO)
        deleteNotificationById(schedule.id, context)
        overlayWindowList.forEach { window ->
            window.close()
        }
        overlayWindowList.forEach { window -> // prevent executing open() if pomodoro has not started
            window.isAppOpened = true
        }
    }

    fun onPomodoroPause() {
        ServiceLogger.d { "Pausing pomodoro session with schedule id ${schedule.id}." }
        isActive = false
        timeBeforePaused += System.currentTimeMillis() - startTime
        isPaused = true
        startTime = 0L
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.IO)
        deleteNotificationById(schedule.id, context)
        overlayWindowList.forEach { window ->
            window.close()
        }
        overlayWindowList.forEach { window -> // prevent executing open() if pomodoro has not started
            window.isAppOpened = true
        }
    }

    fun onPomodoroSkip() {
        ServiceLogger.d { "Skipping pomodoro session with schedule id ${schedule.id} to next work or rest session." }
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.IO)
        if(!isWorkTime) sessions--
        isWorkTime = !isWorkTime
        isPaused = false
        timeBeforePaused = 0L
        onPomodoroStart()
    }

    fun getElapsedTimeInSeconds(): Int {
        val time = if (isPaused) { 
            currentSegmentTime - timeBeforePaused
        } else {
            (currentSegmentTime - timeBeforePaused) - (System.currentTimeMillis() - startTime)
        } / 1000
        return time.toInt()
    }

}