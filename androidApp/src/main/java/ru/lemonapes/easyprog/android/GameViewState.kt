package ru.lemonapes.easyprog.android

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.lemonapes.easyprog.android.commands.CommandItem

data class GameViewState(
    val levelId: Int = 1,
    val codeItems: ImmutableList<CodePeace> = persistentListOf(),
    val commandItems: ImmutableList<CommandItem> = persistentListOf(),
    val sourceItems: ImmutableList<CommandItem> = persistentListOf(),
    val showVictoryDialog: Boolean = false,
    val showTryAgainDialog: Boolean = false,
    val showLevelInfoDialog: Boolean = false,
    val levelTitle: String = "",
    val levelDescription: String = "",
    val executingCommandIndex: Int? = null,
    val isHovered: Boolean = false,
    val itemIndexHovered: Int? = null,
    val scrollToIndex: Int? = null,
)