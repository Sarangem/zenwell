package com.sarangem.zenwell.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.getCurrentTimeInMinutes
import com.sarangem.zenwell.service.alarmer.ManageExactAlarms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class AppBlockerService : AccessibilityService() {

    private val context = this
    private val TAG = "AppBlockerService"
    private val scheduleInfoList: MutableList<ScheduleInfo> = mutableListOf()

    companion object {
        var instance: AppBlockerService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = context

        CoroutineScope(Dispatchers.IO).launch {
            initializeRepository()
            Log.d(TAG, "Service fully initiated with ${context.serviceInfo}")
        }

    }

    suspend fun initializeRepository() {

        // cancel all old alarms
        ManageExactAlarms(
            context = context,
            schedulesList = scheduleInfoList.map { it.schedule }
        ).cancel()


        // update our variable with latest repository

        val schedulesRepository = (application as ZenwellApplication).container
        val schedulesList = schedulesRepository.getAllSchedules().first()

        scheduleInfoList.clear()
        schedulesList.forEach { schedule ->

            scheduleInfoList.add(
                ScheduleInfo(
                    context = context,
                    schedule = schedule,
                    appSet = schedulesRepository.getAppNames(schedule.id).first().toSet()
                )
            )

        }


        // create new alarms
        ManageExactAlarms(
            context = context,
            schedulesList = scheduleInfoList.map { it.schedule }
        ).set()

    }

    private var isWindowOpened = false
    var previousApp: CharSequence? = null
    val openedApps: MutableList<CharSequence> = mutableListOf()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        // get current package name and terminate if null
        val currentApp: CharSequence? = rootInActiveWindow?.packageName
        if(currentApp == null) return
        Log.d(TAG, "previous app is $previousApp and current app is $currentApp")

        // check for duplicate entries
        if (previousApp == currentApp) return

        // check if already opened with wait screen
        if (previousApp in openedApps) return

        // open or close the window
        for (scheduleInfo in scheduleInfoList) {

            if (!scheduleInfo.schedule.isEnabled) break
            if (getCurrentTimeInMinutes() !in scheduleInfo.schedule.startTimeInMinutes..scheduleInfo.schedule.endTimeInMinutes) break

            if (currentApp in scheduleInfo.appSet) {
                Log.d(TAG, "Opening window")
                isWindowOpened = true
                scheduleInfo.blockingWindow.open()
            } else {
                Log.d(TAG, "Closing window")
                isWindowOpened = false
                scheduleInfo.blockingWindow.close()
            }
        }

        previousApp = currentApp
    }

    fun closeWindow(scheduleId: Int) {
        val scheduleInfo = scheduleInfoList.firstOrNull { it.schedule.id == scheduleId }
        scheduleInfo?.blockingWindow?.close()
        isWindowOpened = false
    }

    override fun onInterrupt() {
        scheduleInfoList.forEach {
            it.blockingWindow.close()
        }
        isWindowOpened = false
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

}