package com.sarangem.zenwell.service

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.PomodoroBlockScreen
import com.sarangem.zenwell.utils.ServiceLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.sarangem.zenwell.utils.createPomodoroNotification
import com.sarangem.zenwell.utils.deleteNotificationById
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren

class PomodoroWindow(
    private val schedule: Schedules,
    private val overlayWindowList: List<OverlayWindow> = listOf(),
    private val context: Context,
    supervisorJob: Job,
    private val recheckApp: () -> Unit = {}
) {
    val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)

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
                                getElapsedTimeInSeconds = { getElapsedTimeInSeconds() },
                                segmentTime = schedule.pomodoroWorkTimeInMinutes * 60,
                                getFormattedTime = { getFormattedTime() }
                            )
                        }
                        window.isAppOpened = false
                    }
                    deleteNotificationById(schedule.id, context)
                    while(isActive){
                        val remainingMs = getRemainingMillis()
                        if (remainingMs <= 0) break
                        createPomodoroNotification(
                            time = getFormattedTime(),
                            id = schedule.id,
                            context = context,
                            isWork = true
                        )
                        val delayTime = remainingMs % 1000
                        delay(if (delayTime > 0) delayTime else 1000L)
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
                deleteNotificationById(schedule.id, context)
                while (isActive) {
                    val remainingMs = getRemainingMillis()
                    if (remainingMs <= 0) break
                    createPomodoroNotification(
                        time = getFormattedTime(),
                        id = schedule.id,
                        context = context,
                        isWork = false
                    )
                    val delayTime = remainingMs % 1000
                    delay(if (delayTime > 0) delayTime else 1000L)
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
        coroutineScope.coroutineContext.cancelChildren()
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
        coroutineScope.coroutineContext.cancelChildren()
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
        coroutineScope.coroutineContext.cancelChildren()
        if(!isWorkTime) sessions--
        isWorkTime = !isWorkTime
        isPaused = false
        timeBeforePaused = 0L
        onPomodoroStart()
    }

    fun getRemainingMillis(): Long {
        return if (isPaused) {
            currentSegmentTime - timeBeforePaused
        } else {
            (currentSegmentTime - timeBeforePaused) - (System.currentTimeMillis() - startTime)
        }.coerceAtLeast(0)
    }
    fun getElapsedTimeInSeconds(): Long = getRemainingMillis() / 1000
    fun getFormattedTime(): String {
        val time = getElapsedTimeInSeconds()
        return "%02d:%02d".format(time / 60, time % 60)
    }

}