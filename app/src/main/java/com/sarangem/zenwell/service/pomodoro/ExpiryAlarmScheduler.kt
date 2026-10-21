/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.pomodoro

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import timber.log.Timber

const val SESSIONS_COMPLETED_TILL_THEN = "SESSIONS_COMPLETED_TILL_THEN"
const val IS_WORK_SEGMENT_TO_EXPIRE = "IS_WORK_SEGMENT_TO_EXPIRE"

@Singleton
class ExpiryAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    private fun pendingIntent(
        flags: Int,
        putExtras: (Intent.() -> Unit)? = null
    ): PendingIntent? {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroServiceActions.EXPIRE_SEGMENT.name
            putExtras?.invoke(this)
        }
        return PendingIntent.getForegroundService(
            context,
            0,
            intent,
            flags or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun set(
        triggerAt: Long,
        sessionsCompletedTillThen: Int,
        isWorkSegmentToExpire: Boolean
    ) {
        val am = alarmManager ?: return
        val pi = pendingIntent(PendingIntent.FLAG_UPDATE_CURRENT) {
            putExtra(SESSIONS_COMPLETED_TILL_THEN, sessionsCompletedTillThen)
            putExtra(IS_WORK_SEGMENT_TO_EXPIRE, isWorkSegmentToExpire)
        } ?: return
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                am.canScheduleExactAlarms()
            ) {
                am.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAt,
                    pi
                )
            } else {
                am.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerAt,
                    pi
                )
            }
        } catch (e: Exception) {
            Timber.e(e,"Exact alarm permission missing, falling back to inexact")
        }
    }

    fun delete() {
        val am = alarmManager ?: return
        pendingIntent(PendingIntent.FLAG_NO_CREATE)?.let { pi ->
            am.cancel(pi)
            pi.cancel()
        }
    }
}
