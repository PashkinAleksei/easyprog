package ru.lemonapes.easyprog

import android.util.Log

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Logger {
    actual fun debug(tag: String, message: String) {
        if (isDebug()) Log.d(tag, message)
    }
    
    actual fun error(tag: String, message: String) {
        if (isDebug()) Log.e(tag, message)
    }
    
    actual fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}