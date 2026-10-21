/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.repository.SchedulesRepository
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.model.UserPreferences
import com.sarangem.zenwell.service.AppLockerService
import com.sarangem.zenwell.service.pomodoro.PomodoroManager
import com.sarangem.zenwell.service.pomodoro.PomodoroService
import com.sarangem.zenwell.service.pomodoro.PomodoroServiceActions
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val schedulesRepository: SchedulesRepository,
    private val pomodoroManager: PomodoroManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    val pomodoroState = pomodoroManager.state
    
    init {
        viewModelScope.launch {
            combine(
                schedulesRepository.getAllSchedules(),
                schedulesRepository.getUserPreferences()
            ) { schedules, prefs ->
                _uiState.update { current ->
                    current.copy(
                        schedulesList = schedules.sortedBy { it.title.lowercase() },
                        userPreferences = prefs,
                        showAccessibilityPermissionRationale = AppLockerService.instance == null
                    )
                }
            }.collect()
        }
    }

    fun updateUiState(uiState: HomeUiState) { _uiState.update { uiState } }

    fun setAsExistingUser() {
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.upsertUserPreferences(
                _uiState.value.userPreferences.copy(firstEntry = false)
            )
        }
        _uiState.update { it.copy(userPreferences = _uiState.value.userPreferences.copy(firstEntry = false)) }
    }

    fun recheckPermission(){
        _uiState.update {
            it.copy(
                showAccessibilityPermissionRationale = AppLockerService.instance == null
            )
        }
    }

    suspend fun addNewSchedule(
        context: Context,
        isPomodoro: Boolean
    ): Schedules {
        val title =
            context.getString(R.string.schedule) + schedulesRepository.getSchedulesCount()
                .first()
                .plus(1)
        val newSchedule = Schedules(
            id = schedulesRepository.addNewSchedule(
                Schedules(
                    title = title,
                    isPomodoro = isPomodoro
                )
            ),
            title = title,
            isPomodoro = isPomodoro
        )
        AppLockerService.instance?.initializeRepository()
        return newSchedule
    }

    fun enableAllSchedules() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.schedulesList
                .filter { !it.isActive }
                .forEach {
                    schedulesRepository.updateSchedule(it.copy(isActive = true))
                }
            AppLockerService.instance?.initializeRepository()
        }
    }
    
    fun startPomodoro(
        context: Context,
        schedule: Schedules
    ): Boolean {
        if (pomodoroManager.state.value.status != PomodoroStatus.IDLE) return false
        pomodoroManager.loadSchedule(schedule)
        ContextCompat.startForegroundService(
            /* context = */ context,
            /* intent = */ Intent(context, PomodoroService::class.java).apply {
                action = PomodoroServiceActions.FRESH_START.name
            }
        )
        return true
    }

    fun endPomodoro() = pomodoroManager.onPomodoroEnd()
}

data class HomeUiState(
    val schedulesList: List<Schedules> = listOf(),
    val showAccessibilityPermissionRationale: Boolean = false,
    val currentFilter: SchedulesFilter = SchedulesFilter.All,
    val userPreferences: UserPreferences = UserPreferences(1, false)
)