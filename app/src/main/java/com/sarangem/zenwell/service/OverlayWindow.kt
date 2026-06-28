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
import kotlin.time.Duration.Companion.minutes

class OverlayWindow(
    val packageName: String, // only for use in AppBlockerService
    val appName: String,
    val schedule: Schedules,
    var content: @Composable (() -> Unit, () -> Unit) -> Unit,
    private val service: AppBlockerService,
    supervisorJob: Job,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private val notificationId = schedule.id + appName.hashCode()

    private val audioManager = service.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener {}
    private val focusRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(focusChangeListener)
        }.build()
    } else null

    private val windowManager = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var composeView: ComposeView? = null
    var isAppOpened = schedule.isPomodoro // true if pomodoro to prevent open() else false

    fun open() {
        if (composeView != null || isAppOpened) return

        try {
            val windowContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val displayManager = service.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                val display = displayManager.getDisplay(DEFAULT_DISPLAY)
                service.createWindowContext(display, WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, null)
            } else {
                service
            }
            composeView = ComposeView(windowContext)
            val viewModelStore = ViewModelStore()
            val viewModelStoreOwner = object : ViewModelStoreOwner {
                override val viewModelStore = viewModelStore
            }
            val lifecycleOwner = OverlayWindowLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            composeView?.apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)
                setViewTreeViewModelStoreOwner(viewModelStoreOwner)
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
            ServiceLogger.d { "Added layoutParams $layoutParams" }
            windowManager.addView(composeView, layoutParams)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            service.openedWindows.add(this)
            if (!schedule.playBackgroundMedia && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusRequest?.let { audioManager.requestAudioFocus(it) }
            } else if (!schedule.playBackgroundMedia) {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
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
            if (!schedule.playBackgroundMedia && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
            } else if (!schedule.playBackgroundMedia) {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(focusChangeListener)
            }
            ServiceLogger.d { "Successfully removed compose view" }
        } catch (e: Exception) {
            ServiceLogger.e({ "Error removing ComposeView" }, e)
        } finally {
            service.openedWindows.remove(this)
            composeView?.disposeComposition()
            composeView = null
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
            delay(delayTime.minutes)
            if (schedule.notificationTimeInMinutes > 0) {
                createBlockNotification(
                    id = notificationId,
                    message = schedule.title + service.getString(R.string.block_notification_message) + appName,
                    context = service,
                )
            }

            delay(schedule.notificationTimeInMinutes.minutes)
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

    fun onExit() {
        service.performGlobalAction(GLOBAL_ACTION_HOME)
        close()
    }

}