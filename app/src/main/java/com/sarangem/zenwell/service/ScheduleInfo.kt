package com.sarangem.zenwell.service

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.model.BlockType
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.BreathingScreen
import com.sarangem.zenwell.ui.overlay.FullBlockScreen
import com.sarangem.zenwell.ui.overlay.WaitScreen
import com.sarangem.zenwell.ui.overlay.MathEquationScreen
import com.sarangem.zenwell.ui.overlay.MultiplicationTableScreen
import com.sarangem.zenwell.ui.overlay.TypingScreen

data class ScheduleInfo(
    private val service: AppBlockerService,
    var schedule: Schedules,
    val appSet: Set<String>,
) {
    var pomodoroWindow: PomodoroWindow? = null
    val overlayWindowList: MutableList<OverlayWindow> = mutableListOf()

    init {
        appSet.forEach { appName ->
            overlayWindowList.add(

                OverlayWindow(
                    service = service,
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
                                schedule = schedule
                            )

                            BlockType.Breathing -> BreathingScreen(
                                modifier = Modifier.fillMaxSize(),
                                onTimerEnd = onTimerEnd,
                                schedule = schedule
                            )

                            BlockType.MathEquation -> MathEquationScreen(
                                modifier = Modifier.fillMaxSize(),
                                onTimerEnd = onTimerEnd,
                                schedule = schedule
                            )

                            BlockType.MultiplicationTable -> MultiplicationTableScreen(
                                modifier = Modifier.fillMaxSize(),
                                schedule = schedule,
                                onTimerEnd = onTimerEnd,
                            )

                            BlockType.Typing -> TypingScreen(
                                modifier = Modifier.fillMaxSize(),
                                schedule = schedule,
                                onTimerEnd = onTimerEnd,
                            )

                            else -> {}

                        }
                    }
                )

            )
        }

        pomodoroWindow = if (schedule.isPomodoro) {
            PomodoroWindow(
                schedule = schedule,
                overlayWindowList = overlayWindowList,
                context = service,
                recheckApp = { service.recheckApp() }
            )
        } else {
            null
        }
    }

}