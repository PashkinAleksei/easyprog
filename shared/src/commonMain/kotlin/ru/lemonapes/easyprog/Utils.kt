package ru.lemonapes.easyprog

import kotlin.time.measureTime

class Utils {
    companion object {
        private var TAG = "myLogs"

        fun <T> log(obj: T?) {
            Logger.debug(TAG, obj.toString())
        }

        fun <T> logErr(obj: T?) {
            Logger.error(TAG, obj.toString())
        }

        inline fun <T> logWithTimeTrack(func: (() -> T?)) {
            if (Logger.isDebug()) {
                val res: T?
                val time = measureTime {
                    res = func.invoke()
                }
                log("$res msWasted: ${time.inWholeMilliseconds}")
            }
        }

        inline fun logWithMiddleTimeTrack(func: (() -> Int)) {
            if (Logger.isDebug()) {
                var sum = 0L
                var res = 0

                //for (i in 0..100) {
                sum += measureTime {
                    res = func.invoke()
                }.inWholeMilliseconds
                //}
                log("$res msWasted: $sum")
            }
        }
    }
}