package com.sarangem.zenwell.ui.screens.home

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.service.AppBlockerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.getAllSchedules().collect { list ->
                _uiState.update { uiState ->
                    uiState.copy(
                        schedulesList = list,
                        showAccessibilityPermissionRationale = AppBlockerService.instance == null
                    )
                }
            }
        }
    }

    fun updateUiState(uiState: HomeUiState){
        _uiState.update { uiState }
    }

    fun recheckNotificationPermission(context: Context){
        val isEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        val needsPermission = _uiState.value.schedulesList.any { schedule ->
            (schedule.unlockMethod != UnlockMethod.StrictBlock && schedule.notificationTimeInMinutes > 0) || schedule.isPomodoro
        }
        _uiState.update {
            it.copy(
                showNotificationPermissionRationale = needsPermission && !isEnabled
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
        return newSchedule
    }
}

data class HomeUiState(
    val schedulesList: List<Schedules> = listOf(),
    val showAccessibilityPermissionRationale: Boolean = false,
    val showNotificationPermissionRationale: Boolean = false,
    val currentFilter: SchedulesFilter = SchedulesFilter.All
)
