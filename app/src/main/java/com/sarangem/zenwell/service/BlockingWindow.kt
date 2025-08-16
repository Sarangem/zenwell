package com.sarangem.zenwell.service

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sarangem.zenwell.data.tables.Schedules
import com.sarangem.zenwell.service.ui.BlockingScreenLifecycleOwner
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.utils.ServiceLogger
import com.sarangem.zenwell.utils.createNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockingWindow(
    val appName: String,
    val schedule: Schedules,
    var content: @Composable (Float, () -> Unit) -> Unit,
    private val context: Context,
) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutParams =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams() else null
    private val composeView = ComposeView(context)

    var isAppOpened = schedule.isPomodoro // true if pomodoro to prevent open() else false
    fun isWindowOpen(): Boolean = composeView.isAttachedToWindow


    fun open(bounds: Rect = Rect()) {

        if (composeView.isAttachedToWindow) {
            ServiceLogger.v { "Compose view already attached to window" }
            return
        }
        if (isAppOpened){
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
            val lifecycleOwner = BlockingScreenLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            composeView.setViewTreeLifecycleOwner(lifecycleOwner)
            composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

            // get screen size
            val density = context.resources.displayMetrics.density
            val appBarHeight = 56 * density
            val screenWidth = bounds.width()
            val screenHeight = if (screenWidth / density >= 840) {
                bounds.height()
            } else {
                bounds.height() - appBarHeight.toInt()
            }

            // set the layoutParams according to bound
            layoutParams?.apply {
                type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                format = PixelFormat.TRANSLUCENT
                gravity = Gravity.TOP or Gravity.START
                height = screenHeight
                width = screenWidth
                x = bounds.left
                y = bounds.top

                ServiceLogger.v { "Applying specific bounds to overlay: $bounds" }
            }

            // set the view
            composeView.apply {
                setContent {
                    ZenwellTheme {
                        content(screenWidth / density) { onTimerEnd() }
                    }
                }
            }
            windowManager.addView(composeView, layoutParams)

            // advance lifecycle for animation
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

            // ServiceLogger window opened
            ServiceLogger.d { "Successfully added composeView" }

        } catch (e: Exception) {

            ServiceLogger.e({ "Error adding ComposeView" }, e)

        }

    }

    fun close() {
        if (!composeView.isAttachedToWindow) return
        try {
            windowManager.removeView(composeView)
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
                    scheduleId = schedule.id,
                    scheduleName = schedule.title,
                    appName = appName,
                    context = context
                )
            }

            delay(schedule.notificationTimeInMinutes * 60 * 1000L)

            // re-trigger opening window
            ServiceLogger.d { "Rechecking the app." }
            val instance = AppBlockerService.instance ?: return@launch
            isAppOpened = false
            withContext(Dispatchers.Main) {
                instance.recheckApp()
            }

        }
    }

}