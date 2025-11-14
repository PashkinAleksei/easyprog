package ru.lemonapes.easyprog.android

import android.icu.util.Calendar

sealed class CodePeace {
    abstract val id: Long

    data class IntVariable(
        override val id: Long = Calendar.getInstance().timeInMillis,
        val name: String,
        var value: Int?,
        val isMutable: Boolean = true,
    ) : CodePeace()
}