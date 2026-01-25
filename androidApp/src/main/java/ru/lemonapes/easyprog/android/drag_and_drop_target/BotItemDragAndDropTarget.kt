package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.android.GameListener

fun createBotItemDragAndDropTarget(
    index: Int,
    listener: GameListener,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            return listener.onBotItemDrop(index, event)
        }

        override fun onEntered(event: DragAndDropEvent) {
            listener.onSetItemIndexHovered(index + 1)
        }
    }
}