/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.database.tables.UserPreferences
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.service.AppBlockerService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val schedulesRepository: SchedulesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.getAllSchedules().collect { list ->
                _uiState.update { uiState ->
                    uiState.copy(
                        schedulesList = list.sortedBy { it.title.lowercase() },
                        showAccessibilityPermissionRationale = AppBlockerService.instance == null
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(
                userPreferences = schedulesRepository.getUserPreferences().first()
            ) }
        }
    }

    fun updateUiState(uiState: HomeUiState) { _uiState.update { uiState } }

    fun hideNotificationCard() {
        val userPrefs = _uiState.value.userPreferences.copy(showNotificationPermissionCard = false)
        _uiState.update { it.copy(
            userPreferences = userPrefs,
            showNotificationPermissionRationale = false
        )}
        viewModelScope.launch(Dispatchers.IO) { schedulesRepository.upsertUserPreferences(userPrefs) }
    }

    fun updateUserEntry(entry: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.upsertUserPreferences(
                _uiState.value.userPreferences.copy(firstEntry = entry)
            )
        }

        // entry is only updated on app start, not mid-session
        _uiState.update { it.copy(userPreferences = _uiState.value.userPreferences.copy(firstEntry = null)) }
    }

    fun recheckPermission(context: Context){
        val isEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        val needsPermission = _uiState.value.schedulesList.any { schedule ->
            (schedule.unlockMethod != UnlockMethod.StrictBlock && schedule.notificationTimeInMinutes > 0) || schedule.isPomodoro
        }
        _uiState.update {
            it.copy(
                showNotificationPermissionRationale = needsPermission && !isEnabled && _uiState.value.userPreferences.showNotificationPermissionCard,
                showAccessibilityPermissionRationale = AppBlockerService.instance == null
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
        AppBlockerService.instance?.initializeRepository()
        return newSchedule
    }
}

data class HomeUiState(
    val schedulesList: List<Schedules> = listOf(),
    val showAccessibilityPermissionRationale: Boolean = false,
    val showNotificationPermissionRationale: Boolean = false,
    val currentFilter: SchedulesFilter = SchedulesFilter.All,
    val userPreferences: UserPreferences = UserPreferences(1, false, null)
)
