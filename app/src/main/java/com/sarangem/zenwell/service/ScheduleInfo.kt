/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.overlay.BreathingScreen
import com.sarangem.zenwell.ui.overlay.FullBlockScreen
import com.sarangem.zenwell.ui.overlay.TimerScreen
import com.sarangem.zenwell.ui.overlay.MathProblemScreen
import com.sarangem.zenwell.ui.overlay.MultiplicationTableScreen
import com.sarangem.zenwell.ui.overlay.PomodoroBlockScreen
import com.sarangem.zenwell.ui.overlay.TypingScreen
import com.sarangem.zenwell.utils.getAppNameFromPackageName
import kotlinx.coroutines.Job

data class ScheduleInfo(
    private val service: AppBlockerService,
    val schedule: Schedules,
    val appNamesList: List<AppNames>,
    val supervisorJob: Job
) {
    val appSet = appNamesList
        .map { it.title }
        .toSet()
    val viewsMap: Map<String, List<String>> = appNamesList
        .filter { it.viewTitle != null }
        .map { it.title }
        .groupBy { it.substringBefore(":id/") }

    val overlayWindowList: List<OverlayWindow> = appNamesList.map { app ->
        OverlayWindow(
            service = service,
            schedule = schedule,
            packageName = app.title,
            appName = app.viewTitle ?: getAppNameFromPackageName(service, app.title) ?: app.title,
            supervisorJob = supervisorJob,
            content = { onTimerEnd, onExit ->

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
                            Modifier.fillMaxSize(),
                            schedule.message,
                            schedule.showExit,
                            onExit
                        )

                        UnlockMethod.Timer -> TimerScreen(
                            Modifier.fillMaxSize(),
                            schedule.message,
                            schedule.timerDurationInSeconds,
                            schedule.requireManualUnlock,
                            onTimerEnd,
                            schedule.showExit,
                            onExit
                        )

                        UnlockMethod.Breathing -> BreathingScreen(
                            Modifier.fillMaxSize(),
                            schedule.message,
                            schedule.breathingCycleDurationInSeconds,
                            schedule.breathingCycleNumber,
                            schedule.requireManualUnlock,
                            onTimerEnd,
                            schedule.showExit,
                            onExit
                        )

                        UnlockMethod.MathProblem -> MathProblemScreen(
                            Modifier.fillMaxSize(),
                            schedule,
                            onTimerEnd,
                            onExit
                        )

                        UnlockMethod.MultiplicationTable -> MultiplicationTableScreen(
                            Modifier.fillMaxSize(),
                            schedule,
                            onTimerEnd,
                            onExit
                        )

                        UnlockMethod.Typing -> TypingScreen(
                            Modifier.fillMaxSize(),
                            schedule.message,
                            schedule.requireManualUnlock,
                            onTimerEnd,
                            schedule.showExit,
                            onExit
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