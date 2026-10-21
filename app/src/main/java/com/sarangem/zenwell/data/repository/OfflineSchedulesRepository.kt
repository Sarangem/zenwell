/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.data.repository

import com.sarangem.zenwell.data.ScheduleDao
import com.sarangem.zenwell.model.AppNames
import com.sarangem.zenwell.model.BlockedApps
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.model.UserPreferences
import com.sarangem.zenwell.model.BackupData
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class OfflineSchedulesRepository @Inject constructor(
    private val scheduleDao: ScheduleDao
) : SchedulesRepository {

    override fun getAllSchedules()= scheduleDao.getAllSchedules()
    override fun getScheduleInfoById(id: Int)= scheduleDao.getScheduleInfoById(id)
    override fun getSchedulesCount()= scheduleDao.getSchedulesCount()
    override fun getAppNamesById(id: Int)= scheduleDao.getAppNamesById(id)
    override fun getAllApps()= scheduleDao.getAllApps()
    override fun getUserPreferences()= scheduleDao.getUserPreferences().map { it ?: UserPreferences() }
    override fun getCustomApps() = scheduleDao.getCustomApps()
    override suspend fun upsertApp(appName: AppNames)= scheduleDao.upsertAppNames(appName)
    override suspend fun upsertUserPreferences(userPreferences: UserPreferences)= scheduleDao.upsertUserPreferences(userPreferences)
    override suspend fun deleteApp(appName: AppNames)= scheduleDao.deleteAppNames(appName)
    override suspend fun addNewSchedule(schedule: Schedules)= scheduleDao.insertSchedule(schedule).toInt()
    override suspend fun updateSchedule(schedule: Schedules)= scheduleDao.updateSchedule(schedule)

    override suspend fun getAllData(): List<BackupData> {
        val schedulesList = getAllSchedules().first()
        val data = mutableListOf<BackupData>()
        schedulesList.forEach { schedule ->
            data.add(
                BackupData(
                    schedule = schedule,
                    appNamesList = getAppNamesById(schedule.id).first()
                )
            )
        }
        return data
    }

    override suspend fun restoreAllData(list: List<BackupData>) {
        list.forEach { data ->
            data.appNamesList.forEach { upsertApp(it.copy(id = 0)) }
            val id = addNewSchedule(data.schedule.copy(id = 0))
            saveToDatabase(
                schedule = data.schedule.copy(id = id),
                selectedAppsList = data.appNamesList,
                pastAppList = listOf()
            )
        }
    }

    override suspend fun saveToDatabase(
        schedule: Schedules,
        selectedAppsList: List<AppNames>?,
        pastAppList: List<AppNames>?
    ) {
        scheduleDao.updateSchedule(schedules = schedule)
        if (selectedAppsList == null || pastAppList == null) return

        val pastAppSet = pastAppList.toMutableSet()
        for (app in selectedAppsList) {
            if (!pastAppSet.any {
                it.packageName != app.packageName &&
                    it.viewKeyword != app.viewKeyword &&
                    it.viewId != app.viewId
                }
            ) {
                val appId = scheduleDao.getAppId(app.packageName, app.viewId, app.viewKeyword).firstOrNull()
                if (appId == null || appId == 0) {
                    upsertApp(app.copy(id = 0))
                }

                scheduleDao.insertAppRelation(
                    BlockedApps(
                        scheduleId = schedule.id,
                        appId = scheduleDao.getAppId(app.packageName, app.viewId, app.viewKeyword).firstOrNull() ?: 0
                    )
                )
            } else {
                pastAppSet.remove(app)
            }
        }

        for (app in pastAppSet) {
            scheduleDao.deleteAppRelation(
                scheduleId = schedule.id,
                appId = scheduleDao.getAppId(app.packageName, app.viewId, app.viewKeyword).firstOrNull() ?: 0
            )
        }

        removeAppNameIfUnused()
    }

    override suspend fun deleteSchedule(schedule: Schedules) {
        scheduleDao.deleteSchedule(schedule)
        removeAppNameIfUnused()
    }

    private suspend fun removeAppNameIfUnused() {
        val appIds = scheduleDao.getAllApps().first()
        for (app in appIds) {
            if (app.viewId != null || app.viewKeyword != null) continue // do not remove custom views
            if (scheduleDao.getAppRelationByAppId(app.id).first().isNotEmpty()) continue // app is not referenced
            scheduleDao.deleteAppNames(app)
        }
    }
}