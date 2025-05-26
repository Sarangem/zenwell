package com.sarangem.zenwell.data

import com.sarangem.zenwell.data.tables.AppNames
import com.sarangem.zenwell.data.tables.BlockedApps
import com.sarangem.zenwell.data.tables.Schedules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

interface SchedulesRepository {
    fun getAllSchedules(): Flow<List<Schedules>>
    fun getScheduleInfoById(id: Int): Flow<Schedules>
    fun getAppNames(id: Int): Flow<List<String>>
    suspend fun deleteSchedule(schedule: Schedules)
    suspend fun addNewSchedule(schedule: Schedules): Int
    suspend fun saveToDatabase(
        schedule: Schedules,
        appNames: List<String>,
        pastAppList: MutableList<String>
    )
}

class OfflineSchedulesRepository(private val scheduleDao: ScheduleDao) : SchedulesRepository {

    override fun getAllSchedules(): Flow<List<Schedules>> = scheduleDao.getAllSchedules()
    override fun getScheduleInfoById(id: Int): Flow<Schedules> = scheduleDao.getScheduleInfoById(id)
    override fun getAppNames(id: Int): Flow<List<String>> = scheduleDao.getAppNames(id)
    override suspend fun addNewSchedule(schedule: Schedules): Int =
        scheduleDao.addNewSchedule(schedule).toInt()

    override suspend fun saveToDatabase(
        schedule: Schedules,
        appNames: List<String>,
        pastAppList: MutableList<String>
    ) {
        // first update the schedule table
        scheduleDao.updateSchedule(schedules = schedule)

        for (app in appNames) {

            // insert app if not exists
            this.insertAppNameIfNotExists(
                app = app,
                id = scheduleDao.getAppId(app).firstOrNull()
            )

            // update app to schedule relation
            if (app !in pastAppList) {
                scheduleDao.insertAppRelation(
                    BlockedApps(
                        scheduleId = schedule.id,
                        appId = scheduleDao.getAppId(app).firstOrNull() ?: 0
                    )
                )
            } else {
                pastAppList.remove(app)
            }
        }

        for (app in pastAppList) { // remove unnecessary schedule relations
            val scheduleId = schedule.id
            val appId = scheduleDao.getAppId(app).firstOrNull() ?: -1

            scheduleDao.deleteAppRelation(
                scheduleDao.getAppRelation(appId = appId, scheduleId = scheduleId).first()
            )
        }

        this.removeAppNameIfUnused()
    }

    override suspend fun deleteSchedule(schedule: Schedules) {
        scheduleDao.deleteSchedule(schedule)
        scheduleDao.getAppRelationByScheduleId(schedule.id).first().forEach { blockedApps ->
            scheduleDao.deleteAppRelation(blockedApps)
        }
        this.removeAppNameIfUnused()
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