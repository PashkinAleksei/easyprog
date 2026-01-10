package ru.lemonapes.easyprog.android.ui.components

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.GotoCommand
import ru.lemonapes.easyprog.android.commands.IncValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.commands.SingleVariableCommand
import ru.lemonapes.easyprog.android.commands.TwoVariableCommand
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TwoVariableCommand.CommandRow(
    index: Int,
    codeItems: ImmutableList<CodePeace>,
    isExecuting: Boolean,
    viewModel: GameViewModel,
) {
    val variables = remember(codeItems) {
        codeItems.filterIsInstance<CodePeace.IntVariable>().map { it }.toImmutableList()
    }
    val backgroundColor = if (isExecuting) AppColors.CommandBackgroundExecuting else AppColors.MAIN_COLOR
    val text = stringResource(textRes)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.dp16)
            .background(
                color = backgroundColor,
                shape = AppShapes.CORNER_MEDIUM
            )
            .dragAndDropSource { _ ->
                viewModel.setDraggedCommandItem(viewModel.removeCommand(index))
                DragAndDropTransferData(
                    ClipData.newPlainText("dragged_item", text)
                )
            }
            .padding(AppDimensions.dp12),
    ) {
        Spacer(modifier = Modifier.weight(0.7f))
        when (this@CommandRow) {
            is CopyValueCommand, is MoveValueCommand ->
                Box(
                    modifier = Modifier
                        .clip(AppShapes.CORNER_MEDIUM)
                        .background(AppColors.COLOR_ACCENT)

                ) {
                    Box(Modifier.padding(vertical = AppDimensions.dp4, horizontal = AppDimensions.dp8)) {
                        Image(
                            modifier = Modifier.size(AppDimensions.iconSize),
                            painter = painterResource(iconRes),
                            contentDescription = stringResource(textRes),
                            colorFilter = ColorFilter.tint(AppColors.MAIN_COLOR),
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        IntVariableDropdownBox(
            selectedIndex = source,
            codeItems = codeItems,
            variables = variables,
            onVariableSelected = { variable ->
                when (this@CommandRow) {
                    is MoveValueCommand -> viewModel.updateCommand(
                        index,
                        copy(source = codeItems.indexOf(variable))
                    )

                    is CopyValueCommand -> viewModel.updateCommand(
                        index,
                        copy(source = codeItems.indexOf(variable))
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(AppDimensions.dp2))

        Image(
            modifier = Modifier
                .padding(top = AppDimensions.dp2)
                .size(28.dp),
            painter = painterResource(R.drawable.arrow_right_alt),
            contentDescription = stringResource(R.string.arrow_to_description),
            colorFilter = ColorFilter.tint(AppColors.COLOR_ACCENT),
        )

        Spacer(modifier = Modifier.width(AppDimensions.dp2))

        IntVariableDropdownBox(
            selectedIndex = target,
            codeItems = codeItems,
            variables = variables,
            onVariableSelected = { variable ->
                when (this@CommandRow) {
                    is CopyValueCommand -> viewModel.updateCommand(
                        index,
                        copy(target = codeItems.indexOf(variable))
                    )

                    is MoveValueCommand -> viewModel.updateCommand(
                        index,
                        copy(target = codeItems.indexOf(variable))
                    )
                }
            }
        )

        Spacer(modifier = Modifier.weight(0.7f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SingleVariableCommand.CommandRow(
    index: Int,
    codeItems: ImmutableList<CodePeace>,
    isExecuting: Boolean,
    viewModel: GameViewModel,
) {
    val variables = remember(codeItems) {
        codeItems.filterIsInstance<CodePeace.IntVariable>().map { it }.toImmutableList()
    }
    val backgroundColor = if (isExecuting) AppColors.CommandBackgroundExecuting else AppColors.MAIN_COLOR
    val text = stringResource(textRes)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.dp16)
            .background(
                color = backgroundColor,
                shape = AppShapes.CORNER_MEDIUM
            )
            .dragAndDropSource { _ ->
                viewModel.setDraggedCommandItem(viewModel.removeCommand(index))
                DragAndDropTransferData(
                    ClipData.newPlainText("dragged_item", text)
                )
            }
            .padding(AppDimensions.dp12),
    ) {
        Spacer(modifier = Modifier.weight(0.7f))

        Box(
            modifier = Modifier
                .clip(AppShapes.CORNER_MEDIUM)
                .background(AppColors.COLOR_ACCENT)
        ) {
            Box(Modifier.padding(vertical = AppDimensions.dp4, horizontal = AppDimensions.dp8)) {
                Image(
                    modifier = Modifier.size(AppDimensions.iconSize),
                    painter = painterResource(iconRes),
                    contentDescription = stringResource(textRes),
                    colorFilter = ColorFilter.tint(AppColors.MAIN_COLOR),
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        IntVariableDropdownBox(
            selectedIndex = target,
            codeItems = codeItems,
            variables = variables,
            onVariableSelected = { variable ->
                when (this@CommandRow) {
                    is IncValueCommand -> viewModel.updateCommand(
                        index,
                        copy(target = codeItems.indexOf(variable))
                    )
                }
            }
        )

        Spacer(modifier = Modifier.weight(0.7f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GotoCommand.CommandRow(
    index: Int,
    isExecuting: Boolean,
    viewModel: GameViewModel,
) {
    val backgroundColor = if (isExecuting) AppColors.CommandBackgroundExecuting else AppColors.MAIN_COLOR
    val text = stringResource(textRes)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.dp16)
            .background(
                color = backgroundColor,
                shape = AppShapes.CORNER_MEDIUM
            )
            .dragAndDropSource { _ ->
                viewModel.setDraggedCommandItem(viewModel.removeCommand(index))
                DragAndDropTransferData(
                    ClipData.newPlainText("dragged_item", text)
                )
            }
            .padding(AppDimensions.dp12),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(AppShapes.CORNER_MEDIUM)
                .background(AppColors.COLOR_ACCENT)
        ) {
            Box(Modifier.padding(vertical = AppDimensions.dp4, horizontal = AppDimensions.dp8)) {
                Image(
                    modifier = Modifier.size(AppDimensions.iconSize),
                    painter = painterResource(iconRes),
                    contentDescription = stringResource(textRes),
                    colorFilter = ColorFilter.tint(AppColors.MAIN_COLOR),
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}