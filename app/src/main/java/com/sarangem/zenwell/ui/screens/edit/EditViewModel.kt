/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditUiState(
    val schedule: Schedules = Schedules(),
    val appNames: List<String>? = null,
    val viewsList: List<AppNames> = listOf(),
    val validationErrors: Set<ValidationError> = emptySet(),
)

class EditViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState = _uiState.asStateFlow()
    var pastAppList: List<String>? = null

    fun updateSchedule(schedule: Schedules) {
        _uiState.update {
            _uiState.value.copy(
                schedule = schedule,
                validationErrors = validateSchedule(schedule)
            )
        }
    }
    fun updateAppNames(appNames: List<String>) {
        _uiState.update {
            _uiState.value.copy(
                appNames = appNames
            )
        }
    }

    fun initialize(schedule: Schedules) {
        viewModelScope.launch(Dispatchers.IO) {
            val appNames = schedulesRepository.getAppNamesById(schedule.id).first()
            val viewsList = schedulesRepository.getAllApps().first().filter { it.viewTitle != null }
            _uiState.update {
                _uiState.value.copy(
                    schedule = schedule,
                    appNames = appNames.map { it.title },
                    viewsList = viewsList
                )
            }
            pastAppList = appNames.map { it.title }
        }
    }

    fun saveToDatabase() {
        val schedule = _uiState.value.schedule
        val appNames = _uiState.value.appNames
        val pastApps = pastAppList
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.saveToDatabase(
                schedule = schedule,
                appNames = appNames,
                pastAppList = pastApps
            )
            AppBlockerService.instance?.initializeRepository()
        }
    }

    fun deleteSchedule() {
        val schedule = _uiState.value.schedule
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.deleteSchedule(schedule)
            AppBlockerService.instance?.initializeRepository()
        }
    }
}