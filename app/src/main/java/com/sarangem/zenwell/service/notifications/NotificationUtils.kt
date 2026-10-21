/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import com.sarangem.zenwell.R
import timber.log.Timber

fun createNotificationChannel(context: Context) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return

    manager.createNotificationChannel(
        NotificationChannel(
            /* id = */ APP_LOCK_NOTIFICATION_CHANNEL_ID,
            /* name = */ context.getString(R.string.notify_before_closing),
            /* importance = */ NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.app_lock_notification_description)
        }
    )
}

fun deleteNotificationById(
    id: Int,
    context: Context
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
    manager.cancel(id)
    Timber.v("Notification with ID $id cancelled.")
}