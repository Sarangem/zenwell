/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sarangem.zenwell.MainActivity
import com.sarangem.zenwell.R

const val BlockNotification = "BlockNotification"
const val PomodoroNotification = "PomodoroNotification"

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) return
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return

    manager.createNotificationChannel(
        NotificationChannel(
            /* id = */ BlockNotification,
            /* name = */ context.getString(R.string.notify_before_closing),
            /* importance = */ NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.block_notification_description)
        }
    )

    manager.createNotificationChannel(
        NotificationChannel(
            /* id = */ PomodoroNotification,
            /* name = */ context.getString(R.string.pomodoro_timer),
            /* importance = */ NotificationManager.IMPORTANCE_DEFAULT
        )
    )
}

fun createPomodoroNotification(
    time: String,
    id: Int,
    isWork: Boolean,
    context: Context,
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return
    val intent = PendingIntent.getActivity(
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
    val builder = NotificationCompat.Builder(context, PomodoroNotification)
        .setSmallIcon(if(isWork) R.drawable.filled_work else R.drawable.outlined_local_cafe)
        .setContentTitle(time)
        .setContentText(if(isWork) context.getString(R.string.work_time_notification) else context.getString(R.string.rest_time_notification))
        .setOnlyAlertOnce(true)
        .setOngoing(true)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(intent)
        .build()
    ServiceLogger.v { "Notification sent with $time" }
    manager.notify(id, builder)
}

fun createBlockNotification(
    message: String,
    id: Int,
    context: Context,
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return
    val builder = NotificationCompat.Builder(context, BlockNotification)
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(message)
        .setVibrate(LongArray(0))
        .setSound(null)
        .build()
    ServiceLogger.v { "Notification sent with message: $message" }
    manager.notify(id, builder)
}

fun deleteNotificationById(
    id: Int,
    context: Context
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return
    manager.cancel(id)
    ServiceLogger.v { "Notification with ID $id cancelled." }
}
fun deleteAllNotificationChannel(context: Context){
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) return
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager? ?: return
    manager.deleteNotificationChannel(BlockNotification)
    manager.deleteNotificationChannel(PomodoroNotification)
}