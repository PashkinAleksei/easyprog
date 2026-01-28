package ru.lemonapes.easyprog.android.commands

import androidx.compose.runtime.Immutable

@Immutable
sealed interface PairCommand : CommandItem {
    val pairId: Long // ID для связи парных команд
    val colorIndex: Int

    enum class PairType {
        FIRST,
        SECOND
    }
}