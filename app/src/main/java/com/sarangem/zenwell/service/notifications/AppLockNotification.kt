/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.notifications

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.sarangem.zenwell.R
import timber.log.Timber

const val APP_LOCK_NOTIFICATION_CHANNEL_ID = "BlockNotification"

fun createAppLockNotification(
    message: String,
    id: Int,
    context: Context,
) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
    val builder = NotificationCompat.Builder(context, APP_LOCK_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(message)
        .setVibrate(LongArray(0))
        .setSound(null)
        .build()
    Timber.v("Notification sent with message: $message")
    manager.notify(id, builder)
}