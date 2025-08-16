package com.sarangem.zenwell.service

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.tables.Schedules
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
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val context = this
    var isActive = false
    var elapsedTimeInSeconds: Int = 0
    var isWorkTime: Boolean = true


    fun onPomodoroStart() {
        ServiceLogger.i { "Pomodoro session with schedule id ${schedule.id} has started." }
        isActive = true

        coroutineScope.launch {
            while (true) {

                // start work time
                elapsedTimeInSeconds = schedule.pomodoroWorkTimeInMinutes * 60
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
                while (elapsedTimeInSeconds >= 0){
                    delay(1000L)
                    elapsedTimeInSeconds--
                }

                // start rest time
                elapsedTimeInSeconds = schedule.pomodoroRestTimeInMinutes * 60
                isWorkTime = false
                blockingWindowList.forEach { window ->
                    window.isAppOpened = true
                }
                withContext(Dispatchers.Main) {
                    blockingWindowList.forEach { window ->
                        window.close()
                    }
                }
                while (elapsedTimeInSeconds >= 0){
                    delay(1000L)
                    elapsedTimeInSeconds--
                }

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
        }
    }

    fun onPomodoroEnd() {
        ServiceLogger.i { "Stopping pomodoro session with schedule id ${schedule.id}." }
        isActive = false
        coroutineScope.cancel()
        blockingWindowList.forEach { window ->
            window.close()
        }
        blockingWindowList.forEach { window -> // prevent executing open() if pomodoro has not started
            window.isAppOpened = true
        }
    }
}