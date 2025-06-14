package com.sarangem.zenwell.service.alarmer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sarangem.zenwell.SCHEDULE_ID_STRING
import com.sarangem.zenwell.service.AppBlockerService

class EndTimeAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val id = intent?.getIntExtra(SCHEDULE_ID_STRING, 0) ?: 0
        AppBlockerService.instance?.closeWindow(id)

    }
}