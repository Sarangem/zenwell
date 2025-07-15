package com.sarangem.zenwell.utils

import android.util.Log
import com.sarangem.zenwell.BuildConfig

object ServiceLogger {
    val isDebug = BuildConfig.DEBUG
    const val TAG = "AccessibilityService"

    // for non-important logging
    inline fun v(message: () -> String) {
        if (isDebug) Log.v(TAG, message())
    }

    // for things related to opening or closing window
    inline fun d(message: () -> String) {
        if (isDebug) Log.d(TAG, message())
    }

    // for opening or closing a window
    inline fun i(message: () -> String) {
        if (isDebug) Log.i(TAG, message())
    }

    // for some exceptions opening/closing window
    inline fun w(message: () -> String) {
        if (isDebug) Log.w(TAG, message())
    }

    // for errors
    inline fun e(message: () -> String, e: Exception? = null) {
        if (isDebug) Log.e(TAG, message(), e)
    }
}