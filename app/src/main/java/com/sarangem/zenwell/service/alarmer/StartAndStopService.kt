package com.sarangem.zenwell.service.alarmer

import android.content.Context
import android.content.Intent
import android.os.Build
import com.sarangem.zenwell.SCHEDULE_ID_STRING
import com.sarangem.zenwell.service.AppBlockerService

fun startAppBlockerForegroundService(context: Context?, id: Int) {

    val intent = Intent(context, AppBlockerService::class.java).putExtra(SCHEDULE_ID_STRING, id)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        context?.startForegroundService(intent)
    } else {
        context?.startService(intent)
    }
}

fun stopAppBlockerForegroundService(context: Context?, id: Int) {

    val intent = Intent(context, AppBlockerService::class.java).putExtra(SCHEDULE_ID_STRING, id)
    context?.stopService(intent)
}