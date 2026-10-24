/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.model.TaskItem
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.service.pomodoro.PomodoroManager

@Composable
fun OverlayScreen(
    schedule: Schedules,
    pomodoroManager: PomodoroManager,
    tasksList: List<TaskItem>,
    updateTasksList: (List<TaskItem>) -> Unit,
    onTimerEnd: () -> Unit = {},
    onExit: () -> Unit = {}
) {
    if (schedule.isPomodoro) {

        PomodoroBlockScreen(
            modifier = Modifier.fillMaxSize(),
            message = schedule.message,
            pomodoroManager = pomodoroManager,
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
                updateTasksList,
                schedule.requireManualUnlock,
                onTimerEnd,
                schedule.showExit,
                onExit
            )

        }
    }
}