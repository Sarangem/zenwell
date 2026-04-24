/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit

import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.model.MathOperators
import com.sarangem.zenwell.model.UnlockMethod

enum class ValidationError {
    Default,
    ActiveTime,
    NotificationTime,
    MathEquationNumOperands,
}

fun validateSchedule(s: Schedules): Set<ValidationError> = with(s) {
    if (isPomodoro) {
        return if (pomodoroWorkTimeInMinutes == 0 || pomodoroRestTimeInMinutes == 0 || pomodoroSessionNumber == 0) {
            setOf(ValidationError.Default)
        } else emptySet()
    }
    val errors: MutableSet<ValidationError> = mutableSetOf()
    if (isActive && startTimeInMinutes >= endTimeInMinutes) errors.add(ValidationError.ActiveTime)
    if (unlockMethod != UnlockMethod.StrictBlock) {
        if (usageSessionDurationInMinutes == 0) errors.add(ValidationError.Default)
        if (usageSessionDurationInMinutes <= notificationTimeInMinutes) errors.add(ValidationError.NotificationTime)
    }
    when (unlockMethod) {
        UnlockMethod.Timer -> if (timerDurationInSeconds == 0) errors.add(ValidationError.Default)
        UnlockMethod.Breathing -> if (breathingCycleDurationInSeconds == 0 || breathingCycleNumber == 0) errors.add(ValidationError.Default)
        UnlockMethod.MathProblem -> {
            if (mathEquationNumOperands < 2) errors.add(ValidationError.MathEquationNumOperands)
            if (mathEquationMinNumber > mathEquationMaxNumber ||
                (MathOperators.MULTIPLICATION in allowedMathOperators && mathEquationMinNumberInMultiplication > mathEquationMaxNumberInMultiplication)
            ) errors.add(ValidationError.Default)
        }
        UnlockMethod.MultiplicationTable -> if (multiplierMinNum > multiplierMaxNum || multiplicationMinNum > multiplicationMaxNum) errors.add(ValidationError.Default)
        else -> {}
    }
    return errors
}