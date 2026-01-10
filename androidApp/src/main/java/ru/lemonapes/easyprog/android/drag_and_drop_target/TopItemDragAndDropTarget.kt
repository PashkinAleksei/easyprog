package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.toCommandItem

fun GameViewModel.createTopItemDragAndDropTarget(
    index: Int,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            val isNewItem = event.label == "new_item"
            event.toCommandItem(draggedCommandItem.value)?.let { command ->
                addCommandAtIndex(
                    index = index,
                    command = command,
                    isNewItem = isNewItem
                )
            }
            setItemIndexHovered(null)
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            setItemIndexHovered(index)
        }
    }
}