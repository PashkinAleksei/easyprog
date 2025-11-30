package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import kotlinx.coroutines.flow.StateFlow
import ru.lemonapes.easyprog.Utils.Companion.log
import ru.lemonapes.easyprog.android.MainViewModel
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.toItem

fun createColumnDragAndDropTarget(
    viewModel: MainViewModel,
    commandItems: List<CommandItem>,
    draggedCommandItem: StateFlow<CommandItem?>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem(draggedCommandItem.value)?.let { item -> viewModel.addCommand(item) }
            viewModel.setHovered(false)
            viewModel.setItemIndexHovered(null)
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Column onEntered")
            viewModel.setItemIndexHovered(commandItems.lastIndex + 1)
            viewModel.setHovered(true)
        }
    }
}