package com.sarangem.zenwell.ui.screens.focus

import androidx.lifecycle.ViewModel
import com.sarangem.zenwell.data.database.tables.Schedules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FocusViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(state: FocusUiState) {
        _uiState.update { state }
    }
}

data class FocusUiState(
    val schedule: Schedules = Schedules(),
    val isFullScreen: Boolean = false
)
