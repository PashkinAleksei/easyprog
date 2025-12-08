package ru.lemonapes.easyprog.android.ui.columns

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.ui.components.VariableBox
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun RowScope.CodeColumn(codeItems: List<CodePeace>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1.2f)
            .border(
                width = AppDimensions.borderWidth,
                color = AppColors.BorderDefault,
                shape = AppShapes.cornerMedium
            ),
        contentPadding = PaddingValues(AppDimensions.padding),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing8)
    ) {

        itemsIndexed(codeItems) { index, item ->
            when (item) {
                is CodePeace.IntVariable -> {
                    Column {
                        val boxBackground = if (item.value == null) AppColors.BackgroundTransparent else AppColors.BackgroundWhite
                        Box(
                            modifier = Modifier
                                .height(AppDimensions.variableValueHeight)
                                .width(AppDimensions.variableValueWidth)
                                .background(boxBackground)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            item.value?.let { value ->
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = value.toString(),
                                    color = AppColors.TextSecondary,
                                    fontWeight= FontWeight.Bold,
                                )
                            }
                        }
                        item.VariableBox(Modifier.size(AppDimensions.codeVariableBoxSize))
                    }
                }
            }
        }
    }
}