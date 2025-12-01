package ru.lemonapes.easyprog.android.commands

import ru.lemonapes.easyprog.android.CodePeace

sealed interface CommandItem {
    val id: Long
    val text: String

    val stateId: String
    operator fun invoke(codeItems: List<CodePeace>): List<CodePeace>
}