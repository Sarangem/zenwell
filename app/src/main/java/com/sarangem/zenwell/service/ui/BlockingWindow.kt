package com.sarangem.zenwell.service.ui

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
import com.sarangem.zenwell.service.AppBlockerService
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
    private val context: Context,
    private val content: @Composable (Float, Float, () -> Unit) -> Unit,
) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutParams =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams() else null
    private val composeView = ComposeView(context)

    var isAppOpened = false

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

            ServiceLogger.d { "Rechecking the app." }

            // get the instance
            val instance = AppBlockerService.instance ?: return@launch

            // re trigger opening window
            isAppOpened = false
            instance.previousApp.forEach {
                if (it in instance.previousApp) {
                    // the user is still on blocked app
                    withContext(Dispatchers.Main) {
                        instance.recheckApp()
                    }
                }
            }

        }
    }


    fun open(bounds: Rect = Rect()) {

        if (composeView.isAttachedToWindow) return
        if (isAppOpened) return

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
                    content(screenHeight / density, screenWidth / density) { onTimerEnd() }
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

    fun isWindowOpen(): Boolean = composeView.isAttachedToWindow
}
