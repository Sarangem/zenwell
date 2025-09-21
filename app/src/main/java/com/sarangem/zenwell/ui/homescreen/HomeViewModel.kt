package com.sarangem.zenwell.ui.homescreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.database.repository.SchedulesRepository
import com.sarangem.zenwell.data.database.tables.Schedules
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        schedulesRepository.getAllSchedules().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = HomeUiState()
            )

    suspend fun addNewSchedule(context: Context): Schedules {
        val title = context.getString(R.string.schedule) + schedulesRepository.getSchedulesCount().first().plus(1)
        val newSchedule = Schedules(
            id = schedulesRepository.addNewSchedule(Schedules(title = title)),
            title = title
        )
        return newSchedule
    }

}

data class HomeUiState(val schedulesList: List<Schedules> = listOf())
