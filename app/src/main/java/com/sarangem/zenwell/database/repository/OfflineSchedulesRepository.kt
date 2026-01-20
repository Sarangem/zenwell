package com.sarangem.zenwell.database.repository

import com.sarangem.zenwell.model.BackupData
import com.sarangem.zenwell.database.ScheduleDao
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.BlockedApps
import com.sarangem.zenwell.database.tables.Schedules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class OfflineSchedulesRepository(private val scheduleDao: ScheduleDao) : SchedulesRepository {

    override fun getAllSchedules(): Flow<List<Schedules>> = scheduleDao.getAllSchedules()
    override fun getScheduleInfoById(id: Int): Flow<Schedules> = scheduleDao.getScheduleInfoById(id)
    override fun getSchedulesCount(): Flow<Int> = scheduleDao.getSchedulesCount()
    override fun getAppNames(id: Int): Flow<List<String>> = scheduleDao.getAppNames(id)
    override suspend fun addNewSchedule(schedule: Schedules): Int = scheduleDao.insertSchedule(schedule).toInt()
    override suspend fun updateSchedule(schedule: Schedules) = scheduleDao.updateSchedule(schedule)

    override suspend fun getAllData(): List<BackupData> {
        val schedulesList = scheduleDao.getAllSchedules().first()
        val data = mutableListOf<BackupData>()
        schedulesList.forEach {
            data.add(
                BackupData(
                    schedule = it,
                    appNamesList = getAppNames(it.id).first()
                )
            )
        }
        return data
    }

    override suspend fun restoreAllData(data: List<BackupData>) {
        data.forEach {
            val id = addNewSchedule(it.schedule)
            saveToDatabase(
                schedule = it.schedule.copy(id = id),
                appNames = it.appNamesList,
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
            if (scheduleDao.getAppRelationByAppId(app.id).first().isEmpty()) { // app is not referenced
                scheduleDao.deleteAppNames(app)
            }
        }
    }
}