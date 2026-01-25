package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.Utils.Companion.log
import ru.lemonapes.easyprog.android.GameListener

fun createColumnDragAndDropTarget(
    listener: GameListener,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            return listener.onColumnItemDrop(event)
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Column onEntered")
            listener.onSetLastItemHovered()
            listener.onSetComandColumnHovered(true)
        }
    }
}