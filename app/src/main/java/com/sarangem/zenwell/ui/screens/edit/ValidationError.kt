/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit

import com.sarangem.zenwell.database.tables.Schedules

enum class ValidationError {
    ActiveTime,
    NotificationTime,
    PomodoroSessionNumber,
    MathEquationNumOperands,
}

fun validateSchedule(schedule: Schedules): Set<ValidationError> = buildSet {

    if (schedule.startTimeInMinutes >= schedule.endTimeInMinutes) {
        add(ValidationError.ActiveTime)
    }

    if (schedule.usageSessionDurationInMinutes <= schedule.notificationTimeInMinutes) {
        add(ValidationError.NotificationTime)
    }
    if (schedule.pomodoroSessionNumber <= 0) {
        add(ValidationError.PomodoroSessionNumber)
    }
    if (schedule.mathEquationNumOperands < 2) {
        add(ValidationError.MathEquationNumOperands)
    }
}