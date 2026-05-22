/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.settings

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.service.AppBlockerService
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

    fun createBackup(uri: Uri, context: Context) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val data = repository.getAllData()
                val jsonString = json.encodeToString(data)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    BufferedWriter(OutputStreamWriter(outputStream)).use { it.write(jsonString) }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, R.string.backup_completed, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (_: Exception) {
            Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_SHORT).show()
        }
    }

    fun restoreBackup(uri: Uri, context: Context) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
                    val restoredData = json.decodeFromString<List<BackupData>>(jsonString)
                    repository.restoreAllData(restoredData)
                    AppBlockerService.instance?.initializeRepository()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, R.string.data_restore_completed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (_: Exception) {
            Toast.makeText(context, R.string.data_restore_failed, Toast.LENGTH_SHORT).show()
        }
    }
}