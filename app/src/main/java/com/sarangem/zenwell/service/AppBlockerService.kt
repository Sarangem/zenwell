package com.sarangem.zenwell.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
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
import com.sarangem.zenwell.checkPackageUsageStatsPermission
import com.sarangem.zenwell.checkSystemAlertWindowPermission
import com.sarangem.zenwell.makeVerboseServiceNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.atomic.AtomicInteger

class AppBlockerService : Service() {

    private val TAG = "AppBlocker Service"
    private val context = this
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val activeCoroutines = AtomicInteger(0)

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
            stopSelf()
        }
        Log.d(TAG, ServiceForeground)

        val schedulesRepository = (application as ZenwellApplication).container
        val scheduleId = intent.getIntExtra(ScheduleIdString, 0)

        Log.d(TAG, "Creating thread for schedule id $scheduleId")
        activeCoroutines.incrementAndGet()
        coroutineScope.async {
            try {
                checkAndBlockApp(
                    context = context,
                    schedule = schedulesRepository.getScheduleInfoById(scheduleId).firstOrNull(),
                    appList = schedulesRepository.getAppNames(scheduleId).first(),
                    coroutineScope = this
                )
            } finally {
                if (activeCoroutines.decrementAndGet() == 0) {
                    stopSelf()
                }
            }
        }

        return START_STICKY
    }

    private fun startForeground(): Int {

        val overlayPermission = checkSystemAlertWindowPermission(this)
        val usageStatsPermission = checkPackageUsageStatsPermission(this)
        if (!overlayPermission || !usageStatsPermission) {
            return 1
        }

        try {
            val notification = makeVerboseServiceNotification(
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
        coroutineScope.cancel()
        Log.w(TAG, ServiceOnDestroy)
    }
}

