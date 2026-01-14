package ru.lemonapes.easyprog.android.ui.components

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp12
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp16
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.commandRowModifier(
    index: Int,
    isThisCommandExecuting: Boolean,
    isCommandExecution: Boolean,
    text: String,
    viewModel: GameViewModel,
): Modifier {
    val backgroundColor = if (isThisCommandExecuting) AppColors.CommandBackgroundExecuting else AppColors.MAIN_COLOR

    val dragModifier = if (!isCommandExecution) {
        this.dragAndDropSource { _ ->
            viewModel.setDraggedCommandItem(viewModel.removeCommand(index))
            DragAndDropTransferData(
                ClipData.newPlainText("dragged_item", text)
            )
        }
    } else {
        this
    }

    return dragModifier
        .fillMaxWidth()
        .padding(horizontal = dp16)
        .background(
            color = backgroundColor,
            shape = AppShapes.CORNER_MEDIUM
        )
        .padding(dp12)
}