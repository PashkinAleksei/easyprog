package ru.lemonapes.easyprog

import platform.Foundation.NSLog

actual object Logger {
    actual fun debug(tag: String, message: String) {
        if (isDebug()) NSLog("[$tag] $message")
    }

    actual fun error(tag: String, message: String) {
        if (isDebug()) NSLog("[ERROR][$tag] $message")
    }

    actual fun isDebug(): Boolean {
        return true // или можно настроить через build config
    }
}