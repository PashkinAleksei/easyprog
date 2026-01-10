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
) : PairCommand {
    override val textRes: Int
        @StringRes
        get() = when (type) {
            PairCommand.PairType.FIRST -> R.string.command_goto_start
            PairCommand.PairType.SECOND -> R.string.command_goto_target
        }

    override val iconRes: Int
        @DrawableRes
        get() = when (type) {
            PairCommand.PairType.FIRST -> R.drawable.ic_step_over
            PairCommand.PairType.SECOND -> R.drawable.ic_step
        }

    override val stateId: String
        get() = toString()

    override fun mkCopy() = copy(id = Calendar.getInstance().timeInMillis)

    override fun invoke(codeItems: ImmutableList<CodePeace>): ImmutableList<CodePeace> {
        // Пока не выполняет действий
        return codeItems
    }
}