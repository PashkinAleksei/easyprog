package ru.lemonapes.easyprog.android

import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import ru.lemonapes.easyprog.android.commands.CommandItem

internal fun DragAndDropEvent.toCommandItem(draggedCommandItem: CommandItem?) = draggedCommandItem