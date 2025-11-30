package ru.lemonapes.easyprog.android

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow
import ru.lemonapes.easyprog.android.commands.CommandItem

data class MainViewState(
    val codeItems: ImmutableList<CodePeace> = persistentListOf(),
    val commandItems: ImmutableList<CommandItem> = persistentListOf(),
    val sourceItems: ImmutableList<CommandItem> = persistentListOf(),
    val showVictoryDialog: Boolean = false,
    val showTryAgainDialog: Boolean = false,
    val executingCommandIndex: Int? = null,
    val isHovered: Boolean = false,
    val itemIndexHovered: Int? = null,
)