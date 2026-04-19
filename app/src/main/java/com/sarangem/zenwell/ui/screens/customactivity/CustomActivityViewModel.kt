/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.customactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.tables.AppNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomActivityViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<Map<Int, CustomActivityUiState>>(emptyMap())
    val uiState = _uiState.asStateFlow()

    fun initialize() {
        viewModelScope.launch {
            schedulesRepository.getAllApps()
                .flowOn(Dispatchers.IO)
                .collect { apps ->
                    val viewsMap = apps.filter { it.viewTitle != null }
                        .associateBy(
                            { it.id },
                            { CustomActivityUiState(
                                packageName = it.title.substringBefore(":id/"),
                                viewId = it.title.substringAfter(":id/"),
                                viewTitle = it.viewTitle ?: ""
                            ) }
                        )
                    _uiState.update { viewsMap }
                }
        }
    }

    fun updateUiState(
        id: Int,
        customActivityUiState: CustomActivityUiState
    ) {
        _uiState.update {
            it + (id to customActivityUiState.copy(isSaved = false))
        }
    }

    fun onSave(
        id: Int,
        customActivityUiState: CustomActivityUiState
    ){
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.insertApp(
                AppNames(
                    id,
                    customActivityUiState.packageName + ":id/" + customActivityUiState.viewId,
                    customActivityUiState.viewTitle
                )
            )
            initialize()
        }
    }

    fun onDelete(
        id: Int,
        customActivityUiState: CustomActivityUiState
    ){
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.deleteApp(
                AppNames(
                    id,
                    customActivityUiState.packageName + ":id/" + customActivityUiState.viewId,
                    customActivityUiState.viewTitle
                )
            )
            _uiState.update { it - id }
        }
    }

    fun onReset(){
        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.insertApp(
                AppNames(
                    1,
                    "com.google.android.youtube:id/reel_watch_fragment_root",
                    "Youtube Shorts"
                )
            )
            _uiState.value.forEach { onDelete(it.key, it.value) }
            initialize()
        }
    }

}

data class CustomActivityUiState(
    val packageName: String = "",
    val viewId: String = "",
    val viewTitle: String = "",
    val isSaved: Boolean = true
)