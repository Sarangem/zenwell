/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Locale

fun minutesToString(minutes: Int): String {
    return LocalTime
        .ofSecondOfDay(minutes * 60L)
        .format(
            DateTimeFormatter
                .ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
        )
}

fun getMinutesUntilTomorrow(): Int {
    val now = Calendar.getInstance()
    val midnight = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val millis = (midnight.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
    return (millis / 1000 / 60).toInt()
}