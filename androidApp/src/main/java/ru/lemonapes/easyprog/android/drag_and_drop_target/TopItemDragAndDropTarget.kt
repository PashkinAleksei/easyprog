package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.android.GameListener
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.GameViewState
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.toCommandItem

fun createTopItemDragAndDropTarget(
    index: Int,
    listener: GameListener,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            return listener.onTopItemDrop(index, event)
        }

        override fun onEntered(event: DragAndDropEvent) {
            listener.onSetItemIndexHovered(index)
        }
    }
}