package com.sarangem.zenwell.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.data.SchedulesRepository
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.service.alarmer.ManageExactAlarms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class ZenwellAppViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {


    // UI STATE DECLARATION

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState = _uiState.asStateFlow()

    // DIRECT FUNCTIONS

    fun getAllSchedules() = schedulesRepository.getAllSchedules()
    fun getAppNames(id: Int) = schedulesRepository.getAppNames(id)


    // UPDATE UI STATE

    fun updateUiState(currentState: AppUiState) {
        _uiState.update { currentState }
    }

    // DATABASE QUERIES

    var pastAppList: MutableList<String> = mutableListOf()

    suspend fun addNewSchedule(context: Context): Schedules {

        val title = context.getString(R.string.schedule) +
                schedulesRepository.getSchedulesCount().first().plus(1)

        val newSchedule = Schedules(
            id = schedulesRepository.addNewSchedule(Schedules(title = title)),
            title = title
        )
        return newSchedule
    }

    suspend fun saveToDatabase(context: Context) {
        uiState.filter { it.appNames != null }.first()

        val alarmClass = ManageExactAlarms(
            context = context,
            schedulesList = schedulesRepository.getAllSchedules().first()
        )

        alarmClass.cancelAllExactAlarms()
        schedulesRepository.saveToDatabase(
            schedule = _uiState.value.schedule,
            appNames = _uiState.value.appNames ?: listOf(),
            pastAppSet = pastAppList.toMutableSet(),
        )
        alarmClass.setExactAlarms()
    }

    suspend fun deleteSchedule(context: Context) {
        uiState.filter { it.appNames != null }.first()

        val alarmClass = ManageExactAlarms(
            context = context,
            schedulesList = schedulesRepository.getAllSchedules().first()
        )

        alarmClass.cancelAllExactAlarms()
        schedulesRepository.deleteSchedule(_uiState.value.schedule)
        alarmClass.setExactAlarms()

    }


    // INITIALIZATION

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ZenwellApplication)
                ZenwellAppViewModel(application.container)
            }
        }
    }
}