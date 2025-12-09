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
    override val textRes
        @StringRes
        get() = R.string.command_copy_text
    override val iconRes: Int
        @DrawableRes
        get() = R.drawable.copy
    override val stateId: String
        get() = toString()

    override fun mkCopy() = copy(id = Calendar.getInstance().timeInMillis)

    override fun invoke(codeItems: ImmutableList<CodePeace>): ImmutableList<CodePeace> {
        val newCodeItems = codeItems.toMutableList()

        val sourceIndex = source!!
        val targetIndex = target!!

        val sourceItem = codeItems[sourceIndex] as CodePeace.IntVariable
        val targetItem = newCodeItems.removeAt(targetIndex) as CodePeace.IntVariable

        newCodeItems.add(targetIndex, targetItem.copy(value = sourceItem.value))
        return newCodeItems.toImmutableList()
    }
}