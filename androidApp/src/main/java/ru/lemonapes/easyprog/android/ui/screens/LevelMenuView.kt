package ru.lemonapes.easyprog.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.lemonapes.easyprog.android.LevelMenuViewModel
import ru.lemonapes.easyprog.android.MyApplicationTheme
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun LevelMenuView(
    modifier: Modifier = Modifier,
    viewModel: LevelMenuViewModel,
    onLevelClick: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = modifier
                .padding(AppDimensions.dp16)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.dp32),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.dp32),
            contentPadding = PaddingValues(AppDimensions.dp8)
        ) {
            items(state.levels.take(24).size) { index ->
                val levelNumber = index + 1
                val isUnlocked = viewModel.isLevelUnlocked(levelNumber)

                LevelItem(
                    levelNumber = levelNumber,
                    isUnlocked = isUnlocked,
                    onClick = {
                        if (isUnlocked) {
                            onLevelClick(levelNumber)
                        }
                    }
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun LevelItem(
    levelNumber: Int,
    isUnlocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = if (isUnlocked) AppColors.CommandAccent else AppColors.CommandBackground,
                shape = AppShapes.cornerMedium
            )
            .border(
                width = AppDimensions.columnBorderWidth,
                color = if (isUnlocked) AppColors.CommandBackground else AppColors.CommandAccent,
                shape = AppShapes.cornerMedium
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(AppDimensions.dp8),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = levelNumber.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUnlocked) AppColors.CommandBackground else AppColors.CommandAccent
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
private fun LevelMenuViewPreview() {
    MyApplicationTheme {
        Surface {
            LevelMenuView(
                viewModel = viewModel(),
                onLevelClick = {}
            )
        }
    }
}