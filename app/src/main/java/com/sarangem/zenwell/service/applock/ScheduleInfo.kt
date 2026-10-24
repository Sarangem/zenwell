/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.applock

import com.sarangem.zenwell.domain.packageinfo.getAppNameFromPackageName
import com.sarangem.zenwell.model.AppNames
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.ui.overlay.OverlayScreen
import kotlinx.coroutines.Job

data class ScheduleInfo(
    private val service: AppLockerService,
    val schedule: Schedules,
    val appNamesList: List<AppNames>,
    val supervisorJob: Job
) {
    val overlayWindowList: List<OverlayWindow> = appNamesList.map { app ->
        OverlayWindow(
            service = service,
            schedule = schedule,
            targetApp = app.copy(
                viewTitle = app.viewTitle ?: getAppNameFromPackageName(service, app.packageName)
                ?: app.packageName
            ),
            supervisorJob = supervisorJob,
            audioFocus = service.audioFocusController,
            content = { onTimerEnd, onExit, tasksList, updateTasksList ->
                OverlayScreen(
                    schedule = schedule,
                    pomodoroManager = service.pomodoroManager,
                    tasksList = tasksList,
                    updateTasksList = updateTasksList,
                    onTimerEnd = onTimerEnd,
                    onExit = onExit
                )
            }
        )
    }

    val overlaysByPackage = overlayWindowList.groupBy { it.targetApp.packageName }
}