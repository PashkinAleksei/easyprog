package ru.lemonapes.easyprog.android.ui.columns

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.lemonapes.easyprog.android.GameListener
import ru.lemonapes.easyprog.android.GameViewState
import ru.lemonapes.easyprog.android.MyApplicationTheme
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.GotoCommand
import ru.lemonapes.easyprog.android.commands.SingleVariableCommand
import ru.lemonapes.easyprog.android.commands.TwoVariableCommand
import ru.lemonapes.easyprog.android.drag_and_drop_target.createBotItemDragAndDropTarget
import ru.lemonapes.easyprog.android.drag_and_drop_target.createColumnDragAndDropTarget
import ru.lemonapes.easyprog.android.drag_and_drop_target.createTopItemDragAndDropTarget
import ru.lemonapes.easyprog.android.extensions.dragAndDropTextTarget
import ru.lemonapes.easyprog.android.preview.PreviewGameListener
import ru.lemonapes.easyprog.android.preview.PreviewGameState
import ru.lemonapes.easyprog.android.ui.components.CommandRow
import ru.lemonapes.easyprog.android.ui.components.commandRowModifier
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun RowScope.CommandsColumn(
    viewState: GameViewState,
    listener: GameListener,
) {
    val isColumnVisualHovered = viewState.isCommandColumnHovered && viewState.commandItems.isEmpty()
    val isCommandExecution = viewState.isCommandExecution

    val columnDragAndDropTarget = remember(listener) {
        createColumnDragAndDropTarget(listener)
    }
    val borderWidth = if (isColumnVisualHovered) {
        AppDimensions.columnBorderWidthSelected
    } else {
        AppDimensions.columnBorderWidth
    }
    val borderColor = if (isColumnVisualHovered) AppColors.ColumnsBorderColorSelected else AppColors.ColumnsBorderColor
    val boxIconColor = if (isColumnVisualHovered) {
        AppColors.ColumnsBorderColorSelected
    } else {
        AppColors.ColumnsBorderColor
    }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = AppShapes.CORNER_MEDIUM
            )
            .dragAndDropTextTarget(columnDragAndDropTarget)
    ) {
        if (viewState.commandItems.isEmpty()) {
            Image(
                modifier = Modifier
                    .fillMaxSize(0.25f)
                    .align(Alignment.Center),
                painter = painterResource(R.drawable.ic_simple_empty_box),
                contentDescription = stringResource(R.string.empty_command_icon),
                colorFilter = ColorFilter.tint(boxIconColor),
            )
        } else {
            val listState = rememberLazyListState()

            LaunchedEffect(viewState.scrollToIndex) {
                scrollToAddedItemIndex(viewState, listState, listener)
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = AppDimensions.dp8),
            ) {
                itemsIndexed(viewState.commandItems, key = { _, item -> item.stateId }) { index, item ->
                    val topPadding = if (index == 0) AppDimensions.dp8 else AppDimensions.columnBorderWidth

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
                                Spacer(modifier = Modifier.height(AppDimensions.dp8))
                            }

                            val commandText = stringResource(item.textRes)
                            val commandRowModifier = Modifier.commandRowModifier(
                                index = index,
                                isThisCommandExecuting = viewState.executingCommandIndex == index,
                                isCommandExecution = isCommandExecution,
                                text = commandText,
                                listener = listener
                            )

                            when (item) {
                                is TwoVariableCommand -> {
                                    item.CommandRow(
                                        modifier = commandRowModifier,
                                        index = index,
                                        codeItems = viewState.codeItems,
                                        listener = listener,
                                    )
                                }

                                is SingleVariableCommand -> {
                                    item.CommandRow(
                                        modifier = commandRowModifier,
                                        index = index,
                                        codeItems = viewState.codeItems,
                                        listener = listener,
                                    )
                                }

                                is GotoCommand -> {
                                    item.CommandRow(commandRowModifier)
                                }
                            }
                            if (index == viewState.commandItems.lastIndex) {
                                if ((viewState.itemIndexHovered ?: -1) > viewState.commandItems.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = AppDimensions.dividerVerticalPadding),
                                        thickness = AppDimensions.dividerThickness,
                                        color = AppColors.DividerHover
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(AppDimensions.dp8))
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .matchParentSize()
                        ) {
                            val topItemDragAndDropTarget = remember(index, item, listener) {
                                createTopItemDragAndDropTarget(index, listener)
                            }
                            val botItemDragAndDropTarget = remember(index, item, listener) {
                                createBotItemDragAndDropTarget(index, listener)
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
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
private fun CommandsColumnPreview() {
    MyApplicationTheme {
        Surface {
            Row {
                CommandsColumn(
                    viewState = PreviewGameState.getLevel1(),
                    listener = PreviewGameListener(),
                )
            }
        }
    }
}

private suspend fun scrollToAddedItemIndex(
    viewState: GameViewState,
    listState: LazyListState,
    listener: GameListener,
) {
    //TODO: Срабатывает не всегда. Нужно логировать и разбираться
    viewState.scrollToIndex?.let { index ->
        if (index in viewState.commandItems.indices) {
            val layoutInfo = listState.layoutInfo
            val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == index }

            if (visibleItem != null) {
                // Элемент виден, проверяем насколько
                val itemEnd = visibleItem.offset + visibleItem.size
                val viewportEnd = layoutInfo.viewportEndOffset

                // Если элемент виден не полностью снизу
                if (itemEnd > viewportEnd) {
                    // Прокручиваем так, чтобы элемент был внизу viewport с учетом padding
                    val scrollOffset =
                        -(layoutInfo.viewportSize.height - visibleItem.size - layoutInfo.afterContentPadding)
                    listState.animateScrollToItem(index, scrollOffset)
                }
                // Если элемент полностью виден, не прокручиваем
            } else {
                // Элемент не виден
                // Определяем, добавлен ли элемент снизу (после последнего видимого)
                val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

                if (index > lastVisibleIndex) {
                    // Элемент ниже видимой области - показываем его внизу с учетом padding
                    val estimatedItemSize = layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
                    val scrollOffset =
                        -(layoutInfo.viewportSize.height - estimatedItemSize - layoutInfo.afterContentPadding)
                    listState.animateScrollToItem(index, scrollOffset)
                } else {
                    // Элемент выше видимой области - показываем вверху
                    listState.animateScrollToItem(index)
                }
            }
            listener.onClearScrollToIndex()
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
private fun CommandsColumnEmptyPreview() {
    MyApplicationTheme {
        Surface {
            Row {
                CommandsColumn(
                    viewState = PreviewGameState.getLevel1(),
                    listener = PreviewGameListener(),
                )
            }
        }
    }
}