package ru.lemonapes.easyprog.android

import ru.lemonapes.easyprog.android.commands.CommandItem

data class MainViewState(
    val codeItems: List<CodePeace> = emptyList(),
    val commandItems: List<CommandItem> = emptyList(),
    val sourceItems: List<CommandItem> = emptyList(),
    val showVictoryDialog: Boolean = false,
    val showTryAgainDialog: Boolean = false,
    val executingCommandIndex: Int? = null,
    val isHovered: Boolean = false,
    val itemIndexHovered: Int? = null,
)