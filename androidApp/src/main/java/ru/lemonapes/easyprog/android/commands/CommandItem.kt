package ru.lemonapes.easyprog.android.commands

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import ru.lemonapes.easyprog.android.CodePeace

@Immutable
sealed interface CommandItem {
    val id: Long
    val textRes: Int
    val iconRes: Int

    val stateId: String
    fun mkCopy(): CommandItem
    fun execute(
        codeItems: ImmutableList<CodePeace>,
        commandItems: ImmutableList<CommandItem>,
        currentCommandIndex: Int,
    ): CommandResult
}

data class CommandResult(
    val newCodeItems: ImmutableList<CodePeace>,
    val nextCommandIndex: Int,
)