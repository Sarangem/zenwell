package com.sarangem.zenwell.ui.editscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.data.database.repository.SchedulesRepository
import com.sarangem.zenwell.data.database.tables.Schedules
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
    val validationErrors: Set<ValidationError> = emptySet(),
)

class EditViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState = _uiState.asStateFlow()

    fun getAppNames(id: Int) = schedulesRepository.getAppNames(id)

    var pastAppList: List<String>? = null

    fun updateUiState(state: EditUiState) {
        _uiState.update {
            state.copy(
                validationErrors = validateSchedule(state.schedule)
            )
        }
    }

    fun setAppNamesInUiState() {
        viewModelScope.launch(Dispatchers.IO) {
            val appNames = getAppNames(_uiState.value.schedule.id).first()
            updateUiState(
                _uiState.value.copy(
                    appNames = appNames
                )
            )
            pastAppList = appNames
        }
    }

    suspend fun saveToDatabase() {
        val appNames = _uiState.value.appNames ?: getAppNames(_uiState.value.schedule.id).first()
        schedulesRepository.saveToDatabase(
            schedule = _uiState.value.schedule,
            appNames = appNames,
            pastAppList = pastAppList ?: appNames,
        )
        AppBlockerService.instance?.initializeRepository()
    }

    suspend fun deleteSchedule() {
        schedulesRepository.deleteSchedule(_uiState.value.schedule)
        AppBlockerService.instance?.initializeRepository()
    }

}