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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.GotoCommand
import ru.lemonapes.easyprog.android.commands.IncValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.commands.PairCommand
import ru.lemonapes.easyprog.android.commands.SingleVariableCommand
import ru.lemonapes.easyprog.android.commands.TwoVariableCommand
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp2
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp4
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp6
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp8
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp12
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp16
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp20
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp28
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
            .padding(horizontal = dp16)
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
            .padding(dp12),
    ) {
        Spacer(modifier = Modifier.width(dp16))
        when (this@CommandRow) {
            is CopyValueCommand, is MoveValueCommand ->
                Box(
                    modifier = Modifier
                        .clip(AppShapes.CORNER_MEDIUM)
                        .background(AppColors.COLOR_ACCENT)

                ) {
                    Box(Modifier.padding(vertical = dp4, horizontal = dp8)) {
                        Image(
                            modifier = Modifier.size(AppDimensions.iconSize),
                            painter = painterResource(iconRes),
                            contentDescription = stringResource(textRes),
                            colorFilter = ColorFilter.tint(AppColors.MAIN_COLOR),
                        )
                    }
                }
        }
        Spacer(modifier = Modifier.width(dp20))

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

        Spacer(modifier = Modifier.width(dp2))
        Image(
            modifier = Modifier
                .padding(top = dp2)
                .size(dp28),
            painter = painterResource(R.drawable.arrow_right_alt),
            contentDescription = stringResource(R.string.to),
            colorFilter = ColorFilter.tint(AppColors.COLOR_ACCENT),
        )
        Spacer(modifier = Modifier.width(dp2))

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
            .padding(horizontal = dp16)
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
            .padding(dp12),
    ) {
        Spacer(modifier = Modifier.width(dp16))
        Box(
            modifier = Modifier
                .clip(AppShapes.CORNER_MEDIUM)
                .background(AppColors.COLOR_ACCENT)
        ) {
            Box(Modifier.padding(vertical = dp4, horizontal = dp8)) {
                Image(
                    modifier = Modifier.size(AppDimensions.iconSize),
                    painter = painterResource(iconRes),
                    contentDescription = stringResource(textRes),
                    colorFilter = ColorFilter.tint(AppColors.MAIN_COLOR),
                )
            }
        }
        Spacer(modifier = Modifier.width(dp20))

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
            .padding(horizontal = dp16)
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
            .padding(dp12),
    ) {
        Spacer(modifier = Modifier.width(dp16))
        Box(
            modifier = Modifier
                .size(AppDimensions.commandVariableBoxSize)
                .clip(AppShapes.CORNER_SMALL)
                .background(AppColors.LabelColors.first())
        )
        if (type == PairCommand.PairType.FIRST) {
            Image(
                modifier = Modifier
                    .size(dp28),
                painter = painterResource(R.drawable.ic_arrow_down_left),
                contentDescription = stringResource(R.string.to_portal),
                colorFilter = ColorFilter.tint(AppColors.COLOR_ACCENT),
            )
        } else {
            Image(
                modifier = Modifier
                    .padding(top = dp6)
                    .size(dp28),
                painter = painterResource(R.drawable.ic_arrow_right_down),
                contentDescription = stringResource(R.string.from_portal),
                colorFilter = ColorFilter.tint(AppColors.COLOR_ACCENT),
            )
        }
    }
}