package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.Utils.Companion.log
import ru.lemonapes.easyprog.android.GameListener

fun createGlobalDragAndDropTarget(listener: GameListener): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            return listener.onGlobalItemDrop(event)
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Global onEntered")
            listener.onSetComandColumnHovered(false)
            listener.onSetItemIndexHovered(null)
        }
    }
}