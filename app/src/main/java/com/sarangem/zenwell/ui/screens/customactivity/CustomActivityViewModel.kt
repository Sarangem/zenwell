/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.customactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.YOUTUBE_PACKAGE_NAME
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CustomActivityUiState(
    val appName: AppNames = AppNames(),
    val isSaved: Boolean = true
)

@HiltViewModel
class CustomActivityViewModel @Inject constructor(
    private val schedulesRepository: SchedulesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<CustomActivityUiState>>(emptyList())
    val uiState = _uiState.asStateFlow()

    private var isInitialized = false

    fun initialize() {
        if (isInitialized) return
        isInitialized = true
        viewModelScope.launch {
            schedulesRepository.getCustomApps()
                .flowOn(Dispatchers.IO)
                .collect { customApps ->
                    _uiState.update { currentList ->
                        val unsavedNewItems = currentList.filter { it.appName.id == 0 && !it.isSaved }
                        customApps.map { CustomActivityUiState(appName = it, isSaved = true) } + unsavedNewItems
                    }
                }
        }
    }
    
    private suspend fun updateAccessibilityService() {
        withContext(Dispatchers.IO) {
            AppBlockerService.instance?.initializeRepository()
        }
    }

    fun onAddNew() {
        _uiState.update { current ->
            current + CustomActivityUiState(
                appName = AppNames(),
                isSaved = false
            )
        }
    }

    fun updateUiState(index: Int, state: CustomActivityUiState) {
        _uiState.update { current ->
            current.toMutableList().apply {
                if (index in indices) {
                    this[index] = state.copy(isSaved = false)
                }
            }
        }
    }

    fun onSave(state: CustomActivityUiState) {
        val app = state.appName.copy(
            viewId = state.appName.viewId?.ifBlank { null },
            viewKeyword = state.appName.viewKeyword?.ifBlank { null },
            viewTitle = state.appName.viewTitle?.ifBlank { null }
        )
        _uiState.update { current ->
            current.map { item ->
                if (item === state || (item.appName.id != 0 && item.appName.id == state.appName.id)) {
                    item.copy(appName = app, isSaved = true)
                } else {
                    item
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            schedulesRepository.upsertApp(app)
            updateAccessibilityService()
        }
    }

    fun onDelete(state: CustomActivityUiState) {
        if (state.appName.id != 0) {
            viewModelScope.launch(Dispatchers.IO) {
                schedulesRepository.deleteApp(state.appName)
                updateAccessibilityService()
            }
        } else {
            _uiState.update { current -> current.filter { it !== state } }
        }
    }

    fun onReset() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.forEach { item ->
                if (item.appName.id != 0) {
                    schedulesRepository.deleteApp(item.appName)
                }
            }
            schedulesRepository.upsertApp(
                AppNames(
                    id = 0,
                    packageName = YOUTUBE_PACKAGE_NAME,
                    viewId = YOUTUBE_SHORTS_VIEW_ID,
                    viewKeyword = null,
                    viewTitle = YOUTUBE_SHORTS_NAME
                )
            )
            updateAccessibilityService()
        }
    }
}