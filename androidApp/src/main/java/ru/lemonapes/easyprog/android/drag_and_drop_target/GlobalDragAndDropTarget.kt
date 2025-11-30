package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.Utils.Companion.log
import ru.lemonapes.easyprog.android.MainViewModel

fun createGlobalDragAndDropTarget(
    viewModel: MainViewModel,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            viewModel.setItemIndexHovered(null)
            return false
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Global onEntered")
            viewModel.setHovered(false)
            viewModel.setItemIndexHovered(null)
        }
    }
}