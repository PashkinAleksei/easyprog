package ru.lemonapes.easyprog.android

import androidx.annotation.DrawableRes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.lemonapes.easyprog.android.commands.CommandItem

enum class ExecutionSpeed(val delayMs: Long, @DrawableRes val iconRes: Int) {
    SPEED_05X(800, R.drawable.ic_speed_05x),
    SPEED_1X(400, R.drawable.ic_speed_1x),
    SPEED_2X(200, R.drawable.ic_speed_2x);

    fun next(): ExecutionSpeed = when (this) {
        SPEED_05X -> SPEED_1X
        SPEED_1X -> SPEED_2X
        SPEED_2X -> SPEED_05X
    }
}

data class GameViewState(
    val levelId: Int = 1,
    val codeItems: ImmutableList<CodePeace> = persistentListOf(),
    val commandItems: ImmutableList<CommandItem> = persistentListOf(),
    val sourceItems: ImmutableList<CommandItem> = persistentListOf(),
    val showVictoryDialog: Boolean = false,
    val showTryAgainDialog: Boolean = false,
    val showLevelInfoDialog: Boolean = false,
    val showClearCommandsDialog: Boolean = false,
    val levelTitle: String = "",
    val levelDescription: String = "",
    val executingCommandIndex: Int? = null,
    val isHovered: Boolean = false,
    val itemIndexHovered: Int? = null,
    val scrollToIndex: Int? = null,
    val executionSpeed: ExecutionSpeed = ExecutionSpeed.SPEED_1X,
) {
    val isCommandExecution: Boolean
        get() = executingCommandIndex != null
}