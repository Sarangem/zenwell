/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.domain.backup

import android.content.Context
import android.net.Uri
import com.sarangem.zenwell.data.repository.SchedulesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.BufferedWriter
import java.io.OutputStreamWriter

suspend fun createBackup(
    uri: Uri,
    context: Context,
    repository: SchedulesRepository
): Boolean {
    val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }
    return withContext(Dispatchers.IO) {
        runCatching {
            val schedulesList = repository.getAllSchedules().first()
            val data = mutableListOf<BackupData>()
            schedulesList.forEach { schedule ->
                data.add(
                    BackupData(
                        schedule = schedule,
                        appNamesList = repository.getAppNamesById(schedule.id)
                    )
                )
            }
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
}