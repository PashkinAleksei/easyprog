package ru.lemonapes.easyprog.android.ui.columns

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.ImmutableList
import ru.lemonapes.easyprog.android.MainViewModel
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.SourceColumn(viewModel: MainViewModel, sourceItems: ImmutableList<CommandItem>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.8f)
            .border(
                width = AppDimensions.borderWidth,
                color = AppColors.BorderDefault,
                shape = AppShapes.cornerMedium
            ),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing8),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing8),
        contentPadding = PaddingValues(AppDimensions.padding16),
        userScrollEnabled = false,
    ) {
        items(sourceItems) { item ->
            val text = stringResource(item.textRes)

            Box(
                modifier = Modifier
                    .dragAndDropSource { ->
                        detectTapGestures(
                            onPress = { offset ->
                                viewModel.setDraggedCommandItem(item.mkCopy())
                                startTransfer(
                                    transferData = DragAndDropTransferData(
                                        clipData = ClipData.newPlainText("adding_item", text)
                                    )
                                )
                            })
                    }
                    .background(
                        color = AppColors.CommandBackground,
                        shape = AppShapes.cornerMedium
                    )
                    .padding(AppDimensions.padding8),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(AppDimensions.sourceCodeIconSize),
                    painter = painterResource(item.iconRes),
                    contentDescription = stringResource(item.textRes),
                    colorFilter = ColorFilter.tint(AppColors.CommandAccent)
                )
            }
        }
    }
}