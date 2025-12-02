package com.sarangem.zenwell.service

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sarangem.zenwell.R
import com.sarangem.zenwell.data.NotificationChannels
import com.sarangem.zenwell.data.database.tables.Schedules
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.createNotification
import com.sarangem.zenwell.utils.deleteNotificationById
import com.sarangem.zenwell.utils.getAppNameFromPackageName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverlayWindow(
    val appName: String,
    val schedule: Schedules,
    var content: @Composable (() -> Unit) -> Unit,
    private val service: AppBlockerService,
) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val windowManager = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutParams =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams() else null
    private val composeView = ComposeView(service)

    var isAppOpened = schedule.isPomodoro // true if pomodoro to prevent open() else false

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    fun open(bounds: Rect = Rect()) {

        if (composeView.isAttachedToWindow) {
            ServiceLogger.v { "Compose view already attached to window" }
            return
        }
        if (isAppOpened) {
            ServiceLogger.v { "App is set to opened." }
            return
        }

        try {

            // set view model
            val viewModelStore = ViewModelStore()
            val viewModelStoreOwner = object : ViewModelStoreOwner {
                override val viewModelStore = viewModelStore
            }

            // set lifecycle
            val lifecycleOwner = OverlayWindowLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            composeView.setViewTreeLifecycleOwner(lifecycleOwner)
            composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

            // set the layoutParams according to bound
            layoutParams?.apply {
                type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                flags =
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                format = PixelFormat.TRANSLUCENT
                gravity = Gravity.TOP or Gravity.START
                height = bounds.height()
                width = bounds.width()
                x = bounds.left
                y = bounds.top

                ServiceLogger.v { "Applying specific bounds to overlay: $bounds" }
            }

            // set the view
            composeView.apply {
                setContent {
                    ZenwellTheme {
                        content { onTimerEnd() }
                    }
                }
            }
            windowManager.addView(composeView, layoutParams)

            // advance lifecycle for animation
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

            // log window opened
            service.openedWindows.add(this)
            ServiceLogger.d { "Successfully added composeView" }

        } catch (e: Exception) {

            ServiceLogger.e({ "Error adding ComposeView" }, e)

        }

    }

    fun close() {
        if (!composeView.isAttachedToWindow) return
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
            val delayTime = schedule.openTimeInMinutes - schedule.notificationTimeInMinutes
            if (delayTime < 0) return@launch
            delay(delayTime * 60 * 1000L)
            if (schedule.notificationTimeInMinutes > 0) {
                createNotification(
                    id = schedule.id + appName.hashCode(),
                    message = schedule.title + service.getString(R.string.block_notification_message) + getAppNameFromPackageName(
                        service,
                        appName
                    ),
                    context = service,
                    notificationChannel = NotificationChannels.BlockNotification
                )
            }

            delay(schedule.notificationTimeInMinutes * 60 * 1000L)
            deleteNotificationById(
                id = schedule.id + appName.hashCode(),
                context = service
            )

            // re-trigger opening window
            ServiceLogger.d { "Rechecking the app." }
            isAppOpened = false
            withContext(Dispatchers.Main) {
                service.recheckApp()
            }

        }
    }

}