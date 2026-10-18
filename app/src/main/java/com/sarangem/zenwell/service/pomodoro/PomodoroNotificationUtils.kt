/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.pomodoro

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.sarangem.zenwell.MainActivity
import com.sarangem.zenwell.R

const val POMODORO_NOTIFICATION_CHANNEL_ID = "POMODORO_NOTIFICATION_CHANNEL_ID"
const val POMODORO_NOTIFICATION_ID = 1

fun createPomodoroNotificationChannel(context: Context) {
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
    manager.createNotificationChannel(
        NotificationChannel(
            /* id = */ POMODORO_NOTIFICATION_CHANNEL_ID,
            /* name = */ context.getString(R.string.pomodoro_timer),
            /* importance = */ NotificationManager.IMPORTANCE_DEFAULT
        )
    )
}

fun createPomodoroNotification(
    context: Context,
    state: PomodoroState,
    playSound: Boolean = false,
): Notification {
    val isPaused = state.status == PomodoroStatus.PAUSED
    val totalMillis = state.totalSegmentTime.toInt()
    val elapsedMillis = (state.totalSegmentTime - state.remainingMillis).coerceAtLeast(0L).toInt()
    val canPause = if (state.isWorkTime) state.schedule.showPauseInWorkTime else state.schedule.showPauseInRestTime
    val canSkip = if (state.isWorkTime) state.schedule.showSkipInWorkTime else state.schedule.showSkipInRestTime

    val builder = NotificationCompat.Builder(context, POMODORO_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(if (state.isWorkTime) R.drawable.filled_work else R.drawable.outlined_local_cafe)
        .setContentTitle(
            if (state.isWorkTime) context.getString(R.string.work_time_notification)
            else context.getString(R.string.rest_time_notification)
        )
        .setContentText(state.formattedTime)
        .setProgress(totalMillis, elapsedMillis, false)
        .setWhen(System.currentTimeMillis() + state.remainingMillis)
        .setUsesChronometer(!isPaused)
        .setChronometerCountDown(true)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setOnlyAlertOnce(!playSound)
        .setOngoing(!playSound && !isPaused)
        .setSilent(!playSound)
        .setCategory(NotificationCompat.CATEGORY_PROGRESS)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentIntent(
            PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */ 0,
                /* intent = */ Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                /* flags = */ PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

    if (canPause) {
        val toggleAction = if (isPaused) PomodoroServiceActions.RESUME else PomodoroServiceActions.PAUSE
        builder.addAction(
            /* icon = */ 0,
            /* title = */ if (isPaused) context.getString(R.string.resume) else context.getString(R.string.pause),
            /* intent = */ PendingIntent.getService(
                /* context = */ context,
                /* requestCode = */ toggleAction.ordinal,
                /* intent = */ Intent(context, PomodoroService::class.java).apply { action = toggleAction.name },
                /* flags = */ PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    if (canSkip) {
        val skipPendingIntent = PendingIntent.getService(
            /* context = */ context,
            /* requestCode = */ PomodoroServiceActions.SKIP.ordinal,
            /* intent = */ Intent(context, PomodoroService::class.java).apply { action = PomodoroServiceActions.SKIP.name },
            /* flags = */ PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.addAction(0, context.getString(R.string.skip), skipPendingIntent)
    }

    val notification = builder.build()
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
    manager?.notify(POMODORO_NOTIFICATION_ID, notification)
    return notification
}