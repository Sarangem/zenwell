package com.sarangem.zenwell.service.alarmer

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.sarangem.zenwell.ServiceForeground
import com.sarangem.zenwell.ServiceForegroundFail
import com.sarangem.zenwell.ServiceOnCreate
import com.sarangem.zenwell.ServiceOnDestroy
import com.sarangem.zenwell.ServiceOnStart
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.makeVerboseServiceNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ResetAlarmService: Service() {

    private val context = this
    private val TAG = "ResetAlarmService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, ServiceOnCreate)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, ServiceOnStart)

        this.startForeground()
        Log.d(TAG, ServiceForeground)

        val schedulesRepository = (application as ZenwellApplication).container
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG,"Trying to reset alarms.")
            val schedulesList = schedulesRepository.getAllSchedules().first()
            ManageExactAlarms(
                context = context,
                schedulesList = schedulesList
            ).setExactAlarms()
            Log.d(TAG, "Alarms successfully reset.")
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startForeground() {

        try {
            val notification = makeVerboseServiceNotification(
                message = "Zenwell restarting...",
                context = this
            )
            ServiceCompat.startForeground(
                /* service = */ this,
                /* id = */ 101, // Cannot be 0
                /* notification = */ notification,
                /* foregroundServiceType = */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
                } else {
                    0
                },
            )
        } catch (e: Exception) {
            Log.e(TAG, ServiceForegroundFail)
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