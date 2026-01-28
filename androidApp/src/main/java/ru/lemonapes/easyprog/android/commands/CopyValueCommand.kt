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
data class CopyValueCommand(
    override val id: Long = Calendar.getInstance().timeInMillis,
    override val target: Int? = null,
    override val source: Int? = null,
) : TwoVariableCommand {
    @StringRes
    override val textRes = R.string.command_copy_text
    @DrawableRes
    override val iconRes: Int = R.drawable.ic_copy
    override val stateId = toString()

    override fun mkCopy() = copy(id = Calendar.getInstance().timeInMillis)

    override fun execute(
        codeItems: ImmutableList<CodePeace>,
        commandItems: ImmutableList<CommandItem>,
        currentCommandIndex: Int,
    ): CommandResult {
        val newCodeItems = codeItems.toMutableList()

        val sourceIndex = source!!
        val targetIndex = target!!

        val sourceItem = codeItems[sourceIndex] as CodePeace.IntVariable
        val targetItem = newCodeItems.removeAt(targetIndex) as CodePeace.IntVariable

        newCodeItems.add(targetIndex, targetItem.copy(value = sourceItem.value))

        return CommandResult(
            newCodeItems = newCodeItems.toImmutableList(),
            nextCommandIndex = currentCommandIndex + 1,
        )
    }

    override fun validate(): Boolean {
        return source != null && target != null
    }
}