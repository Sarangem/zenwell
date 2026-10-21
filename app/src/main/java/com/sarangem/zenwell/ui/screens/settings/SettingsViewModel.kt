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
import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.service.AppLockerService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SchedulesRepository
) : ViewModel() {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun createBackup(
        uri: Uri,
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                runCatching {
                    val data = repository.getAllData()
                    val jsonString = json.encodeToString(data)
                    context
                        .contentResolver
                        .openOutputStream(uri)
                        ?.use { outputStream ->
                            BufferedWriter(OutputStreamWriter(outputStream)).use {
                                it.write(jsonString)
                            }
                        }
                }.isSuccess
            }
            onResult(success)
        }
    }


    fun restoreBackup(
        uri: Uri,
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                runCatching {
                    context
                        .contentResolver
                        .openInputStream(uri)
                        ?.use { inputStream ->
                            val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
                            val restoredData = json.decodeFromString<List<BackupData>>(jsonString)
                            repository.restoreAllData(restoredData)
                            withContext(Dispatchers.IO) {
                                AppLockerService.instance?.initializeRepository()
                            }
                        }
                }.isSuccess
            }
            onResult(success)
        }
    }
}