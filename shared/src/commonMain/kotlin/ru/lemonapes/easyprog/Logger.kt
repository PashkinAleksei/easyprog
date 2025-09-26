package ru.lemonapes.easyprog

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object Logger {
    fun debug(tag: String, message: String)
    fun error(tag: String, message: String)
    fun isDebug(): Boolean
}