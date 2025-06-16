package com.sarangem.zenwell.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.data.SchedulesRepository
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.service.AppBlockerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    fun initUiState() {

        val context = this
        viewModelScope.launch(Dispatchers.IO) {

            val appNames = context.getAppNames(_uiState.value.schedule.id).first()
            context.updateUiState(
                _uiState.value.copy(
                    appNames = appNames
                )
            )
            context.pastAppList = appNames.toMutableList()

        }
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

    suspend fun saveToDatabase() {
        uiState.filter { it.appNames != null }.first()

        schedulesRepository.saveToDatabase(
            schedule = _uiState.value.schedule,
            appNames = _uiState.value.appNames ?: listOf(),
            pastAppSet = pastAppList.toMutableSet(),
        )

        AppBlockerService.instance?.initializeRepository()
    }

    suspend fun deleteSchedule() {

        uiState.filter { it.appNames != null }.first()
        schedulesRepository.deleteSchedule(_uiState.value.schedule)
        AppBlockerService.instance?.initializeRepository()

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