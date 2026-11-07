/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.data.repository.SchedulesRepository
import com.sarangem.zenwell.domain.backup.createBackup
import com.sarangem.zenwell.domain.backup.restoreBackup
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SchedulesRepository
) : ViewModel() {

    fun createBackup(
        uri: Uri,
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = createBackup(uri, context, repository)
            onResult(success)
        }
    }

    fun restoreBackup(
        uri: Uri,
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = restoreBackup(uri, context, repository)
            onResult(success)
        }
    }
}