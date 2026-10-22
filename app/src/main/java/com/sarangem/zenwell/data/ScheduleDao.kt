/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.sarangem.zenwell.model.AppNames
import com.sarangem.zenwell.model.BlockedApps
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.model.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedules ORDER BY is_active DESC, start_time ASC")
    fun getAllSchedules(): Flow<List<Schedules>>

    @Query("SELECT COUNT(*) FROM schedules")
    suspend fun getSchedulesCount(): Int

    @Query("SELECT * FROM app_names")
    suspend fun getAllApps(): List<AppNames>

    @Query("""
        SELECT id FROM app_names WHERE 
        packageName = :packageName AND viewId IS :viewId AND viewKeyword IS :viewKeyword
        LIMIT 1
    """)
    suspend fun getAppId(packageName: String, viewId: String?, viewKeyword: String?): Int

    @Query("""
        SELECT * FROM app_names WHERE id IN (
            SELECT app_id FROM blocked_apps WHERE schedule_id = :id
        )
    """)
    suspend fun getAppNamesById(id: Int): List<AppNames>

    @Query("SELECT id FROM blocked_apps WHERE app_id = :appId")
    suspend fun getAppRelationByAppId(appId: Int): List<Int>

    @Query("SELECT * FROM user_prefs WHERE id = 1 LIMIT 1")
    fun getUserPreferences(): Flow<UserPreferences?>

    @Query("SELECT * FROM app_names WHERE viewTitle IS NOT NULL")
    fun getCustomApps(): Flow<List<AppNames>>

    @Upsert
    suspend fun upsertAppNames(appNames: AppNames)

    @Upsert
    suspend fun upsertUserPreferences(userPreferences: UserPreferences)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppRelation(blockedApps: BlockedApps)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedules: Schedules): Long

    @Update
    suspend fun updateSchedule(schedules: Schedules)

    @Delete
    suspend fun deleteSchedule(schedules: Schedules)

    @Delete
    suspend fun deleteAppNames(appNames: AppNames)

    @Query("DELETE FROM blocked_apps WHERE schedule_id = :scheduleId AND app_id = :appId")
    suspend fun deleteAppRelation(scheduleId: Int, appId: Int)
}