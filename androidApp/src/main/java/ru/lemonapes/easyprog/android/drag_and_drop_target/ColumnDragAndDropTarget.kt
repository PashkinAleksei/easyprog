package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.Utils.Companion.log
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.toCommandItem

fun GameViewModel.createColumnDragAndDropTarget(): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            val isNewItem = event.label == "new_item"
            val commandResult = event.toCommandItem(draggedCommandItem.value)?.let { command ->
                addCommand(command, isNewItem)
            } ?: false
            setHovered(false)
            setItemIndexHovered(null)
            setDraggedCommandItem(null)
            return commandResult
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Column onEntered")
            setItemIndexHovered(viewState.value.commandItems.lastIndex + 1)
            setHovered(true)
        }
    }
}