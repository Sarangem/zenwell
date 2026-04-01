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