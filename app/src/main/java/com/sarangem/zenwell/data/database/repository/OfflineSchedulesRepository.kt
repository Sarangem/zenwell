package com.sarangem.zenwell.data.database.repository

import com.sarangem.zenwell.data.BackupData
import com.sarangem.zenwell.data.database.ScheduleDao
import com.sarangem.zenwell.data.database.tables.AppNames
import com.sarangem.zenwell.data.database.tables.BlockedApps
import com.sarangem.zenwell.data.database.tables.Schedules
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

    override suspend fun getAllData(): BackupData {
        return BackupData(
            schedulesList = scheduleDao.getAllSchedules().first().sortedBy { it.id },
            appNamesList = scheduleDao.getAllApps().first(),
            blockedAppsList = scheduleDao.getAllAppRelations().first()
        )
    }

    override suspend fun restoreAllData(data: BackupData) {
        scheduleDao.deleteAllSchedules()
        data.schedulesList.forEach { 
            scheduleDao.insertSchedule(it)
        }
        scheduleDao.deleteAllAppNames()
        data.appNamesList.forEach { 
            scheduleDao.insertAppNames(it)
        }
        scheduleDao.deleteAllAppRelations()
        data.blockedAppsList.forEach { 
            scheduleDao.insertAppRelation(it)
        }
        removeAppNameIfUnused()
    }

    override suspend fun saveToDatabase(
        schedule: Schedules,
        appNames: List<String>,
        pastAppList: List<String>
    ) {
        // first update the schedule table
        scheduleDao.updateSchedule(schedules = schedule)
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