package ru.lemonapes.easyprog.android

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.commands.CopyVariableToVariable

internal fun DragAndDropEvent.toItem(draggedCommandItem: CommandItem?): CommandItem? {
    val label = toAndroidDragEvent()
        .clipData
        ?.description
        ?.label
        ?.toString()

    return when (label) {
        "adding_item" -> CopyVariableToVariable()
        "dragged_item" -> draggedCommandItem
        else -> null
    }
}
