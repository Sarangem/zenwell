package com.sarangem.zenwell.database.repository

import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.coroutines.flow.Flow

interface SchedulesRepository {
    fun getAllSchedules(): Flow<List<Schedules>>
    fun getScheduleInfoById(id: Int): Flow<Schedules>
    fun getSchedulesCount(): Flow<Int>
    fun getAppNamesById(id: Int): Flow<List<AppNames>>
    fun getAllApps(): Flow<List<AppNames>>
    fun insertApp(appName: AppNames)
    fun deleteApp(appName: AppNames)
    suspend fun deleteSchedule(schedule: Schedules)
    suspend fun addNewSchedule(schedule: Schedules): Int
    suspend fun updateSchedule(schedule: Schedules)
    suspend fun saveToDatabase(
        schedule: Schedules,
        appNames: List<String>?,
        pastAppList: List<String>?
    )
    suspend fun getAllData(): List<BackupData>
    suspend fun restoreAllData(list: List<BackupData>)
}