package ru.lemonapes.easyprog.android.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.drag_and_drop_target.createGlobalDragAndDropTarget
import ru.lemonapes.easyprog.android.extensions.dragAndDropTextTarget
import ru.lemonapes.easyprog.android.ui.screens.GameView

fun NavGraphBuilder.gameNavigation(navController: NavHostController) {
    composable<Screens.Game> { backStackEntry ->
        val game: Screens.Game = backStackEntry.toRoute()
        val viewModel: GameViewModel = viewModel()

        LaunchedEffect(game.levelId) {
            viewModel.loadLevel(game.levelId)
        }

        val globalDragAndDropTarget = remember {
            viewModel.createGlobalDragAndDropTarget()
        }

        GameView(
            modifier = Modifier
                .fillMaxSize()
                .dragAndDropTextTarget(globalDragAndDropTarget),
            viewModel = viewModel,
            onBackToMenu = {
                navController.navigate(Screens.LevelMenu) {
                    popUpTo(Screens.LevelMenu) { inclusive = true }
                }
            },
            onNextLevel = {
                if (viewModel.hasNextLevel()) {
                    val nextLevelId = viewModel.getCurrentLevelId() + 1
                    navController.navigate(Screens.Game(nextLevelId)) {
                        popUpTo(Screens.Game(game.levelId)) { inclusive = true }
                    }
                }
            }
        )
    }
}