package com.sarangem.zenwell

import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.content.Context.POWER_SERVICE
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.core.app.NotificationCompat
import java.util.Calendar

// -- NOTIFICATIONS -- //
fun makeVerboseServiceNotification(message: String, context: Context): Notification {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val channel = NotificationChannel(
            /* id = */ VERBOSE_NOTIFICATION_CHANNEL_ID,
            /* name = */ context.getString(R.string.verbose_notification_channel_name),
            /* importance = */ NotificationManager.IMPORTANCE_LOW
        )
        channel.description = context.getString(R.string.verbose_notification_channel_description)

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)

    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, VERBOSE_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(message)
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


// -- PERMISSIONS -- //

fun checkSystemAlertWindowPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(context)
    } else {
        true
    }
}

fun checkPackageUsageStatsPermission(context: Context): Boolean {
    val appOpsManager = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOpsManager.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
    } else {
        @Suppress("DEPRECATION")
        appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
    }
    return (mode == AppOpsManager.MODE_ALLOWED)
}

fun isIgnoringBatteryOptimisations(context: Context): Boolean {
    val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
    } else {
        true
    }
}