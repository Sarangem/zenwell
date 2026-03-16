package com.sarangem.zenwell.service

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.BreathingScreen
import com.sarangem.zenwell.ui.overlay.FullBlockScreen
import com.sarangem.zenwell.ui.overlay.WaitScreen
import com.sarangem.zenwell.ui.overlay.MathProblemScreen
import com.sarangem.zenwell.ui.overlay.MultiplicationTableScreen
import com.sarangem.zenwell.ui.overlay.PomodoroBlockScreen
import com.sarangem.zenwell.ui.overlay.TypingScreen
import kotlinx.coroutines.Job

data class ScheduleInfo(
    private val service: AppBlockerService,
    var schedule: Schedules,
    val appList: List<String>,
    val supervisorJob: Job
) {
    val viewsMap: Map<String, List<String>> = appList
        .filter { it.contains(":id/") }
        .groupBy { it.substringBefore(":id/") }

    val overlayWindowList: List<OverlayWindow> = appList.map { appName ->
        OverlayWindow(
            service = service,
            schedule = schedule,
            appName = appName,
            supervisorJob = supervisorJob,
            content = { onTimerEnd ->

                if (pomodoroWindow?.isWorkTime == true) {

                    PomodoroBlockScreen(
                        modifier = Modifier.fillMaxSize(),
                        message = schedule.message,
                        getElapsedTimeInSeconds = { pomodoroWindow.getElapsedTimeInSeconds() },
                        segmentTime = schedule.pomodoroWorkTimeInMinutes * 60,
                        getFormattedTime = { pomodoroWindow.getFormattedTime() }
                    )

                } else {
                    when (schedule.unlockMethod) {

                        UnlockMethod.StrictBlock -> FullBlockScreen(
                            message = schedule.message,
                            modifier = Modifier.fillMaxSize()
                        )

                        UnlockMethod.Timer -> WaitScreen(
                            modifier = Modifier.fillMaxSize(),
                            onTimerEnd = onTimerEnd,
                            schedule = schedule
                        )

                        UnlockMethod.Breathing -> BreathingScreen(
                            modifier = Modifier.fillMaxSize(),
                            onTimerEnd = onTimerEnd,
                            schedule = schedule
                        )

                        UnlockMethod.MathProblem -> MathProblemScreen(
                            modifier = Modifier.fillMaxSize(),
                            onTimerEnd = onTimerEnd,
                            schedule = schedule
                        )

                        UnlockMethod.MultiplicationTable -> MultiplicationTableScreen(
                            modifier = Modifier.fillMaxSize(),
                            schedule = schedule,
                            onTimerEnd = onTimerEnd,
                        )

                        UnlockMethod.Typing -> TypingScreen(
                            modifier = Modifier.fillMaxSize(),
                            schedule = schedule,
                            onTimerEnd = onTimerEnd,
                        )

                    }
                }
            }
        )
    }

    val pomodoroWindow = if (schedule.isPomodoro) {
        PomodoroWindow(
            schedule = schedule,
            overlayWindowList = overlayWindowList,
            context = service,
            supervisorJob = supervisorJob,
            recheckApp = { service.onAccessibilityEvent(null) },
        )
    } else {
        null
    }
}