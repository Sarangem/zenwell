/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database.repository

import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.database.ScheduleDao
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.BlockedApps
import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class OfflineSchedulesRepository(private val scheduleDao: ScheduleDao) : SchedulesRepository {

    override fun getAllSchedules()= scheduleDao.getAllSchedules()
    override fun getScheduleInfoById(id: Int)= scheduleDao.getScheduleInfoById(id)
    override fun getSchedulesCount()= scheduleDao.getSchedulesCount()
    override fun getAppNamesById(id: Int)= scheduleDao.getAppNamesById(id)
    override fun getAllApps()= scheduleDao.getAllApps()
    override fun insertApp(appName: AppNames)= scheduleDao.insertAppNames(appName)
    override fun deleteApp(appName: AppNames)= scheduleDao.deleteAppNames(appName)
    override suspend fun addNewSchedule(schedule: Schedules)= scheduleDao.insertSchedule(schedule).toInt()
    override suspend fun updateSchedule(schedule: Schedules)= scheduleDao.updateSchedule(schedule)

    override suspend fun getAllData(): List<BackupData> {
        val schedulesList = scheduleDao.getAllSchedules().first()
        val data = mutableListOf<BackupData>()
        schedulesList.forEach { schedule ->
            data.add(
                BackupData(
                    schedule = schedule,
                    appNamesList = getAppNamesById(schedule.id).first().map { it.title to it.viewTitle }
                )
            )
        }
        return data
    }

    override suspend fun restoreAllData(list: List<BackupData>) {
        list.forEach { data ->
            data.appNamesList.forEach { app -> insertApp(AppNames(0, app.first, app.second)) }
            val id = addNewSchedule(data.schedule)
            saveToDatabase(
                schedule = data.schedule.copy(id = id),
                appNames = data.appNamesList.map { it.first },
                pastAppList = listOf()
            )
        }
    }

    override suspend fun saveToDatabase(
        schedule: Schedules,
        appNames: List<String>?,
        pastAppList: List<String>?
    ) {
        // first update the schedule table
        scheduleDao.updateSchedule(schedules = schedule)

        // if appNames is null, apps are not loaded nor modified
        if (appNames == null || pastAppList == null) return

        val pastAppSet = pastAppList.toMutableSet()
        for (app in appNames) {

            if (app !in pastAppSet) {

                // insert new app name
                val appId = scheduleDao.getAppId(app).firstOrNull()
                if (appId == null || appId == 0) { // it does not exist in table
                    scheduleDao.insertAppNames(AppNames(title = app))
                }

                // insert app to schedule relation
                scheduleDao.insertAppRelation(
                    BlockedApps(
                        scheduleId = schedule.id,
                        appId = scheduleDao.getAppId(app).firstOrNull() ?: 0
                    )
                )

            } else {
                pastAppSet.remove(app)
            }

        }

        for (app in pastAppSet) {
            scheduleDao.deleteAppRelation(
                scheduleId = schedule.id,
                appId = scheduleDao.getAppId(app).firstOrNull() ?: 0
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
            if (app.viewTitle != null) continue // do not remove custom views
            if (scheduleDao.getAppRelationByAppId(app.id).first().isNotEmpty()) continue // app is not referenced
            scheduleDao.deleteAppNames(app)
        }
    }
}