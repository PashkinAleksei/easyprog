package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.toItem
import kotlin.math.max

fun GameViewModel.createBotItemDragAndDropTarget(
    index: Int,
    commandItems: ImmutableList<CommandItem>,
    draggedCommandItem: StateFlow<CommandItem?>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem(draggedCommandItem.value)
                ?.let { item ->
                    addCommandAtIndex(index + 1, item)
                }
            setItemIndexHovered(null)
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            setItemIndexHovered(index + 1)
        }
    }
}