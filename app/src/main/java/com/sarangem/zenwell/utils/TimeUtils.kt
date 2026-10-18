/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.utils

import android.os.SystemClock
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun minutesToString(minutes: Int): String =
    LocalTime
        .ofSecondOfDay(minutes * 60L)
        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault()))

fun getElapsedRealtimeUntilTomorrow(): Long {
    val now = ZonedDateTime.now()
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
    return SystemClock.elapsedRealtime() + Duration.between(now, midnight).toMillis()
}

fun millisToString(remainingMillis: Long): String {
    val totalSeconds = remainingMillis.coerceAtLeast(0L) / 1000L
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, (totalSeconds % 3600) / 60, totalSeconds % 60)
    } else {
        "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
    }
}