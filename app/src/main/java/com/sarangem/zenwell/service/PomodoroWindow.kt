package com.sarangem.zenwell.service

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.ui.PomodoroBlockScreen
import com.sarangem.zenwell.utils.ServiceLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PomodoroWindow(
    private val schedule: Schedules,
    private val blockingWindowList: List<BlockingWindow> = listOf()
) {
    var coroutineScope = CoroutineScope(Dispatchers.IO)
    val context = this

    var sessions = schedule.pomodoroSessionNumber
    var isActive = false
    var startTime = 0L
    var isWorkTime = true
    var totalTime = 0L


    fun onPomodoroStart() {
        ServiceLogger.d { "Pomodoro session with schedule id ${schedule.id} has started." }
        isActive = true

        coroutineScope.launch {
            while (sessions > 0) {

                // start work time
                startTime = System.currentTimeMillis()
                totalTime = schedule.pomodoroWorkTimeInMinutes * 60 * 1000L
                isWorkTime = true
                blockingWindowList.forEach { window ->
                    window.content = { width, onTimerEnd ->
                        PomodoroBlockScreen(
                            modifier = Modifier.fillMaxSize(),
                            message = schedule.message,
                            width = width,
                            pomodoroWindow = context
                        )
                    }
                    window.isAppOpened = false
                }
                delay(totalTime)

                // start rest time
                startTime = System.currentTimeMillis()
                isWorkTime = false
                totalTime = schedule.pomodoroWorkTimeInMinutes * 60 * 1000L
                blockingWindowList.forEach { window ->
                    window.isAppOpened = true
                }
                withContext(Dispatchers.Main) {
                    blockingWindowList.forEach { window ->
                        window.close()
                    }
                }
                delay(totalTime)
                sessions--

                // re-trigger opening window
                ServiceLogger.d { "Rechecking the app." }
                val instance = AppBlockerService.instance ?: return@launch
                blockingWindowList.forEach { window ->
                    window.isAppOpened = false
                }
                withContext(Dispatchers.Main) {
                    instance.recheckApp()
                }

            }
            onPomodoroEnd()
        }
    }

    fun onPomodoroEnd() {
        ServiceLogger.d { "Stopping pomodoro session with schedule id ${schedule.id}." }

        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.IO)

        blockingWindowList.forEach { window ->
            window.close()
        }
        blockingWindowList.forEach { window -> // prevent executing open() if pomodoro has not started
            window.isAppOpened = true
        }

        isActive = false
        sessions = schedule.pomodoroSessionNumber
        startTime = 0L
    }

    fun getElapsedTimeInSeconds(): Long{
        return (totalTime - (System.currentTimeMillis() - startTime)) / 1000
    }
}