/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.pomodoro

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PomodoroService : Service() {

    @Inject lateinit var pomodoroManager: PomodoroManager
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        createPomodoroNotificationChannel(this)

        serviceScope.launch {
            pomodoroManager.state
                .distinctUntilChangedBy { it.formattedTime }
                .collect {
                    createPomodoroNotification(
                        context = this@PomodoroService,
                        state = it
                    )
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        when (intent.action) {
            PomodoroServiceActions.FRESH_START.name -> pomodoroManager.onSegmentStart()
            PomodoroServiceActions.PAUSE.name -> pomodoroManager.onSegmentPause()
            PomodoroServiceActions.RESUME.name -> pomodoroManager.onSegmentResume()
            PomodoroServiceActions.SKIP.name -> pomodoroManager.onSegmentEnd()
            PomodoroServiceActions.EXPIRE_SEGMENT.name -> pomodoroManager.onExpireSegment(intent)
            PomodoroServiceActions.FORCE_STOP.name -> pomodoroManager.onPomodoroEnd()
        }

        if (intent.action != PomodoroServiceActions.FORCE_STOP.name) startForeground(
            /* id = */ POMODORO_NOTIFICATION_ID,
            /* notification = */ createPomodoroNotification(
                context = this,
                state = pomodoroManager.state.value,
                playSound = (intent.action ==  PomodoroServiceActions.FRESH_START.name )||
                    (intent.action == PomodoroServiceActions.SKIP.name)
            )
        )

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        pomodoroManager.onPomodoroEnd()
        super.onDestroy()
    }
}

enum class PomodoroServiceActions {
    FRESH_START,
    PAUSE,
    RESUME,
    SKIP,
    EXPIRE_SEGMENT,
    FORCE_STOP
}