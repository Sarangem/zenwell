package com.sarangem.zenwell.service.alarmer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sarangem.zenwell.ScheduleIdString

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getIntExtra(ScheduleIdString, 0) ?: 0
        startAppBlockerForegroundService(context, id)
    }
}