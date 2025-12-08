package ru.lemonapes.easyprog.android

import android.icu.util.Calendar

sealed class CodePeace {
    abstract val id: Long

    data class IntVariable(
        override val id: Long = Calendar.getInstance().timeInMillis,
        var value: Int?,
        val isMutable: Boolean = true,
        val colorIndex: Int,
    ) : CodePeace()
}