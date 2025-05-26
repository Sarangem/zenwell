package com.sarangem.zenwell

import android.app.Application
import com.sarangem.zenwell.data.OfflineSchedulesRepository
import com.sarangem.zenwell.data.SchedulesRepository
import com.sarangem.zenwell.data.ZenwellDatabase

class ZenwellApplication : Application() {

    lateinit var container: SchedulesRepository

    override fun onCreate() {
        super.onCreate()
        container = OfflineSchedulesRepository(ZenwellDatabase.getDatabase(this).scheduleDao())

    }

}