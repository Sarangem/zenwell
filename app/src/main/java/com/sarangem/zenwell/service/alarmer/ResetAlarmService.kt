package com.sarangem.zenwell.service.alarmer

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.sarangem.zenwell.R
import com.sarangem.zenwell.SERVICE_FOREGROUND
import com.sarangem.zenwell.SERVICE_FOREGROUND_FAIL
import com.sarangem.zenwell.SERVICE_ON_CREATE
import com.sarangem.zenwell.SERVICE_ON_DESTROY
import com.sarangem.zenwell.SERVICE_ON_START
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
        Log.d(TAG, SERVICE_ON_CREATE)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, SERVICE_ON_START)

        this.startForeground()
        Log.d(TAG, SERVICE_FOREGROUND)

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
                message = getString(R.string.notification_on_boot),
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
            Log.e(TAG, SERVICE_FOREGROUND_FAIL)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, SERVICE_ON_DESTROY)
    }
}