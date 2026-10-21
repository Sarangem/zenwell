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
import com.sarangem.zenwell.service.AppLockerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val schedulesRepository: SchedulesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState = _uiState.asStateFlow()
    var pastAppList: List<AppNames>? = null

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
            val selectedAppsList = schedulesRepository.getAppNamesById(schedule.id).first()
            val viewsList = schedulesRepository.getAllApps().first().filter { it.viewTitle != null }
            _uiState.update {
                _uiState.value.copy(
                    schedule = schedule,
                    selectedAppsList = selectedAppsList,
                    viewsList = viewsList
                )
            }
            pastAppList = selectedAppsList
        }
    }

    fun saveToDatabase() {
        val schedule = _uiState.value.schedule
        val selectedAppsList = _uiState.value.selectedAppsList
        val pastApps = pastAppList
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.saveToDatabase(
                schedule = schedule,
                selectedAppsList = selectedAppsList,
                pastAppList = pastApps
            )
            AppLockerService.instance?.initializeRepository()
        }
    }

    fun deleteSchedule() {
        val schedule = _uiState.value.schedule
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.deleteSchedule(schedule)
            AppLockerService.instance?.initializeRepository()
        }
    }
}