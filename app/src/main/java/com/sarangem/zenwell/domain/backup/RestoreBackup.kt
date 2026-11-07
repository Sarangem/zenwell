/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.domain.backup

import android.content.Context
import android.net.Uri
import com.sarangem.zenwell.data.repository.SchedulesRepository
import com.sarangem.zenwell.service.applock.AppLockerService
import com.sarangem.zenwell.ui.screens.edit.validateSchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

suspend fun restoreBackup(
    uri: Uri,
    context: Context,
    repository: SchedulesRepository
): Boolean {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        allowComments = true
        allowTrailingComma = true
    }

    var isInvalidatedSchedule = false
    val result = withContext(Dispatchers.IO) {
        runCatching {
            context
                .contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->

                    val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
                    val restoredData = json.decodeFromString<List<BackupData>>(jsonString)

                    restoredData.forEach { data ->

                        if (validateSchedule(data.schedule).isNotEmpty()) {
                            isInvalidatedSchedule = true
                            return@forEach
                        }

                        data.appNamesList.forEach { repository.upsertApp(it.copy(id = 0)) }
                        val id = repository.addNewSchedule(data.schedule.copy(id = 0))
                        repository.saveToDatabase(
                            schedule = data.schedule.copy(id = id),
                            selectedAppsList = data.appNamesList,
                            pastAppList = listOf()
                        )
                    }

                    withContext(Dispatchers.IO) {
                        AppLockerService.instance?.initializeRepository()
                    }

                }
        }.isSuccess
    }

    return if (isInvalidatedSchedule) false else result
}