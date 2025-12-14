package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.toItem

fun GameViewModel.createTopItemDragAndDropTarget(
    index: Int,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem(draggedCommandItem.value)?.let { item -> addCommandAtIndex(index, item) }
            setItemIndexHovered(null)
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            setItemIndexHovered(index)
        }
    }
}