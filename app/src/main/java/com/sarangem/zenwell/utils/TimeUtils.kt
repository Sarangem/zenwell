package com.sarangem.zenwell.utils

import android.content.Context
import android.text.format.DateFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.sarangem.zenwell.R
import java.util.Calendar
import java.util.Locale
import kotlin.math.pow

fun getCurrentTimeInMinutes(): Int {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return (hour * 60) + minute
}

fun is24Hour(context: Context): Boolean {
    return DateFormat.is24HourFormat(context)
}

fun minutesToString(num: Int, context: Context): String {
    val is24Hour = is24Hour(context)

    var hours: Int = num / 60
    if(is24Hour) {
        hours = if (hours <= 12) hours else (hours - 12)
        hours = if (hours == 0) 12 else hours
    }
    val hourString = if (hours < 10) "0$hours" else hours.toString()

    val minutes: Int = num % 60
    val minuteString = if (minutes < 10) "0$minutes" else minutes.toString()

    return "$hourString:$minuteString"
}

fun secondsToString(num: Long): String{
    val minutes = num / 60
    val minuteString = if (minutes < 10) "0$minutes" else minutes.toString()
    val seconds = num % 60
    val secondString = if (seconds < 10) "0$seconds" else seconds.toString()
    return "$minuteString:$secondString"
}

fun getAmPm(num: Int): String {
    val hours: Int = num / 60
    return if (hours < 12) "AM" else "PM"
}

@OptIn(ExperimentalMaterial3Api::class)
fun convertToTimePickerState(timeInMinutes: Int, context: Context): TimePickerState {
    return TimePickerState(
        initialHour = (timeInMinutes / 60),
        initialMinute = (timeInMinutes % 60),
        is24Hour = DateFormat.is24HourFormat(context)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.toMinutes(): Int {
    return (this.hour * 60) + (this.minute)
}

fun getWeekDays(): List<Pair<Int, Int>> {
    val calendar = Calendar.getInstance(Locale.getDefault())
    val firstDay = calendar.firstDayOfWeek

    val daysList = listOf(
        Calendar.SUNDAY to R.string.sunday_abbr,
        Calendar.MONDAY to R.string.monday_abbr,
        Calendar.TUESDAY to R.string.tuesday_abbr,
        Calendar.WEDNESDAY to R.string.wednesday_abbr,
        Calendar.THURSDAY to R.string.tuesday_abbr,
        Calendar.FRIDAY to R.string.friday_abbr,
        Calendar.SATURDAY to R.string.sunday_abbr,
    )


    val startIndex = daysList.indexOfFirst { it.first == firstDay }
    return if (startIndex == -1) daysList
    else daysList.drop(startIndex) + daysList.take(startIndex)
}

fun checkIfScheduleEnabled(weekDays: Int): Boolean {
    val calendar = Calendar.getInstance(Locale.getDefault())
    val today = calendar.get(Calendar.DAY_OF_WEEK)
    return checkIfScheduleEnabled(weekDays, today)
}

fun checkIfScheduleEnabled(weekDays: Int, day: Int): Boolean {
    return ((weekDays / 10.0.pow(day).toInt()) % 10) == 1
}