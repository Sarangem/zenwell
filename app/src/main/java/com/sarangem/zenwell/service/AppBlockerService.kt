package com.sarangem.zenwell.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.sarangem.zenwell.R
import com.sarangem.zenwell.SCHEDULE_ID_STRING
import com.sarangem.zenwell.SERVICE_FOREGROUND
import com.sarangem.zenwell.SERVICE_FOREGROUND_FAIL
import com.sarangem.zenwell.SERVICE_NO_PERMISSION
import com.sarangem.zenwell.SERVICE_ON_CREATE
import com.sarangem.zenwell.SERVICE_ON_DESTROY
import com.sarangem.zenwell.SERVICE_ON_START
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
    private val isDeviceOld = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val activeCoroutines = AtomicInteger(0)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, SERVICE_ON_CREATE)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, SERVICE_ON_START)

        val status = this.startForeground()
        if (!status) {
            Log.d(TAG, SERVICE_NO_PERMISSION)
            stopSelf()
            return START_NOT_STICKY
        }
        Log.d(TAG, SERVICE_FOREGROUND)

        val schedulesRepository = (application as ZenwellApplication).container
        val scheduleId = intent.getIntExtra(SCHEDULE_ID_STRING, 0)

        Log.d(TAG, "Creating thread for schedule id $scheduleId")
        coroutineScope.async {
            activeCoroutines.incrementAndGet()
            checkAndBlockApp(
                context = context,
                schedule = schedulesRepository.getScheduleInfoById(scheduleId).firstOrNull(),
                appList = schedulesRepository.getAppNames(scheduleId).first(),
                coroutineScope = this
            )
            if (activeCoroutines.decrementAndGet() == 0) {
                stopSelf()
            }
        }

        return START_STICKY
    }

    private fun startForeground(): Boolean {

        // Set foreground service type
        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
        } else {
            0
        }

        // Check for permissions
        val overlayPermission = checkSystemAlertWindowPermission(this)
        val usageStatsPermission = checkPackageUsageStatsPermission(this)

        // Stop if permissions not granted
        if (!overlayPermission || !usageStatsPermission) {
            // Create a false foreground service
            val notification = makeVerboseServiceNotification(
                context = this,
                message = getString(R.string.notification_permission_not_granted)
            )
            ServiceCompat.startForeground(this, 100, notification, serviceType)
            return false
        }

        // Promote itself to foreground
        try {
            val notification = makeVerboseServiceNotification(
                context = this,
                message = getString(R.string.notification_service_running)
            )
            ServiceCompat.startForeground(
                /* service = */ this,
                /* id = */ 100, // Cannot be 0
                /* notification = */ notification,
                /* foregroundServiceType = */ serviceType
            )
            return true
        } catch (e: Exception) {
            Log.e(TAG, SERVICE_FOREGROUND_FAIL, e)
            return false
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        if (isDeviceOld) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        Log.w(TAG, SERVICE_ON_DESTROY)
    }
}

