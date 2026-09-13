/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.utils

import android.content.Context
import android.text.format.DateFormat
import java.util.Calendar

fun getCurrentTimeInMinutes(calendar: Calendar = Calendar.getInstance()): Int {
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return (hour * 60) + minute
}

fun minutesToString(num: Int, context: Context): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, num / 60)
        set(Calendar.MINUTE, num % 60)
    }
    return DateFormat.getTimeFormat(context).format(calendar.time)
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