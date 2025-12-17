package ru.lemonapes.easyprog.android.commands

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SingleVariableCommand : CommandItem {
    val target: Int?
}