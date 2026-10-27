/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sarangem.zenwell.MainActivity
import com.sarangem.zenwell.R
import com.sarangem.zenwell.service.pomodoro.*

const val POMODORO_NOTIFICATION_CHANNEL_ID = "PomodoroNotification"
const val POMODORO_NOTIFICATION_ID = 1

fun createPomodoroNotification(
    context: Context,
    state: PomodoroState,
    setSilent: Boolean = true,
    workSegmentColor: Int,
    restSegmentColor: Int
): Notification {
    val isPaused = state.status == PomodoroStatus.PAUSED

    val builder = NotificationCompat.Builder(context, POMODORO_NOTIFICATION_CHANNEL_ID)
        .setSilent(if (isPaused) true else setSilent)
        .setContentTitle(state.formattedTime)
        .setShortCriticalText(state.formattedTime)
        .setOngoing(!isPaused)
        .setRequestPromotedOngoing(!isPaused)
        .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setWhen(
            if (!isPaused) {
                0L
            } else {
                System.currentTimeMillis() + state.remainingMillis
            }
        )
        .setShowWhen(false)
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
        .addPomodoroActions(context, state)

    if (state.isWorkTime) {
        builder
            .setSmallIcon(R.drawable.filled_work)
            .setColor(workSegmentColor)
            .setSubText(context.getString(R.string.work_time_notification))
    } else {
        builder
            .setSmallIcon(R.drawable.outlined_local_cafe)
            .setColor(restSegmentColor)
            .setSubText(context.getString(R.string.rest_time_notification))
    }

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        builder.setStyle(
            NotificationCompat.ProgressStyle()
                .addAllSegments(state, workSegmentColor, restSegmentColor)
                .setProgress(getNotificationProgress(state))
        )
    } else {
        builder.setProgress(
            /* max = */ state.totalSegmentTime.toInt(),
            /* progress = */ (state.totalSegmentTime - state.remainingMillis).toInt(),
            /* indeterminate = */ false
        )
    }

    val notification = builder.build()
    val manager = context.getSystemService(NOTIFICATION_SERVICE) as? NotificationManager
    manager?.notify(POMODORO_NOTIFICATION_ID, notification)
    return notification
}

private fun NotificationCompat.Builder.addPomodoroActions(
    context: Context,
    state: PomodoroState
): NotificationCompat.Builder {
    val isPaused = state.status == PomodoroStatus.PAUSED
    val canPause = if (state.isWorkTime) state.schedule.showPauseInWorkTime else state.schedule.showPauseInRestTime
    val canSkip = if (state.isWorkTime) state.schedule.showSkipInWorkTime else state.schedule.showSkipInRestTime

    if (canPause) {
        val toggleAction = if (isPaused) PomodoroServiceActions.RESUME else PomodoroServiceActions.PAUSE
        addAction(
            /* icon = */ if (isPaused) R.drawable.filled_play_arrow else R.drawable.filled_pause,
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
        addAction(
            /* icon = */ R.drawable.filled_fast_forward,
            /* title = */ context.getString(R.string.skip),
            /* intent = */  PendingIntent.getService(
                /* context = */ context,
                /* requestCode = */ PomodoroServiceActions.SKIP.ordinal,
                /* intent = */ Intent(context, PomodoroService::class.java).apply { action = PomodoroServiceActions.SKIP.name },
                /* flags = */ PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    return this
}

private fun NotificationCompat.ProgressStyle.addAllSegments(
    state: PomodoroState,
    workSegmentColor: Int,
    restSegmentColor: Int
): NotificationCompat.ProgressStyle {
    repeat(state.schedule.pomodoroSessionNumber.coerceAtLeast(1)) {
        addProgressSegment(
            NotificationCompat.ProgressStyle
                .Segment(state.schedule.pomodoroWorkTimeInMinutes * 60 * 1000)
                .setColor(workSegmentColor)
        )
        addProgressSegment(
            NotificationCompat.ProgressStyle
                .Segment(state.schedule.pomodoroRestTimeInMinutes * 60 * 1000)
                .setColor(restSegmentColor)
        )
    }
    return this
}

private fun getNotificationProgress(state: PomodoroState): Int {
    var totalSegmentMin = state.schedule.pomodoroWorkTimeInMinutes
    if (state.schedule.pomodoroSessionNumber != 0) {
        totalSegmentMin += (
            state.schedule.pomodoroWorkTimeInMinutes + state.schedule.pomodoroRestTimeInMinutes
        ) * state.sessionsCompleted
    }
    if (!state.isWorkTime) totalSegmentMin += state.schedule.pomodoroRestTimeInMinutes
    return (totalSegmentMin * 60 * 1000L - state.remainingMillis).toInt()
}
