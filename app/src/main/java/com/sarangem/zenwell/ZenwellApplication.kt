package com.sarangem.zenwell

import android.app.Application
import com.sarangem.zenwell.data.database.repository.OfflineSchedulesRepository
import com.sarangem.zenwell.data.database.repository.SchedulesRepository
import com.sarangem.zenwell.data.database.ZenwellDatabase

class ZenwellApplication : Application() {

    lateinit var container: SchedulesRepository

    override fun onCreate() {
        super.onCreate()
        container = OfflineSchedulesRepository(ZenwellDatabase.getDatabase(this).scheduleDao())

    }

}