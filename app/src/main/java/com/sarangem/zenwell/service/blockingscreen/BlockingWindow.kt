package com.sarangem.zenwell.service.blockingscreen

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
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

class BlockingWindow(
    context: Context,
    content: @Composable (Float, Float) -> Unit
) {
    // define window manager
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val density = context.resources.displayMetrics.density
    private val screenHeight = context.resources.displayMetrics.heightPixels / density
    private val screenWidth = context.resources.displayMetrics.widthPixels / density

    // overlay window layout
    private val layoutParams =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams().apply {
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                format = PixelFormat.TRANSLUCENT
                gravity = Gravity.CENTER
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.MATCH_PARENT
            }
        } else {
            null
        }

    private val composeView = ComposeView(context).apply {
        setContent {
            content(screenHeight, screenWidth)
        }
    }


    fun open() {
        try {
            val viewModelStore = ViewModelStore()
            val viewModelStoreOwner = object : ViewModelStoreOwner {
                override val viewModelStore = viewModelStore
            }
            val lifecycleOwner = BlockingScreenLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            composeView.setViewTreeLifecycleOwner(lifecycleOwner)
            composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
            windowManager.addView(composeView, layoutParams)
        } catch (e: Exception) {
            Log.e("BlockingWindow", "Error adding ComposeView", e)
        }
    }

    fun close() {
        if (composeView.isAttachedToWindow == true) {
            try {
                windowManager.removeView(composeView)
            } catch (e: Exception) {
                Log.e("BlockingWindow", "Error removing ComposeView", e)
            } finally {
                composeView.disposeComposition()
            }
        }
    }
}
