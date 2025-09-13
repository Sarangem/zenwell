package com.sarangem.zenwell.service

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.ui.screen.BreathingScreen
import com.sarangem.zenwell.service.ui.screen.FullBlockScreen
import com.sarangem.zenwell.service.ui.screen.WaitScreen

data class ScheduleInfo(
    private val context: Context,
    var schedule: Schedules,
    val appSet: Set<String>,
) {
    var pomodoroWindow: PomodoroWindow? = null
    val blockingWindowList: MutableList<BlockingWindow> = mutableListOf()

    init {
        appSet.forEach { appName ->
            blockingWindowList.add(

                BlockingWindow(
                    context = context,
                    schedule = schedule,
                    appName = appName,
                    content = { onTimerEnd ->

                        when (schedule.blockType) {

                            BlockType.FullBlock -> FullBlockScreen(
                                message = schedule.message,
                                modifier = Modifier.fillMaxSize()
                            )

                            BlockType.Wait -> WaitScreen(
                                modifier = Modifier.fillMaxSize(),
                                onTimerEnd = onTimerEnd,
                                waitTimeInSeconds = schedule.waitTimeInSeconds,
                                message = schedule.message,
                                showOpenDialog = schedule.waitEnterButton,
                            )

                            BlockType.Breathing -> BreathingScreen(
                                modifier = Modifier.fillMaxSize(),
                                onTimerEnd = onTimerEnd,
                                breathingCycleDuration = schedule.breathingCycleDuration,
                                breathingCycleNumber = schedule.breathingCycleNumber,
                                showOpenDialog = schedule.waitEnterButton,
                                message = schedule.message
                            )

                            else -> {}

                        }
                    }
                )

            )
        }

        pomodoroWindow = if (schedule.isPomodoro){
            PomodoroWindow(
                schedule = schedule,
                blockingWindowList = blockingWindowList,
                context = context
            )
        } else {
            null
        }
    }

}