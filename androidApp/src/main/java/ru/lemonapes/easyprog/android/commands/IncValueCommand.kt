package ru.lemonapes.easyprog.android.commands

import android.icu.util.Calendar
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.R

@Immutable
data class IncValueCommand(
    override val id: Long = Calendar.getInstance().timeInMillis,
    override val target: Int? = null,
) : SingleVariableCommand {
    @StringRes
    override val textRes = R.string.command_inc_text
    @DrawableRes
    override val iconRes: Int = R.drawable.ic_increment
    override val stateId = toString()

    override fun mkCopy() = copy(id = Calendar.getInstance().timeInMillis)

    override fun execute(
        codeItems: ImmutableList<CodePeace>,
        commandItems: ImmutableList<CommandItem>,
        currentCommandIndex: Int,
    ): CommandResult {
        val newCodeItems = codeItems.toMutableList()

        val targetIndex = target!!
        val targetItem = newCodeItems.removeAt(targetIndex) as CodePeace.IntVariable

        val newValue = (targetItem.value ?: 0) + 1
        newCodeItems.add(targetIndex, targetItem.copy(value = newValue))

        return CommandResult(
            newCodeItems = newCodeItems.toImmutableList(),
            nextCommandIndex = currentCommandIndex + 1,
        )
    }
}