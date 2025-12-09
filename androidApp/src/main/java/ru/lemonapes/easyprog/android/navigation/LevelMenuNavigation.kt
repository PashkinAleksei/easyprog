package ru.lemonapes.easyprog.android.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ru.lemonapes.easyprog.android.LevelMenuViewModel
import ru.lemonapes.easyprog.android.ui.screens.LevelMenuView

fun NavGraphBuilder.levelMenuNavigation(navController: NavHostController) {
    composable<Screens.LevelMenu> {
        val viewModel: LevelMenuViewModel = viewModel()
        LevelMenuView(
            viewModel = viewModel,
            onLevelClick = { levelId ->
                navController.navigate(Screens.Game(levelId))
            }
        )
    }
}