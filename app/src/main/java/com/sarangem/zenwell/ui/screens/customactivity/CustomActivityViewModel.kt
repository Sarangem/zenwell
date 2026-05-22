/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.customactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.YOUTUBE_SHORTS_NAME
import com.sarangem.zenwell.YOUTUBE_SHORTS_VIEW_ID
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.service.AppBlockerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomActivityViewModel @Inject constructor(
    private val schedulesRepository: SchedulesRepository
) : ViewModel() {

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
            schedulesRepository.upsertApp(
                AppNames(
                    id,
                    customActivityUiState.packageName + ":id/" + customActivityUiState.viewId,
                    customActivityUiState.viewTitle
                )
            )
            AppBlockerService.instance?.initializeRepository()
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
            AppBlockerService.instance?.initializeRepository()
            _uiState.update { it - id }
        }
    }

    fun onReset(){
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.forEach { onDelete(it.key, it.value) }
            schedulesRepository.upsertApp(
                AppNames(0, YOUTUBE_SHORTS_VIEW_ID, YOUTUBE_SHORTS_NAME)
            )
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