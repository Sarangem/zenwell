package com.sarangem.zenwell.data

import com.sarangem.zenwell.data.tables.Schedules
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
        appNames: List<String>,
        pastAppSet: MutableSet<String>
    )
}