/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.pomodoro

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import com.sarangem.zenwell.utils.millisToString
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Singleton
class PomodoroManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val expiryAlarmScheduler: ExpiryAlarmScheduler
) {
    private val _state = MutableStateFlow(PomodoroState())
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private var timerJob: Job? = null

    private var startTime = 0L
    private var timeWhenPaused = 0L
    private var pausedFor = 0L

    fun loadSchedule(schedule: Schedules) = _state.update { PomodoroState(schedule = schedule) }

    fun onSegmentStart() {
        startTime = SystemClock.elapsedRealtime()
        pausedFor = 0L
        timeWhenPaused = 0L
        onSegmentContinue()
    }

    private fun onSegmentContinue() {
        _state.update {
            it.copy(
                status = PomodoroStatus.RUNNING,
                totalSegmentTime = if (it.isWorkTime) {
                    it.schedule.pomodoroWorkTimeInMinutes
                } else {
                    it.schedule.pomodoroRestTimeInMinutes
                } * 60 * 1000L
            )
        }
        removeOverlayWindow()
        expiryAlarmScheduler.set(
            triggerAt = startTime + state.value.totalSegmentTime + pausedFor,
            sessionsCompletedTillThen = state.value.sessionsCompleted,
            isWorkSegmentToExpire = state.value.isWorkTime
        )
        timerJob?.cancel()
        timerJob = scope.launch {
            while (true) {
                val remaining = (state.value.totalSegmentTime + startTime + pausedFor) - SystemClock.elapsedRealtime()
                _state.update {
                    it.copy(
                        remainingMillis = remaining,
                        formattedTime = if (it.remainingMillis/1000 != remaining/1000) {
                            millisToString(remaining)
                        } else {
                            it.formattedTime
                        }
                    )
                }
                if (remaining <= 0L) {
                    onSegmentEnd()
                    break
                }
                delay(0.2.seconds)
            }
        }
    }

    fun onSegmentEnd() {
        _state.update {
            it.copy(
                sessionsCompleted = if(!it.isWorkTime) it.sessionsCompleted + 1 else it.sessionsCompleted,
                isWorkTime = !it.isWorkTime
            )
        }
        if (state.value.sessionsCompleted >= state.value.schedule.pomodoroSessionNumber) {
            onPomodoroEnd()
        } else {
            expiryAlarmScheduler.delete()
            onSegmentStart()
        }
    }

    fun onSegmentPause() {
        timeWhenPaused = SystemClock.elapsedRealtime()
        timerJob?.cancel()
        val remaining = (state.value.totalSegmentTime + startTime + pausedFor) - timeWhenPaused
        _state.update {
            it.copy(
                status = PomodoroStatus.PAUSED,
                remainingMillis = remaining,
                formattedTime = millisToString(remaining)
            )
        }
        removeOverlayWindow()
        expiryAlarmScheduler.delete()
    }

    fun onSegmentResume() {
        pausedFor += SystemClock.elapsedRealtime() - timeWhenPaused
        timeWhenPaused = 0L
        onSegmentContinue()
    }
    
    fun onExpireSegment(intent: Intent) {
        val sessionsCompletedTillThen = intent.getIntExtra(SESSIONS_COMPLETED_TILL_THEN, -1)
        if (sessionsCompletedTillThen == -1 || !intent.hasExtra(IS_WORK_SEGMENT_TO_EXPIRE)) return
        val isWorkSegmentToExpire = intent.getBooleanExtra(IS_WORK_SEGMENT_TO_EXPIRE, false)
        if (state.value.status == PomodoroStatus.RUNNING &&
            state.value.isWorkTime == isWorkSegmentToExpire &&
            state.value.sessionsCompleted == sessionsCompletedTillThen
        ) onSegmentEnd()
    }

    fun onPomodoroEnd() {
        if (state.value.status == PomodoroStatus.IDLE) return
        timerJob?.cancel()
        timerJob = null
        deletePomodoroNotification(context)
        _state.update { PomodoroState() }
        removeOverlayWindow()
        timeWhenPaused = 0L
        pausedFor = 0L
        expiryAlarmScheduler.delete()
        context.stopService(Intent(context, PomodoroService::class.java))
    }

    private fun removeOverlayWindow() {
        AppBlockerService.instance?.onAccessibilityEvent(null)
        if(!_state.value.isWorkTime) AppBlockerService.instance
            ?.openedWindows
            ?.firstOrNull { it.schedule.id == _state.value.schedule.id }
            ?.close()
    }
}