package com.sarangem.zenwell.service

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.service.ui.BlockingWindow
import com.sarangem.zenwell.service.ui.BreathingScreen
import com.sarangem.zenwell.service.ui.FullBlockScreen
import com.sarangem.zenwell.service.ui.WaitScreen
import com.sarangem.zenwell.ui.theme.ZenwellTheme

data class ScheduleInfo(
    private val context: Context,
    var schedule: Schedules,
    val appSet: Set<String>,
) {
    val blockingWindowList: MutableList<BlockingWindow> = mutableListOf()

    init {
        appSet.forEach { appName ->
            blockingWindowList.add(

                BlockingWindow(
                    context = context,
                    schedule = schedule,
                    appName = appName,
                    content = { height, width, onTimerEnd ->
                        ZenwellTheme {
                            when (schedule.blockType) {

                                BlockType.FullBlock -> FullBlockScreen(
                                    message = schedule.message,
                                    modifier = Modifier.fillMaxSize(),
                                    width = width
                                )

                                BlockType.Wait -> WaitScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onTimerEnd = onTimerEnd,
                                    waitTimeInSeconds = schedule.waitTimeInSeconds,
                                    message = schedule.message,
                                    showOpenDialog = schedule.waitEnterButton,
                                    width = width,
                                )

                                BlockType.Breathing -> BreathingScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onTimerEnd = onTimerEnd,
                                    breathingCycleDuration = schedule.breathingCycleDuration,
                                    breathingCycleNumber = schedule.breathingCycleNumber,
                                    showOpenDialog = schedule.waitEnterButton,
                                    message = schedule.message,
                                    width = width
                                )

                                else -> {}
                            }
                        }
                    }
                )

            )
        }
    }

}
