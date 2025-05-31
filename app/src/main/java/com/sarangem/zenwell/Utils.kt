package com.sarangem.zenwell

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.core.app.NotificationCompat
import java.util.Calendar

// -- NOTIFICATIONS -- //
fun makeVerboseServiceNotification(message: String = "", context: Context): Notification {

    val channelName: CharSequence = "Verbose Background Service Notifications"
    val channelDescription = "Shows notifications whenever Zenwell runs in background"
    val notificationTitle = "Zenwell running..."
    val channelId = "VERBOSE_NOTIFICATION"

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = channelName
        val description = channelDescription
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(notificationTitle)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setVibrate(LongArray(0))
        .build()

    return builder
}


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