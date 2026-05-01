/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.pomodoro

import com.sarangem.zenwell.database.tables.Schedules

data class PomodoroUiState(
    val schedule: Schedules = Schedules(),
    val elapsedTime: Long = 0L,
    val formattedTime: String = "",
    val isWorkTime: Boolean = true,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val sessionsLeft: Int = schedule.pomodoroSessionNumber,
    val isServiceRunning: Boolean = false
){
    val segmentTime = if(isWorkTime) {
        schedule.pomodoroWorkTimeInMinutes * 60
    } else schedule.pomodoroRestTimeInMinutes * 60
}