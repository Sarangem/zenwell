/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.SystemClock
import android.os.Build
import android.view.Display.DEFAULT_DISPLAY
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
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.service.pomodoro.PomodoroManager
import com.sarangem.zenwell.service.pomodoro.PomodoroStatus
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.createBlockNotification
import com.sarangem.zenwell.utils.deleteNotificationById
import com.sarangem.zenwell.utils.getElapsedRealtimeUntilTomorrow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds

class OverlayWindow(
    val targetApp: AppNames,
    val schedule: Schedules,
    var content: @Composable (() -> Unit, () -> Unit) -> Unit,
    private val service: AppBlockerService,
    supervisorJob: Job,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private val notificationId = "${schedule.id}$targetApp".hashCode().absoluteValue

    private val audioManager = service.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val focusChangeListener: AudioManager.OnAudioFocusChangeListener =
        object: AudioManager.OnAudioFocusChangeListener {
            override fun onAudioFocusChange(focusChange: Int) {
                if (schedule.playBackgroundMedia) return
                if (focusChange == AudioManager.AUDIOFOCUS_GAIN) return
                focusRequest?.let { audioManager.requestAudioFocus(it) }
            }
    }
    private val focusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(focusChangeListener)
        }.build()
    private val vmStore = ViewModelStore()

    private val windowManager = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var composeView: ComposeView? = null
    
    private var notifyAfterTime = 0L
    private var lockAfterTime = 0L

    fun open(pomodoroManager: PomodoroManager) {
        if (composeView != null) return
        if (schedule.isPomodoro) {
            val state = pomodoroManager.state.value
            ServiceLogger.d { "Current pomodoro session: $state"}
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
                val display = displayManager.getDisplay(DEFAULT_DISPLAY)
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
                            { onTimerEnd() },
                            { onExit() }
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
            ServiceLogger.d { "Added layoutParams $layoutParams" }
            windowManager.addView(composeView, layoutParams)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            service.openedWindows.add(this)
            if (!schedule.playBackgroundMedia) {
                focusRequest?.let { audioManager.requestAudioFocus(it) }
            }
            ServiceLogger.d { "Successfully added composeView" }

        } catch (e: Exception) {
            close()
            ServiceLogger.e({ "Error adding ComposeView" }, e)
        }

    }

    fun close() {
        if (composeView == null) return
        try {
            windowManager.removeView(composeView)
            if (!schedule.playBackgroundMedia) {
                focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            }
            vmStore.clear()
            composeView?.disposeComposition()
            ServiceLogger.d { "Successfully removed compose view" }
        } catch (e: Exception) {
            ServiceLogger.e({ "Error removing ComposeView" }, e)
        } finally {
            service.openedWindows.remove(this)
            composeView = null
        }
    }

    private fun onTimerEnd() {

        close()
        ServiceLogger.i { "Closing the window" }
        lockAfterTime = if (schedule.usageSessionDurationInMinutes == 0) {
            getElapsedRealtimeUntilTomorrow()
        } else {
            SystemClock.elapsedRealtime() + schedule.usageSessionDurationInMinutes * 60 * 1000L
        }
        notifyAfterTime = lockAfterTime - schedule.notificationTimeInMinutes * 60 * 1000L
        
        coroutineScope.launch {
            if (schedule.notificationTimeInMinutes != 0) {
                while (SystemClock.elapsedRealtime() < notifyAfterTime) delay(2.seconds)
                createBlockNotification(
                    id = notificationId,
                    message = schedule.title + service.getString(R.string.block_notification_message) + (targetApp.viewTitle ?: targetApp.packageName),
                    context = service,
                )
            }
            while (SystemClock.elapsedRealtime() < lockAfterTime) delay(2.seconds)
            deleteNotificationById(
                id = notificationId,
                context = service
            )
            ServiceLogger.d { "Rechecking the app." }
            withContext(Dispatchers.Main) {
                service.onAccessibilityEvent(null)
            }
        }
    }

    fun onExit() {
        service.performGlobalAction(GLOBAL_ACTION_HOME)
        close()
    }

}