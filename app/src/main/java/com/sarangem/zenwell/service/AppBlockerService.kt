package com.sarangem.zenwell.service

import android.app.AppOpsManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.core.app.ServiceCompat
import com.sarangem.zenwell.ScheduleIdString
import com.sarangem.zenwell.ServiceForeground
import com.sarangem.zenwell.ServiceForegroundFail
import com.sarangem.zenwell.ServiceNoPermission
import com.sarangem.zenwell.ServiceOnCreate
import com.sarangem.zenwell.ServiceOnDestroy
import com.sarangem.zenwell.ServiceOnStart
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.makeVerboseServiceNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppBlockerService : Service() {

    private val TAG = "AppBlocker Service"
    private val context = this

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, ServiceOnCreate)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, ServiceOnStart)

        val status = this.startForeground()
        if (status != 0) {
            Log.d(TAG, ServiceNoPermission)
            return START_NOT_STICKY
        }
        Log.d(TAG, ServiceForeground)

        val schedulesRepository = (application as ZenwellApplication).container
        val scheduleId = intent.getIntExtra(ScheduleIdString, 0)

        Log.d(TAG, "Creating thread for schedule id $scheduleId")
        CoroutineScope(Dispatchers.IO).launch {
            checkAndBlockApp(
                context = context,
                schedule = schedulesRepository.getScheduleInfoById(scheduleId).firstOrNull(),
                appList = schedulesRepository.getAppNames(scheduleId).firstOrNull(),
                coroutineScope = this
            )
        }

        return START_STICKY
    }


    private fun startForeground(): Int {

        val overlayPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }

        val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                packageName
            )
        }
        val usageStatsPermission = (mode == AppOpsManager.MODE_ALLOWED)

        if (!overlayPermission || !usageStatsPermission) {
            return 1
        }

        try {
            val notification = makeVerboseServiceNotification(
                message = "Zenwell checking for blocked apps...",
                context = this
            )
            ServiceCompat.startForeground(
                /* service = */ this,
                /* id = */ 100, // Cannot be 0
                /* notification = */ notification,
                /* foregroundServiceType = */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
                } else {
                    0
                },
            )
            return 0
        } catch (e: Exception) {
            Log.e(TAG, ServiceForegroundFail, e)
            return 2
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, ServiceOnDestroy)
    }
}

