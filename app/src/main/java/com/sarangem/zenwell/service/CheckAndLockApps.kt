/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service

import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.sarangem.zenwell.model.AppNames
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import kotlin.collections.forEach

fun AppLockerService.checkAndLockApps(eventWindowId: Int? = null) {
    Timber.d("There are ${windows.size} and they are $windows")

    val currentVisibleTargets = mutableListOf<AppNames>()
    val currentlyActiveSchedules = scheduleInfoList.filterActiveSchedules()

    for (windowInfo in windows) {

        if (windowInfo.type != AccessibilityWindowInfo.TYPE_APPLICATION) continue
        if (windowInfo.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD &&
            eventWindowId == windowInfo.id
        ) continue // probably a keyboard type
        if (windowInfo.isInPictureInPictureMode) continue

        val root = windowInfo.root ?: continue
        val currentPackage = root.packageName?.toString() ?: continue
        if (currentPackage == packageName && root.className == "android.view.ViewGroup") continue
        Timber.d("Checking schedule for app $currentPackage")

        currentlyActiveSchedules.forEach { scheduleInfo ->
            scheduleInfo.appsByPackage[currentPackage]?.forEach { appRule ->
                if (!root.matches(appRule)) return@forEach
                val overlayWindow = scheduleInfo.overlayWindowList
                    .firstOrNull { it.targetApp.id == appRule.id } ?: return@forEach
                currentVisibleTargets.add(overlayWindow.targetApp)
                Timber.i("Opening window for schedule ${scheduleInfo.schedule.id} and ${overlayWindow.targetApp}")
                overlayWindow.open(pomodoroManager)
            }
        }

        windowInfo.recycle()
    }

    openedWindows
        .filter { it.targetApp !in currentVisibleTargets }
        .forEach { overlayWindow ->
            Timber.i("Closing window for schedule ${overlayWindow.schedule.id} and ${overlayWindow.targetApp}")
            overlayWindow.close()
        }
    Timber.d("Accessibility Event completed")
}

private fun List<ScheduleInfo>.filterActiveSchedules(): List<ScheduleInfo> {
    val currentTime = LocalTime.now().toSecondOfDay() / 60
    val todayDay = LocalDate.now().dayOfWeek.value

    return filter { scheduleInfo ->
        if (todayDay !in scheduleInfo.schedule.weekDays) return@filter false

        with(scheduleInfo.schedule) {
            // both times inclusive
            if (startTimeInMinutes <= endTimeInMinutes) {
                currentTime in startTimeInMinutes..endTimeInMinutes
            } else {
                // run from start time to midnight and (next day) midnight to end time
                currentTime >= startTimeInMinutes || currentTime <= endTimeInMinutes
            }
        }
    }
}

private fun AccessibilityNodeInfo.matches(rule: AppNames): Boolean {
    val nodes = rule.viewId?.let(::findAccessibilityNodeInfosByViewId) ?: listOf(this)
    if (nodes.isEmpty()) return false
    val keyword = rule.viewKeyword ?: return true
    return nodes.any { node ->
        node.text?.contains(keyword, ignoreCase = true) == true ||
            node.contentDescription?.contains(keyword, ignoreCase = true) == true ||
            node.findAccessibilityNodeInfosByText(keyword).isNotEmpty()
    }
}