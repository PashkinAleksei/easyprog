package ru.lemonapes.easyprog.android.commands

import android.icu.util.Calendar
import ru.lemonapes.easyprog.android.CodePeace

data class CopyVariableToVariable(
    override val id: Long = Calendar.getInstance().timeInMillis,
    val target: Pair<String, Int>? = null,
    val source: Pair<String, Int>? = null,
) : CommandItem {
    override val text
        get() = "Копировать"

    override fun invoke(codeItems: List<CodePeace>): List<CodePeace> {
        val newCodeItems = codeItems.toMutableList()

        val sourceIndex = source!!.second
        val targetIndex = target!!.second

        val sourceItem = codeItems[sourceIndex] as CodePeace.IntVariable
        val targetItem = newCodeItems.removeAt(targetIndex) as CodePeace.IntVariable

        newCodeItems.add(targetIndex, targetItem.copy(value = sourceItem.value))
        return newCodeItems
    }
}