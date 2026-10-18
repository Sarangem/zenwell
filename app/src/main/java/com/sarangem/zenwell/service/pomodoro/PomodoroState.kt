/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.pomodoro

import androidx.compose.runtime.Immutable
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.utils.millisToString

enum class PomodoroStatus { IDLE, RUNNING, PAUSED }

@Immutable
data class PomodoroState(
    val schedule: Schedules = Schedules(),
    val status: PomodoroStatus = PomodoroStatus.IDLE,
    val isWorkTime: Boolean = true,
    val totalSegmentTime: Long = schedule.pomodoroWorkTimeInMinutes * 60 * 1000L,
    val remainingMillis: Long = schedule.pomodoroWorkTimeInMinutes * 60 * 1000L,
    val formattedTime: String = millisToString(remainingMillis),
    val sessionsCompleted: Int = 0
) {
    val progress: Float
        get() = if (totalSegmentTime > 0L) {
            ((totalSegmentTime - remainingMillis).toFloat() / totalSegmentTime).coerceIn(0f, 1f)
        } else 0f

}