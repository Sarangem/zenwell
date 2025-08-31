package com.sarangem.zenwell.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sarangem.zenwell.MainActivity
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.NotificationChannels

fun areNotificationsEnabled(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) return
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return

    manager.createNotificationChannel(
        NotificationChannel(
            /* id = */ NotificationChannels.BlockNotification.name,
            /* name = */ context.getString(R.string.send_notification_before_closing),
            /* importance = */ NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.block_notification_description)
            setSound(null, null)
        }
    )

    manager.createNotificationChannel(
        NotificationChannel(
            /* id = */ NotificationChannels.PomodoroNotification.name,
            /* name = */ context.getString(R.string.pomodoro_timer),
            /* importance = */ NotificationManager.IMPORTANCE_DEFAULT
        )
    )
}

fun createNotification(
    message: String,
    id: Int,
    context: Context,
    notificationChannel: NotificationChannels
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return

    val intent = if(notificationChannel == NotificationChannels.PomodoroNotification) {
        PendingIntent.getActivity(
            /* context = */ context,
            /* requestCode = */ 0,
            /* intent = */ Intent(
                /* packageContext = */ context,
                /* cls = */ MainActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            /* flags = */ PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        null
    }

    val builder = NotificationCompat.Builder(context, notificationChannel.name)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(message)
        .setVibrate(LongArray(0))
        .setContentIntent(intent)
        .build()

    ServiceLogger.v { "Notification sent with message: $message" }
    manager.notify(id, builder)
}

fun deleteNotificationById(
    id: Int,
    context: Context
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return
    ServiceLogger.v { "Notification with ID $id cancelled." }
    manager.cancel(id)
}

fun deleteNotificationByChannel(
    channels: NotificationChannels,
    context: Context
){
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) return

    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return
    manager.activeNotifications.forEach { status ->
        if (status.notification.channelId == channels.name){
            manager.cancel(status.id)
        }
    }
}