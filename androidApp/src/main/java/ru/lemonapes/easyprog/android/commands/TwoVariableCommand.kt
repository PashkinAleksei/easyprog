package ru.lemonapes.easyprog.android.commands

import androidx.compose.runtime.Immutable

@Immutable
sealed interface TwoVariableCommand : CommandItem {
    override val id: Long
    val target: Pair<String, Int>?
    val source: Pair<String, Int>?
}