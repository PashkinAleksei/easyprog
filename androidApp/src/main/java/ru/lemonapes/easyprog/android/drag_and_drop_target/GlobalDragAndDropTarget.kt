package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import ru.lemonapes.easyprog.Utils.Companion.log
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.commands.GotoCommand
import ru.lemonapes.easyprog.android.commands.PairCommand
import ru.lemonapes.easyprog.android.toCommandItem

fun GameViewModel.createGlobalDragAndDropTarget(): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {

            val isNewItem = event.label == "new_item"
            event.toCommandItem(draggedCommandItem.value)?.let { command ->
                when (command) {
                    is PairCommand -> if (!isNewItem) removeCommandPair(command)
                    else -> Unit
                }
            }
            setItemIndexHovered(null)
            return !isNewItem
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Global onEntered")
            setHovered(false)
            setItemIndexHovered(null)
        }
    }
}