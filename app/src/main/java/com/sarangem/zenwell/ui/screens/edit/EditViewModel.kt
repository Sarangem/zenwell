package com.sarangem.zenwell.ui.screens.edit

import androidx.lifecycle.ViewModel
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

data class EditUiState(
    val schedule: Schedules = Schedules(),
    val appNames: List<String>? = null,
    val validationErrors: Set<ValidationError> = emptySet(),
)

class EditViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState = _uiState.asStateFlow()

    suspend fun getAppNames(id: Int) = schedulesRepository.getAppNames(id).first()

    var pastAppList: List<String>? = null

    fun updateUiState(state: EditUiState) {
        _uiState.update {
            state.copy(
                validationErrors = validateSchedule(state.schedule)
            )
        }
    }

    suspend fun saveToDatabase() {
        schedulesRepository.saveToDatabase(
            schedule = _uiState.value.schedule,
            appNames = _uiState.value.appNames,
            pastAppList = pastAppList,
        )
        AppBlockerService.instance?.initializeRepository()
    }

    suspend fun deleteSchedule() {
        schedulesRepository.deleteSchedule(_uiState.value.schedule)
        AppBlockerService.instance?.initializeRepository()
    }

}