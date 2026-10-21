/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.data.repository

import com.sarangem.zenwell.model.AppNames
import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SchedulesRepository {
    fun getAllSchedules(): Flow<List<Schedules>>
    fun getScheduleInfoById(id: Int): Flow<Schedules>
    fun getSchedulesCount(): Flow<Int>
    fun getAppNamesById(id: Int): Flow<List<AppNames>>
    fun getAllApps(): Flow<List<AppNames>>
    fun getUserPreferences(): Flow<UserPreferences>
    fun getCustomApps(): Flow<List<AppNames>>
    suspend fun deleteSchedule(schedule: Schedules)
    suspend fun addNewSchedule(schedule: Schedules): Int
    suspend fun upsertApp(appName: AppNames)
    suspend fun upsertUserPreferences(userPreferences: UserPreferences)
    suspend fun deleteApp(appName: AppNames)
    suspend fun updateSchedule(schedule: Schedules)
    suspend fun saveToDatabase(
        schedule: Schedules,
        selectedAppsList: List<AppNames>?,
        pastAppList: List<AppNames>?
    )
    suspend fun getAllData(): List<BackupData>
    suspend fun restoreAllData(list: List<BackupData>)
}