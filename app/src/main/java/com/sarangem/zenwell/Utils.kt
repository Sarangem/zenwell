package com.sarangem.zenwell

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.sarangem.zenwell.service.AppBlockerService
import java.util.Calendar

// -- TIME -- //

fun getCurrentTimeInMinutes(): Int {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return (hour * 60) + minute
}

fun minutesToString(num: Int): String {

    var hours: Int = num / 60
    hours = if (hours <= 12) hours else (hours - 12)
    hours = if (hours == 0) 12 else hours
    val hourString = if (hours < 10) "0$hours" else hours.toString()

    val minutes: Int = num % 60
    val minuteString = if (minutes < 10) "0$minutes" else minutes.toString()

    return "$hourString:$minuteString"
}

fun getAmPm(num: Int): String {
    val hours: Int = num / 60
    return if (hours < 12) "AM" else "PM"
}

@OptIn(ExperimentalMaterial3Api::class)
fun convertToTimePickerState(timeInMinutes: Int): TimePickerState {
    return TimePickerState(
        initialHour = (timeInMinutes / 60).toInt(),
        initialMinute = (timeInMinutes % 60).toInt(),
        is24Hour = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.toMinutes(): Int {
    return (this.hour * 60) + (this.minute)
}


// -- PERMISSIONS -- //

fun checkAccessibilityServicePermission(): Boolean {
    return (AppBlockerService.instance != null)
}