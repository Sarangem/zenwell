/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import com.sarangem.zenwell.service.PomodoroWindow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PomodoroViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    var pomodoroWindow: PomodoroWindow? = null

    fun startObserving(schedule: Schedules) {
        _uiState.update { it.copy(schedule = schedule) }
        pomodoroWindow = AppBlockerService.instance?.getPomodoroWindow(_uiState.value.schedule.id)
        if (pomodoroWindow == null) {
            _uiState.update { it.copy(isServiceRunning = false) }
            return
        }
        _uiState.update { it.copy(isServiceRunning = true) }
        viewModelScope.launch {
            while (pomodoroWindow!!.isActive || pomodoroWindow!!.isPaused) {
                _uiState.update { currentState ->
                    currentState.copy(
                        elapsedTime = pomodoroWindow!!.getElapsedTimeInSeconds(),
                        formattedTime = pomodoroWindow!!.getFormattedTime(),
                        isWorkTime = pomodoroWindow!!.isWorkTime,
                        isPaused = pomodoroWindow!!.isPaused,
                        sessionsLeft = pomodoroWindow!!.sessions,
                        isCompleted = false
                    )
                }
                delay(500L)
            }
            _uiState.update { it.copy(isCompleted = true) }
        }
    }

    fun onEnd() = pomodoroWindow?.onPomodoroEnd()
    fun onPauseOrResume() {
        if (_uiState.value.isPaused) {
            pomodoroWindow?.onPomodoroStart()
        } else {
            pomodoroWindow?.onPomodoroPause()
        }
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }
    fun onSkip() {
        pomodoroWindow?.onPomodoroSkip()
        _uiState.update {
            it.copy(
                isWorkTime = !it.isWorkTime,
                isPaused = false
            )
        }
    }
}