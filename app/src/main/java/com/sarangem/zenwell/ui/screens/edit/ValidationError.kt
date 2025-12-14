package com.sarangem.zenwell.ui.screens.edit

import com.sarangem.zenwell.database.tables.Schedules

enum class ValidationError {
    RunningTime,
    NotificationTime,
    PomodoroSessionNumber,
    MathEquationNumOperands,
    MathOperators,
}

fun validateSchedule(schedule: Schedules): Set<ValidationError> = buildSet {

    if (schedule.startTimeInMinutes >= schedule.endTimeInMinutes) {
        add(ValidationError.RunningTime)
    }

    if (schedule.openTimeInMinutes <= schedule.notificationTimeInMinutes) {
        add(ValidationError.NotificationTime)
    }
    if (schedule.pomodoroSessionNumber <= 0) {
        add(ValidationError.PomodoroSessionNumber)
    }
    if (schedule.mathEquationNumOperands < 2) {
        add(ValidationError.MathEquationNumOperands)
    }

    if (schedule.allowedMathOperators.isEmpty()) {
        add(ValidationError.MathOperators)
    }
}