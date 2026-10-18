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
import com.sarangem.zenwell.ui.overlay.TaskItem
import com.sarangem.zenwell.ui.overlay.TasksScreen
import com.sarangem.zenwell.ui.overlay.TypingScreen
import com.sarangem.zenwell.utils.getAppNameFromPackageName
import kotlinx.coroutines.Job

data class ScheduleInfo(
    private val service: AppBlockerService,
    val schedule: Schedules,
    val appNamesList: List<AppNames>,
    val supervisorJob: Job
) {
    val appsByPackage: Map<String, List<AppNames>> = appNamesList.groupBy { it.packageName }

    val overlayWindowList: List<OverlayWindow> = appNamesList.map { app ->
        OverlayWindow(
            service = service,
            schedule = schedule,
            targetApp = app.copy(
                viewTitle = app.viewTitle ?: getAppNameFromPackageName(service, app.packageName) ?: app.packageName
            ),
            supervisorJob = supervisorJob,
            content = { onTimerEnd, onExit ->

                if (schedule.isPomodoro) {

                    PomodoroBlockScreen(
                        modifier = Modifier.fillMaxSize(),
                        message = schedule.message,
                        pomodoroManager = service.pomodoroManager,
                        showExit = schedule.showExit,
                        onExit = onExit
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

                        UnlockMethod.Tasks -> TasksScreen(
                            Modifier.fillMaxSize(),
                            schedule.message,
                            tasksList,
                            { tasksList = it },
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
    
    // only used for UnlockMethod.Tasks
    var tasksList = schedule.tasks.lines()
        .filter { it.isNotBlank() }
        .mapIndexed { index, line -> TaskItem(id = index, text = line.trim()) }
}