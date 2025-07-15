package com.sarangem.zenwell.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sarangem.zenwell.R

const val BLOCK_NOTIFICATION = "Block Notification"

fun areNotificationsEnabled(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

fun createNotification(
    scheduleName: String,
    scheduleId: Int,
    appName: String,
    context: Context
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return

    val message =
        scheduleName + context.getString(R.string.block_notification_message) + getAppNameFromPackageName(
            context,
            appName
        )

    val builder = NotificationCompat.Builder(context, BLOCK_NOTIFICATION)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(message)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setVibrate(LongArray(0))
        .setSound(null)
        .build()

    manager.notify(scheduleId, builder)
    ServiceLogger.v { "Notification sent with message: $message" }
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) return
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return

    val channel = NotificationChannel(
        /* id = */ BLOCK_NOTIFICATION,
        /* name = */ context.getString(R.string.send_notification_before_closing),
        /* importance = */ NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = context.getString(R.string.block_notification_description)
        setSound(null, null)
    }

    manager.createNotificationChannel(channel)
}