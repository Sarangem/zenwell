/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.domain.time

import android.content.Context
import android.text.format.DateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun minutesToString(
    minutes: Int,
    is24Hour: Boolean
): String {
    return LocalTime
        .ofSecondOfDay(minutes * 60L)
        .format(
            DateTimeFormatter
                .ofPattern(
                    /* pattern = */ if (is24Hour) "HH:mm" else "h:mm a",
                    /* locale = */ Locale.getDefault()
                )
            )
}

fun is24Hour(context: Context): Boolean = DateFormat.is24HourFormat(context)
fun millisToString(remainingMillis: Long): String {
    val totalSeconds = remainingMillis.coerceAtLeast(0L) / 1000L
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, (totalSeconds % 3600) / 60, totalSeconds % 60)
    } else {
        "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
    }
}