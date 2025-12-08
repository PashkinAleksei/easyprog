package ru.lemonapes.easyprog.android.ui.columns

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import ru.lemonapes.easyprog.android.MainViewModel
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RowScope.SourceColumn(viewModel: MainViewModel, sourceItems: List<CommandItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.8f)
            .border(
                width = AppDimensions.borderWidth,
                color = AppColors.BorderDefault,
                shape = AppShapes.cornerMedium
            ),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing8),
        contentPadding = PaddingValues(AppDimensions.padding)
    ) {
        items(sourceItems) { item ->
            Text(
                text = item.text,
                modifier = Modifier
                    .dragAndDropSource { _ ->
                        viewModel.setDraggedCommandItem(item.mkCopy())
                        DragAndDropTransferData(
                            ClipData.newPlainText("adding_item", item.text)
                        )
                    }
                    .background(
                        color = AppColors.SourceItemBackground,
                        shape = AppShapes.cornerMedium
                    )
                    .padding(AppDimensions.padding),
                color = AppColors.TextPrimary
            )
        }
    }
}