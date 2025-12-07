package ru.lemonapes.easyprog.android.extensions

import android.content.ClipDescription
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes

fun Modifier.dragAndDropTextTarget(
    target: DragAndDropTarget,
) = dragAndDropTarget(
    shouldStartDragAndDrop = { event ->
        event
            .mimeTypes()
            .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
    },
    target = target
)