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

    /**
     * Проверяет валидность команды.
     * Возвращает true, если все необходимые параметры команды установлены.
     */
    fun validate(): Boolean
}

data class CommandResult(
    val newCodeItems: ImmutableList<CodePeace>,
    val nextCommandIndex: Int,
)