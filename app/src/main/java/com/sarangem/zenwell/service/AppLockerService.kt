/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import com.sarangem.zenwell.data.repository.SchedulesRepository
import com.sarangem.zenwell.service.overlay.OverlayWindow
import com.sarangem.zenwell.service.pomodoro.PomodoroManager
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@SuppressLint("AccessibilityPolicy")
@AndroidEntryPoint
class AppLockerService : AccessibilityService() {

    companion object { var instance: AppLockerService? = null }

    val supervisorJob = SupervisorJob()
    val scheduleInfoList: MutableList<ScheduleInfo> = mutableListOf()
    @Inject lateinit var schedulesRepository: SchedulesRepository
    @Inject lateinit var pomodoroManager: PomodoroManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        CoroutineScope(Dispatchers.IO).launch {
            initializeRepository()
        }
    }

    suspend fun initializeRepository() {
        supervisorJob.cancelChildren()

        val schedulesList = schedulesRepository.getAllSchedules().first()
            .filter { it.isActive && it.weekDays.isNotEmpty() }
        scheduleInfoList.clear()
        schedulesList.forEach { schedule ->
            scheduleInfoList.add(
                ScheduleInfo(
                    service = this,
                    schedule = schedule,
                    appNamesList = schedulesRepository.getAppNamesById(schedule.id).first(),
                    supervisorJob = supervisorJob
                )
            )
        }
        scheduleInfoList.removeAll { !it.schedule.isPomodoro && it.appNamesList.isEmpty() }

        val info = serviceInfo
        info.eventTypes = when {
            scheduleInfoList.isEmpty() -> 0
            scheduleInfoList.any { scheduleInfo ->
                scheduleInfo.appNamesList.any { appRule -> appRule.viewId != null || appRule.viewKeyword != null }
            } -> {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            }
            else -> AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        }
        serviceInfo = info
        Timber.i("Service fully initiated with $serviceInfo")
        Timber.i("Loaded: $scheduleInfoList")

        withContext(Dispatchers.Main) {
            checkAndLockApps()
        }
    }

    val openedWindows = mutableListOf<OverlayWindow>()
    var lastCheckedTime: Long = 0L
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if ( (event.contentChangeTypes and (
                AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE or
                AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT or
                AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION
            )) == 0 ) return
            val time = SystemClock.elapsedRealtime()
            if (time < lastCheckedTime) return else lastCheckedTime = time + 2000L
        }

        checkAndLockApps(event?.windowId)
    }

    override fun onInterrupt() {
        scheduleInfoList.forEach { scheduleInfo ->
            scheduleInfo.overlayWindowList.forEach { overlayWindow ->
                overlayWindow.close()
                overlayWindow.deleteNotifications()
            }
        }
        supervisorJob.cancel()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }
}