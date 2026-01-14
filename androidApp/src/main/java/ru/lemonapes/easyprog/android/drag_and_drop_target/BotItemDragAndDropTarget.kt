package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.toCommandItem

fun GameViewModel.createBotItemDragAndDropTarget(
    index: Int,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            // Игнорировать drop во время выполнения команд
            val addingResult = if (!viewState.value.isCommandExecution) {
                val isNewItem = event.label == "new_item"
                event.toCommandItem(draggedCommandItem.value)
                    ?.let { command ->
                        addCommandAtIndex(
                            index = index + 1,
                            command = command,
                            isNewItem = isNewItem
                        )
                    } ?: false
            } else false
            setItemIndexHovered(null)
            setDraggedCommandItem(null)
            return addingResult
        }

        override fun onEntered(event: DragAndDropEvent) {
            setItemIndexHovered(index + 1)
        }
    }
}