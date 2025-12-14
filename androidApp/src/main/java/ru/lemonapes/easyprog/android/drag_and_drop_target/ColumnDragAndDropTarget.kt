package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.Utils.Companion.log
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.toItem

fun GameViewModel.createColumnDragAndDropTarget(): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem(draggedCommandItem.value)?.let { item -> addCommand(item) }
            setHovered(false)
            setItemIndexHovered(null)
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Column onEntered")
            setItemIndexHovered(viewState.value.commandItems.lastIndex + 1)
            setHovered(true)
        }
    }
}