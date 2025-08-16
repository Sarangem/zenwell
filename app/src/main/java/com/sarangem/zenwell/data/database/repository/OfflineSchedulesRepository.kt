package com.sarangem.zenwell.data.database.repository

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
    override suspend fun addNewSchedule(schedule: Schedules): Int = scheduleDao.addNewSchedule(schedule).toInt()
    override suspend fun updateSchedule(schedule: Schedules) = scheduleDao.updateSchedule(schedule)

    override suspend fun saveToDatabase(
        schedule: Schedules,
        appNames: List<String>,
        pastAppSet: MutableSet<String>
    ) {
        // first update the schedule table
        scheduleDao.updateSchedule(schedules = schedule)

        for (app in appNames) {

            if (app !in pastAppSet) {

                // insert new app name
                this.insertAppNameIfNotExists(
                    app = app,
                    id = scheduleDao.getAppId(app).firstOrNull()
                )

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

        for (pastApp in pastAppSet) {

            // remove unnecessary schedule relations
            val appId = scheduleDao.getAppId(pastApp).firstOrNull() ?: 0
            scheduleDao.deleteAppRelation(
                scheduleDao.getAppRelation(appId = appId, scheduleId = schedule.id).firstOrNull() ?: BlockedApps()
            )

        }

        this.removeAppNameIfUnused()
    }

    override suspend fun deleteSchedule(schedule: Schedules) {
        scheduleDao.getAppRelationByScheduleId(schedule.id).first().forEach { blockedApps ->
            scheduleDao.deleteAppRelation(blockedApps)
        }
        this.removeAppNameIfUnused()
        scheduleDao.deleteSchedule(schedule)
    }

    private fun insertAppNameIfNotExists(app: String, id: Int?) {
        if (id == null || id == 0) { // it does not exist in table
            scheduleDao.insertAppNames(AppNames(title = app))
        }
    }

    private suspend fun removeAppNameIfUnused() {
        val appIds = scheduleDao.getAllApps().first()
        for (app in appIds) {
            if (scheduleDao.getAppRelationByAppId(app.id).first()
                    .isEmpty()
            ) { // app is not referenced
                scheduleDao.deleteAppNames(app)
            }
        }
    }
}