package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import ru.lemonapes.easyprog.Utils.Companion.log
import ru.lemonapes.easyprog.android.MainViewModel

fun MainViewModel.createGlobalDragAndDropTarget(): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            val label = event.toAndroidDragEvent()
                .clipData
                ?.description
                ?.label
                ?.toString()

            setItemIndexHovered(null)
            return label != "adding_item"
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Global onEntered")
            setHovered(false)
            setItemIndexHovered(null)
        }
    }
}