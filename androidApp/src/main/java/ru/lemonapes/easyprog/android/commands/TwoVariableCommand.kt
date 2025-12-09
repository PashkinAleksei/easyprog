package ru.lemonapes.easyprog.android.commands

import androidx.compose.runtime.Immutable

@Immutable
sealed interface TwoVariableCommand : CommandItem {
    val target: Int?
    val source: Int?
}