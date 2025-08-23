package com.sarangem.zenwell.utils

import com.sarangem.zenwell.service.AppBlockerService
import com.sarangem.zenwell.service.PomodoroWindow

fun checkAccessibilityServicePermission(): Boolean {
    return (AppBlockerService.instance != null)
}

fun isExpandedWidth(width: Float): Boolean {
    return width >= 840
}

fun getPomodoroWindow(scheduleId: Int): PomodoroWindow? {
    return AppBlockerService.instance?.scheduleInfoList?.firstOrNull { it.schedule.id == scheduleId}?.pomodoroWindow
}