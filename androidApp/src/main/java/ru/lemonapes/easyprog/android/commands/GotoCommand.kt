package ru.lemonapes.easyprog.android.commands

import android.icu.util.Calendar
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.R

@Immutable
data class GotoCommand(
    override val id: Long = Calendar.getInstance().timeInMillis,
    val type: PairCommand.PairType,
    override val pairId: Long,
    val colorIndex: Int,
) : PairCommand {
    @StringRes
    override val textRes: Int = when (type) {
        PairCommand.PairType.FIRST -> R.string.command_goto_start
        PairCommand.PairType.SECOND -> R.string.command_goto_target
    }

    @DrawableRes
    override val iconRes: Int = R.drawable.ic_step_over
    override val stateId = toString()

    override fun mkCopy() = copy(id = Calendar.getInstance().timeInMillis)

    override fun execute(
        codeItems: ImmutableList<CodePeace>,
        commandItems: ImmutableList<CommandItem>,
        currentCommandIndex: Int,
    ): CommandResult {
        val pairIndex = commandItems.indexOfFirst { it is GotoCommand && it.id != id && it.pairId == pairId }
        val nextCommandIndex = if (type == PairCommand.PairType.FIRST) pairIndex else currentCommandIndex + 1
        return CommandResult(
            newCodeItems = codeItems,
            nextCommandIndex = nextCommandIndex,
        )
    }
}