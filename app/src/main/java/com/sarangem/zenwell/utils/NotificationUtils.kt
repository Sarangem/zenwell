/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.sarangem.zenwell.R

const val BLOCK_NOTIFICATION_CHANNEL_ID = "BLOCK_NOTIFICATION_CHANNEL_ID"

fun createNotificationChannel(context: Context) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return

    manager.createNotificationChannel(
        NotificationChannel(
            /* id = */ BLOCK_NOTIFICATION_CHANNEL_ID,
            /* name = */ context.getString(R.string.notify_before_closing),
            /* importance = */ NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.block_notification_description)
        }
    )
}

fun createBlockNotification(
    message: String,
    id: Int,
    context: Context,
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
    val builder = NotificationCompat.Builder(context, BLOCK_NOTIFICATION_CHANNEL_ID)
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
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
    manager.cancel(id)
    ServiceLogger.v { "Notification with ID $id cancelled." }
}

fun deleteAllNotificationChannel(context: Context) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
    manager.deleteNotificationChannel(BLOCK_NOTIFICATION_CHANNEL_ID)
}