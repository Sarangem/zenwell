package com.sarangem.zenwell.ui.screens.focus

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

class FocusViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

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

data class FocusUiState(
    val schedule: Schedules = Schedules(),
    val elapsedTime: Long = 0L,
    val formattedTime: String = "",
    val isWorkTime: Boolean = true,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val isServiceRunning: Boolean = false
){
    val segmentTime = if(isWorkTime) schedule.pomodoroWorkTimeInMinutes * 60 else schedule.pomodoroRestTimeInMinutes * 60
}