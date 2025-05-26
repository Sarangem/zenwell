package com.sarangem.zenwell.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.sarangem.zenwell.data.tables.*

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedules ORDER BY start_time")
    fun getAllSchedules() : Flow<List<Schedules>>

    @Query("SELECT * FROM schedules WHERE id=:id")
    fun getScheduleInfoById(id: Int): Flow<Schedules>

    @Query("SELECT * FROM app_names")
    fun getAllApps(): Flow<List<AppNames>>

    @Query("SELECT id FROM app_names WHERE title=:appName")
    fun getAppId(appName: String): Flow<Int>

    @Query("""
        SELECT title FROM app_names WHERE id IN (
            SELECT app_id FROM blocked_apps WHERE schedule_id=:id
        )""")
    fun getAppNames(id: Int): Flow<List<String>>

    @Query("SELECT * FROM blocked_apps WHERE app_id=:appId AND schedule_id=:scheduleId")
    fun getAppRelation(appId: Int, scheduleId: Int): Flow<BlockedApps>

    @Query("SELECT id FROM blocked_apps WHERE app_id=:appId")
    fun getAppRelationByAppId(appId: Int): Flow<List<Int>>

    @Query("SELECT * FROM blocked_apps WHERE schedule_id=:scheduleId")
    fun getAppRelationByScheduleId(scheduleId: Int): Flow<List<BlockedApps>>


    @Insert
    fun insertAppNames(appNames: AppNames)

    @Insert
    fun insertAppRelation(blockedApps: BlockedApps)

    @Insert
    fun addNewSchedule(schedules: Schedules): Long

    @Update
    fun updateSchedule(schedules: Schedules)


    @Delete
    fun deleteSchedule(schedules: Schedules)

    @Delete
    fun deleteAppNames(appNames: AppNames)

    @Delete
    fun deleteAppRelation(blockedApps: BlockedApps)
}