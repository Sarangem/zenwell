package com.sarangem.zenwell.utils

import android.util.Log
import com.sarangem.zenwell.BuildConfig

object ServiceLogger {
    val isDebug = BuildConfig.DEBUG
    val tag = "AccessibilityService"

    // for non-important logging
    inline fun v(message: () -> String) {
        if (isDebug) Log.v(tag, message())
    }

    // for things related to opening or closing window
    inline fun d(message: () -> String) {
        if (isDebug) Log.d(tag, message())
    }

    // for opening or closing a window
    inline fun i(message: () -> String) {
        if (isDebug) Log.i(tag, message())
    }

    // for some exceptions opening/closing window
    inline fun w(message: () -> String) {
        if (isDebug) Log.w(tag, message())
    }

    // for errors
    inline fun e(message: () -> String, e: Exception? = null) {
        if (isDebug) Log.e(tag, message(), e)
    }
}