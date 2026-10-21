/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.overlay

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.SystemClock
import android.view.Display
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sarangem.zenwell.R
import com.sarangem.zenwell.model.AppNames
import com.sarangem.zenwell.model.Schedules
import com.sarangem.zenwell.service.AppLockerService
import com.sarangem.zenwell.service.checkAndLockApps
import com.sarangem.zenwell.service.notifications.createAppLockNotification
import com.sarangem.zenwell.service.notifications.deleteNotificationById
import com.sarangem.zenwell.service.pomodoro.PomodoroManager
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

class OverlayWindow(
    val targetApp: AppNames,
    val schedule: Schedules,
    var content: @Composable (() -> Unit, () -> Unit) -> Unit,
    private val service: AppLockerService,
    supervisorJob: Job,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private val notificationId = "${schedule.id}$targetApp".hashCode().absoluteValue
    private val audioFocus = if (!schedule.playBackgroundMedia) AudioFocusController(service) else null
    private val vmStore = ViewModelStore()
    private val windowManager = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var composeView: ComposeView? = null

    private var notifyAfterTime = 0L
    private var lockAfterTime = 0L

    fun open(pomodoroManager: PomodoroManager) {
        if (composeView != null) return
        if (schedule.isPomodoro) {
            val state = pomodoroManager.state.value
            if (
                state.schedule.id != schedule.id ||
                state.status != PomodoroStatus.RUNNING ||
                !state.isWorkTime
            ) return
        } else {
            if (SystemClock.elapsedRealtime() < lockAfterTime) return
        }

        try {
            val windowContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val displayManager = service.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
                service.createWindowContext(
                    /* display = */ display,
                    /* type = */ WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    /* options = */ null
                )
            } else {
                service
            }
            composeView = ComposeView(windowContext)
            val vmStoreOwner = object : ViewModelStoreOwner {
                override val viewModelStore = vmStore
            }
            val lifecycleOwner = OverlayWindowLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            composeView?.apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)
                setViewTreeViewModelStoreOwner(vmStoreOwner)
                setContent {
                    ZenwellTheme {
                        content(
                            /* onTimerEnd = */ { onTimerEnd() },
                            /* onExit = */ {
                                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
                                close()
                            }
                        )
                    }
                }
            }
            val layoutParams = WindowManager.LayoutParams().apply {
                type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                format = PixelFormat.TRANSPARENT
                flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                gravity = Gravity.TOP or Gravity.START
                height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val navBar = windowManager
                        .currentWindowMetrics
                        .windowInsets
                        .getInsets(WindowInsets.Type.navigationBars())
                        .bottom
                    windowManager.currentWindowMetrics.bounds.height() - navBar
                } else {
                    WindowManager.LayoutParams.MATCH_PARENT
                }
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                width = WindowManager.LayoutParams.MATCH_PARENT
            }
            Timber.d("Added layoutParams $layoutParams")
            windowManager.addView(composeView, layoutParams)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            service.openedWindows.add(this)
            audioFocus?.request()
            Timber.d("Successfully added composeView")

        } catch (e: Exception) {
            close()
            Timber.e(e, "Error adding ComposeView")
        }

    }

    fun close() {
        if (composeView == null) return
        try {
            windowManager.removeView(composeView)
            audioFocus?.abandon()
            vmStore.clear()
            composeView?.disposeComposition()
            Timber.d("Successfully removed compose view")
        } catch (e: Exception) {
            Timber.e(e, "Error removing ComposeView")
        } finally {
            service.openedWindows.remove(this)
            composeView = null
        }
    }

    private fun onTimerEnd() {

        close()
        Timber.i("Closing window for schedule ${schedule.id} and $targetApp")
        lockAfterTime = if (schedule.usageSessionDurationInMinutes == 0) {
            // elapsed time until tomorrow
            val now = ZonedDateTime.now()
            val midnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone)
            Duration.between(now, midnight).toMillis()
        } else {
            schedule.usageSessionDurationInMinutes * 60 * 1000L
        } + SystemClock.elapsedRealtime()
        notifyAfterTime = lockAfterTime - schedule.notificationTimeInMinutes * 60 * 1000L

        coroutineScope.launch {
            if (schedule.notificationTimeInMinutes != 0) {
                while (SystemClock.elapsedRealtime() < notifyAfterTime) delay(2.seconds)
                createAppLockNotification(
                    id = notificationId,
                    message = schedule.title +
                            service.getString(R.string.app_lock_notification_message) +
                            (targetApp.viewTitle ?: targetApp.packageName),
                    context = service,
                )
            }
            while (SystemClock.elapsedRealtime() < lockAfterTime) delay(2.seconds)
            deleteNotificationById(
                id = notificationId,
                context = service
            )
            Timber.d("Rechecking the app")
            withContext(Dispatchers.Main) {
                service.checkAndLockApps()
            }
        }
    }

    fun deleteNotifications() = deleteNotificationById(notificationId, service)
}