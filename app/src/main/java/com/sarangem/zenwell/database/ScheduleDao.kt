/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.BlockedApps
import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedules ORDER BY is_active DESC, start_time ASC")
    fun getAllSchedules() : Flow<List<Schedules>>

    @Query("SELECT * FROM schedules WHERE id=:id")
    fun getScheduleInfoById(id: Int): Flow<Schedules>

    @Query("SELECT COUNT(*) FROM schedules")
    fun getSchedulesCount(): Flow<Int>

    @Query("SELECT * FROM app_names ORDER BY title")
    fun getAllApps(): Flow<List<AppNames>>

    @Query("SELECT * FROM blocked_apps")
    fun getAllAppRelations(): Flow<List<BlockedApps>>

    @Query("SELECT id FROM app_names WHERE title=:appName")
    fun getAppId(appName: String): Flow<Int>

    @Query("""
        SELECT * FROM app_names WHERE id IN (
            SELECT app_id FROM blocked_apps WHERE schedule_id=:id
        )""")
    fun getAppNamesById(id: Int): Flow<List<AppNames>>

    @Query("SELECT id FROM blocked_apps WHERE app_id=:appId")
    fun getAppRelationByAppId(appId: Int): Flow<List<Int>>

    @Query("SELECT * FROM blocked_apps WHERE schedule_id=:scheduleId")
    fun getAppRelationByScheduleId(scheduleId: Int): Flow<List<BlockedApps>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppNames(appNames: AppNames)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppRelation(blockedApps: BlockedApps)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSchedule(schedules: Schedules): Long

    @Update
    fun updateSchedule(schedules: Schedules)


    @Delete
    fun deleteSchedule(schedules: Schedules)

    @Query("DELETE from schedules")
    fun deleteAllSchedules()

    @Delete
    fun deleteAppNames(appNames: AppNames)

    @Query("DELETE from app_names")
    fun deleteAllAppNames()

    @Query("DELETE from blocked_apps WHERE schedule_id=:scheduleId AND app_id=:appId")
    fun deleteAppRelation(scheduleId: Int, appId: Int)

    @Query("DELETE from blocked_apps")
    fun deleteAllAppRelations()
}