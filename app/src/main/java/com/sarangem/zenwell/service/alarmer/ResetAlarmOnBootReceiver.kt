package com.sarangem.zenwell.service.alarmer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class ResetAlarmOnBootReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action && context != null){

            val intent = Intent(context, ResetAlarmService::class.java)

            Log.d("ResetAlarmOnBoot","Resetting alarms on boot")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
