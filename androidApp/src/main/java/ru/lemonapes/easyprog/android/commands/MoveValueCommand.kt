package ru.lemonapes.easyprog.android.commands

import android.icu.util.Calendar
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.lemonapes.easyprog.android.CodePeace

@Immutable
data class MoveValueCommand(
    override val id: Long = Calendar.getInstance().timeInMillis,
    override val target: Pair<String, Int>? = null,
    override val source: Pair<String, Int>? = null,
) : TwoVariableCommand {
    override val text
        get() = "Переместить"
    override val stateId: String
        get() = toString()

    override fun mkCopy() = copy(id = Calendar.getInstance().timeInMillis)
    override fun invoke(codeItems: ImmutableList<CodePeace>): ImmutableList<CodePeace> {
        val newCodeItems = codeItems.toMutableList()

        val sourceIndex = source!!.second
        val targetIndex = target!!.second

        val sourceItem = newCodeItems.removeAt(sourceIndex) as CodePeace.IntVariable
        newCodeItems.add(sourceIndex, sourceItem.copy(value = null))

        val targetItem = newCodeItems.removeAt(targetIndex) as CodePeace.IntVariable
        newCodeItems.add(targetIndex, targetItem.copy(value = sourceItem.value))
        return newCodeItems.toImmutableList()
    }
}