package ru.lemonapes.easyprog.android.drag_and_drop_target

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.toAndroidDragEvent

val DragAndDropEvent.label: String?
    get() = toAndroidDragEvent()
        .clipData
        ?.description
        ?.label
        ?.toString()