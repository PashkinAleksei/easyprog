package ru.lemonapes.easyprog.android.ui.columns

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.unit.dp
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
fun RowScope.SourceColumn(viewModel: MainViewModel, sourceItems: List<CommandItem>) {
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
        contentPadding = PaddingValues(AppDimensions.padding16)
    ) {
        items(sourceItems) { item ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .dragAndDropSource { _ ->
                        viewModel.setDraggedCommandItem(item.mkCopy())
                        DragAndDropTransferData(
                            ClipData.newPlainText("adding_item", item.text)
                        )
                    }
                    .background(
                        color = AppColors.CommandBackground,
                        shape = AppShapes.cornerMedium
                    )
                    .padding(AppDimensions.padding8),
                contentAlignment = Alignment.Center
            ) {
                val iconRes = when (item) {
                    is CopyValueCommand -> R.drawable.copy
                    is MoveValueCommand -> R.drawable.cut
                }

                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(iconRes),
                    contentDescription = item.text,
                    colorFilter = ColorFilter.tint(AppColors.CommandAccent)
                )
            }
        }
    }
}