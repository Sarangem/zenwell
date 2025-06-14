package com.sarangem.zenwell.service.alarmer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sarangem.zenwell.SCHEDULE_ID_STRING
import com.sarangem.zenwell.data.tables.Schedules

class ManageExactAlarms(
    private val context: Context,
    private val schedulesList: List<Schedules>
) {
    private val alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    private fun createPendingIntent(scheduleId: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            100,
            Intent(context, EndTimeAlarmReceiver::class.java).putExtra(SCHEDULE_ID_STRING, scheduleId),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun set(){
        schedulesList.forEach { schedule ->

            alarmManager?.setRepeating(
                AlarmManager.RTC,
                schedule.endTimeInMinutes * 60 * 1000L,
                AlarmManager.INTERVAL_DAY,
                createPendingIntent(schedule.id)
            )

        }
    }

    fun cancel(){
        schedulesList.forEach { schedule ->

            alarmManager?.cancel(
                createPendingIntent(schedule.id)
            )

        }
    }
}