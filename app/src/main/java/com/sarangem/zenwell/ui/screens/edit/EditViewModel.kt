/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.data.repository.SchedulesRepository
import com.sarangem.zenwell.model.AppNames
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.service.applock.AppLockerService
import com.sarangem.zenwell.service.pomodoro.PomodoroManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditUiState(
    val schedule: Schedules = Schedules(),
    val selectedAppsList: List<AppNames>? = null,
    val viewsList: List<AppNames> = listOf(),
    val validationErrors: Set<ValidationError> = emptySet(),
)

@HiltViewModel
class EditViewModel @Inject constructor(
    private val schedulesRepository: SchedulesRepository,
    private val pomodoroManager: PomodoroManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState = _uiState.asStateFlow()
    var pastAppList: List<AppNames>? = null
    var pastSchedule: Schedules? = null

    fun updateSchedule(schedule: Schedules) {
        _uiState.update {
            it.copy(
                schedule = schedule,
                validationErrors = validateSchedule(schedule)
            )
        }
    }
    fun updateSelectedAppsList(selectedAppsList: List<AppNames>) {
        _uiState.update { it.copy(selectedAppsList = selectedAppsList) }
    }

    fun initialize(schedule: Schedules) {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedAppsList = schedulesRepository.getAppNamesById(schedule.id)
            val viewsList = schedulesRepository.getAllApps().filter { it.viewTitle != null }
            _uiState.update {
                it.copy(
                    schedule = schedule,
                    selectedAppsList = selectedAppsList,
                    viewsList = viewsList
                )
            }
            pastAppList = selectedAppsList
            if (schedule.isPomodoro) pastSchedule = schedule
        }
    }

    fun saveToDatabase() {
        val schedule = uiState.value.schedule
        val selectedAppsList = uiState.value.selectedAppsList
        val pastApps = pastAppList
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.saveToDatabase(
                schedule = schedule,
                selectedAppsList = selectedAppsList,
                pastAppList = pastApps
            )
            AppLockerService.instance?.initializeRepository()
            restartPomodoroService()
        }
    }

    private fun restartPomodoroService() {
        if (pastSchedule == null) return
        if(
            pomodoroManager.state.value.schedule.id == uiState.value.schedule.id &&
            listOf(
                Schedules::pomodoroWorkTimeInMinutes,
                Schedules::pomodoroRestTimeInMinutes,
                Schedules::pomodoroSessionNumber,
                Schedules::showPauseInWorkTime,
                Schedules::showPauseInRestTime,
                Schedules::showSkipInWorkTime,
                Schedules::showSkipInRestTime
            ).any { it(pastSchedule!!) != it(uiState.value.schedule) }
        ) pomodoroManager.onPomodoroEnd()
    }

    fun deleteSchedule() {
        val schedule = uiState.value.schedule
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.deleteSchedule(schedule)
            AppLockerService.instance?.initializeRepository()
        }
    }
}