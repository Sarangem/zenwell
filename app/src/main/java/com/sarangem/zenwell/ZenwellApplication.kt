package com.sarangem.zenwell

import android.app.Application
import com.sarangem.zenwell.database.repository.OfflineSchedulesRepository
import com.sarangem.zenwell.database.repository.SchedulesRepository
import com.sarangem.zenwell.database.ZenwellDatabase

class ZenwellApplication : Application() {

    lateinit var container: SchedulesRepository

    override fun onCreate() {
        super.onCreate()
        container = OfflineSchedulesRepository(ZenwellDatabase.getDatabase(this).scheduleDao())

    }

}