package com.sarangem.zenwell.ui.editscreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.sarangem.zenwell.data.SchedulesRepository
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.service.alarmer.ManageExactAlarms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class EditScreenViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {

    // ui state declarations
    private val _uiState = MutableStateFlow(EditScreenUiState())
    val uiState = _uiState.asStateFlow()
    lateinit var pastAppList: MutableList<String>
    var isInitialized: Boolean = false

    // schedulesRepository functions
    fun getScheduleInfo(id: Int) = schedulesRepository.getScheduleInfoById(id)
    fun getAppNames(id: Int) = schedulesRepository.getAppNames(id)

    fun updateUiState(currentState: EditScreenUiState) {
        _uiState.update { currentState }
    }

    suspend fun saveToDatabase(context: Context) {
        if (isInitialized) {

            val alarmClass = ManageExactAlarms(
                context = context,
                schedulesList = schedulesRepository.getAllSchedules().first()
            )

            alarmClass.cancelAllExactAlarms()
            schedulesRepository.saveToDatabase(
                schedule = _uiState.value.scheduleInfo,
                appNames = _uiState.value.appNames,
                pastAppSet = pastAppList.toMutableSet(),
            )
            alarmClass.setExactAlarms()

        }
    }

    suspend fun deleteSchedule(context: Context) {
        if (isInitialized) {

            val alarmClass = ManageExactAlarms(
                context = context,
                schedulesList = schedulesRepository.getAllSchedules().first()
            )

            alarmClass.cancelAllExactAlarms()
            schedulesRepository.deleteSchedule(_uiState.value.scheduleInfo)
            alarmClass.setExactAlarms()

        }
    }

}


data class EditScreenUiState(
    val scheduleInfo: Schedules = Schedules(),
    val appNames: List<String> = emptyList<String>()
)