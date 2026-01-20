package com.sarangem.zenwell.database.repository

import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.coroutines.flow.Flow

interface SchedulesRepository {
    fun getAllSchedules(): Flow<List<Schedules>>
    fun getScheduleInfoById(id: Int): Flow<Schedules>
    fun getSchedulesCount(): Flow<Int>
    fun getAppNames(id: Int): Flow<List<String>>
    suspend fun deleteSchedule(schedule: Schedules)
    suspend fun addNewSchedule(schedule: Schedules): Int
    suspend fun updateSchedule(schedule: Schedules)
    suspend fun saveToDatabase(
        schedule: Schedules,
        appNames: List<String>?,
        pastAppList: List<String>?
    )
    suspend fun getAllData(): List<BackupData>
    suspend fun restoreAllData(data: List<BackupData>)
}