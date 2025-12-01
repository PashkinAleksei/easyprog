package ru.lemonapes.easyprog.android

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import ru.lemonapes.easyprog.android.commands.CommandItem

internal fun DragAndDropEvent.toItem(draggedCommandItem: CommandItem?): CommandItem? {
    val label = toAndroidDragEvent()
        .clipData
        ?.description
        ?.label
        ?.toString()

    return draggedCommandItem
}
