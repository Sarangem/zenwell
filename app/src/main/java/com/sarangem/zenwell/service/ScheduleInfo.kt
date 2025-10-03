package com.sarangem.zenwell.service

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.data.BlockType
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.service.overlay.screens.BreathingScreen
import com.sarangem.zenwell.service.overlay.screens.FullBlockScreen
import com.sarangem.zenwell.service.overlay.screens.WaitScreen
import com.sarangem.zenwell.service.overlay.screens.mathequations.MathEquationScreen

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

                            BlockType.MathEquation -> MathEquationScreen(
                                modifier = Modifier.fillMaxSize(),
                                onTimerEnd = onTimerEnd,
                                minOperandDigits = schedule.mathEquationMinNumber,
                                maxOperandDigits = schedule.mathEquationMaxNumber,
                                minOperandDigitsInMultiplication = schedule.mathEquationMinNumberInMultiplication,
                                maxOperandDigitsInMultiplication = schedule.mathEquationMaxNumberInMultiplication,
                                numOperands = schedule.mathEquationNumOperands,
                                allowedMathOperators = schedule.allowedMathOperators,
                                showParentheses = schedule.mathEquationShowParentheses,
                                allowNegatives = schedule.mathEquationAllowNegatives,
                                showOpenDialog = schedule.waitEnterButton,
                                message = schedule.message
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