package com.sarangem.zenwell.service.alarmer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sarangem.zenwell.ScheduleIdString
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.getCurrentTimeInMinutes

class ManageExactAlarms(
    private val context: Context,
    private val schedulesList: List<Schedules>
) {
    private val TAG = "ManageExactAlarms"
    private val alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    private fun createPendingIntent(scheduleId: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            100,
            Intent(context, AlarmReceiver::class.java).putExtra(ScheduleIdString, scheduleId),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun setExactAlarms(){

        schedulesList.forEach { schedule ->
            Log.d(TAG, "Setting repeating alarms for $schedule")
            alarmManager?.setRepeating(
                AlarmManager.RTC,
                schedule.startTimeInMinutes * 60 * 1000L,
                AlarmManager.INTERVAL_DAY,
                createPendingIntent(schedule.id)
            )

            val currentTime = getCurrentTimeInMinutes()
            if (schedule.startTimeInMinutes < currentTime &&
                schedule.endTimeInMinutes > currentTime
            ){
                Log.d(TAG,"Immediately starting foreground service for schedule id $schedule.id")
                startAppBlockerForegroundService(
                    context = context,
                    id = schedule.id
                )
            }
        }
    }

    fun cancelAllExactAlarms(){
        schedulesList.forEach { schedule ->
            alarmManager?.cancel(
                createPendingIntent(schedule.id)
            )
            stopAppBlockerForegroundService(
                context = context,
                id = schedule.id
            )
        }
    }
}