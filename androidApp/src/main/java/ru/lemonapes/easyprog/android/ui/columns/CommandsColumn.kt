package ru.lemonapes.easyprog.android.ui.columns

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.lemonapes.easyprog.android.MainViewModel
import ru.lemonapes.easyprog.android.MainViewState
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.drag_and_drop_target.createBotItemDragAndDropTarget
import ru.lemonapes.easyprog.android.drag_and_drop_target.createColumnDragAndDropTarget
import ru.lemonapes.easyprog.android.drag_and_drop_target.createTopItemDragAndDropTarget
import ru.lemonapes.easyprog.android.extensions.dragAndDropTextTarget
import ru.lemonapes.easyprog.android.ui.components.CommandRow
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun RowScope.CommandsColumn(
    viewState: MainViewState,
    viewModel: MainViewModel,
) {
    val isColumnVisualHovered = viewState.isHovered && viewState.commandItems.isEmpty()

    val columnDragAndDropTarget = remember(viewModel) { viewModel.createColumnDragAndDropTarget() }
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .border(
                width = AppDimensions.borderWidth,
                color = if (isColumnVisualHovered) AppColors.BorderHover
                else AppColors.BorderDefault,
                shape = AppShapes.cornerMedium
            )
            .background(
                color = if (isColumnVisualHovered) AppColors.CommandHoverBackground
                else AppColors.BackgroundTransparent,
                shape = AppShapes.cornerMedium
            )
            .dragAndDropTextTarget(columnDragAndDropTarget),
        contentPadding = PaddingValues(bottom = AppDimensions.spacing),
    ) {
        if (viewState.commandItems.isEmpty()) {
            item {
                Text(
                    "Колонка для комманд",
                    color = AppColors.BorderDefault,
                    modifier = Modifier.padding(AppDimensions.paddingLarge)
                )
            }
        } else {
            itemsIndexed(viewState.commandItems, key = { _, item -> item.stateId }) { index, item ->
                val topPadding = if (index == 0) AppDimensions.spacing else AppDimensions.borderWidth

                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(top = topPadding)
                            .fillMaxWidth()
                    ) {
                        if (viewState.itemIndexHovered == index) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = AppDimensions.dividerVerticalPadding),
                                thickness = AppDimensions.dividerThickness,
                                color = AppColors.DividerHover
                            )
                        } else {
                            Spacer(modifier = Modifier.height(AppDimensions.spacing))
                        }
                        when (item) {
                            is CopyValueCommand -> item.CommandRow(
                                index = index,
                                codeItems = viewState.codeItems,
                                isExecuting = viewState.executingCommandIndex == index,
                                viewModel = viewModel
                            )

                            is MoveValueCommand -> item.CommandRow(
                                index = index,
                                codeItems = viewState.codeItems,
                                isExecuting = viewState.executingCommandIndex == index,
                                viewModel = viewModel
                            )
                        }
                        if (index == viewState.commandItems.lastIndex) {
                            if ((viewState.itemIndexHovered ?: -1) > viewState.commandItems.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = AppDimensions.dividerVerticalPadding),
                                    thickness = AppDimensions.dividerThickness,
                                    color = AppColors.DividerHover
                                )
                            } else {
                                Spacer(modifier = Modifier.height(AppDimensions.spacing))
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .matchParentSize()
                    ) {
                        val topItemDragAndDropTarget = remember(index, item) {
                            viewModel.createTopItemDragAndDropTarget(
                                index = index,
                            )
                        }
                        val botItemDragAndDropTarget = remember(index, item) {
                            createBotItemDragAndDropTarget(
                                index = index,
                                viewModel = viewModel,
                                commandItems = viewState.commandItems,
                                draggedCommandItem = viewModel.draggedCommandItem,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .dragAndDropTextTarget(topItemDragAndDropTarget)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .dragAndDropTextTarget(botItemDragAndDropTarget)
                        )
                    }
                }
            }
        }
    }
}