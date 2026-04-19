/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.createBlockNotification
import com.sarangem.zenwell.utils.deleteNotificationById
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverlayWindow(
    val packageName: String, // only for use in AppBlockerService
    val appName: String,
    val schedule: Schedules,
    var content: @Composable (() -> Unit) -> Unit,
    private val service: AppBlockerService,
    supervisorJob: Job,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private val notificationId = schedule.id + appName.hashCode()

    private val windowManager = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val composeView = ComposeView(service)
    var isAppOpened = schedule.isPomodoro // true if pomodoro to prevent open() else false
    var isAttachedToWindow = false

    fun open() {
        ServiceLogger.d { "Attached to window: ${composeView.isAttachedToWindow} and app opened: $isAppOpened" }
        if (isAttachedToWindow || isAppOpened) return

        try {
            isAttachedToWindow = true
            val viewModelStore = ViewModelStore()
            val viewModelStoreOwner = object : ViewModelStoreOwner {
                override val viewModelStore = viewModelStore
            }
            val lifecycleOwner = OverlayWindowLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            composeView.apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)
                setViewTreeViewModelStoreOwner(viewModelStoreOwner)
                setContent {
                    ZenwellTheme {
                        content { onTimerEnd() }
                    }
                }
            }
            val layoutParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams().apply {
                    type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                    format = PixelFormat.TRANSPARENT
                    flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    gravity = Gravity.TOP or Gravity.START
                    height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val navBar = windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom
                        windowManager.currentWindowMetrics.bounds.height() - navBar
                    } else {
                        WindowManager.LayoutParams.MATCH_PARENT
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    }
                    width = WindowManager.LayoutParams.MATCH_PARENT
                }
            } else null
            ServiceLogger.d { "Added layoutParams $layoutParams" }
            windowManager.addView(composeView, layoutParams)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            service.openedWindows.add(this)
            ServiceLogger.d { "Successfully added composeView" }

        } catch (e: Exception) {
            isAttachedToWindow = false
            ServiceLogger.e({ "Error adding ComposeView" }, e)
        }

    }

    fun close() {
        if (!isAttachedToWindow) return
        isAttachedToWindow = false
        try {
            windowManager.removeView(composeView)
            service.openedWindows.remove(this)
            ServiceLogger.d { "Successfully removed compose view" }
        } catch (e: Exception) {
            ServiceLogger.e({ "Error removing ComposeView" }, e)
        } finally {
            composeView.disposeComposition()
        }
    }

    private fun onTimerEnd() {

        close() // close the window
        ServiceLogger.i { "Closing the window" }
        isAppOpened = true // add to opened apps

        coroutineScope.launch {

            // send notification
            val delayTime = schedule.usageSessionDurationInMinutes - schedule.notificationTimeInMinutes
            if (delayTime < 0) return@launch
            delay(delayTime * 60 * 1000L)
            if (schedule.notificationTimeInMinutes > 0) {
                createBlockNotification(
                    id = notificationId,
                    message = schedule.title + service.getString(R.string.block_notification_message) + appName,
                    context = service,
                )
            }

            delay(schedule.notificationTimeInMinutes * 60 * 1000L)
            deleteNotificationById(
                id = notificationId,
                context = service
            )

            // re-trigger opening window
            ServiceLogger.d { "Rechecking the app." }
            isAppOpened = false
            withContext(Dispatchers.Main) {
                service.onAccessibilityEvent(null)
            }

        }
    }

}