package ru.lemonapes.easyprog.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.lemonapes.easyprog.android.MainViewModel
import ru.lemonapes.easyprog.android.MyApplicationTheme
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.ui.columns.CodeColumn
import ru.lemonapes.easyprog.android.ui.columns.CommandsColumn
import ru.lemonapes.easyprog.android.ui.columns.SourceColumn
import ru.lemonapes.easyprog.android.ui.dialogs.TryAgainDialog
import ru.lemonapes.easyprog.android.ui.dialogs.VictoryDialog
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions

@Composable
fun GameView(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onBackToMenu: () -> Unit = {},
) {
    val viewState by viewModel.viewState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.dp16)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppDimensions.dp2)
                .height(AppDimensions.playIconSize),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.dp16)
        ) {
            IconButton(
                onClick = onBackToMenu,
                modifier = Modifier
                    .padding(start = AppDimensions.dp16)
                    .size(AppDimensions.mainIconButtonSize)
            ) {
                Icon(
                    modifier = Modifier.size(AppDimensions.menuIconSize),
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = stringResource(R.string.back_to_menu_description),
                    tint = AppColors.COLOR_ACCENT
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { viewModel.executeCommands() },
                modifier = Modifier
                    .padding(end = AppDimensions.dp16)
                    .size(AppDimensions.mainIconButtonSize)
            ) {
                Icon(
                    modifier = Modifier.size(AppDimensions.playIconSize),
                    painter = painterResource(R.drawable.ic_play_triangle),
                    contentDescription = stringResource(R.string.start_button_description),
                    tint = AppColors.PlayButtonColor
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.dp16)
        ) {
            CodeColumn(viewState.codeItems)
            CommandsColumn(viewState, viewModel)
            SourceColumn(viewModel, viewState.sourceItems)
        }
    }


    if (viewState.showVictoryDialog) {
        VictoryDialog(
            onReplay = viewModel::onVictoryReplay,
            onMenu = { viewModel.onVictoryMenu(onBackToMenu) },
            onNextLevel = { viewModel.onVictoryNextLevel(onBackToMenu) }
        )
    }

    if (viewState.showTryAgainDialog) {
        TryAgainDialog(
            onReplay = viewModel::onTryAgainReplay,
            onMenu = { viewModel.onTryAgainMenu(onBackToMenu) }
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
private fun GameViewPreview() {
    MyApplicationTheme {
        Surface {
            val viewModel = viewModel<MainViewModel>()

            LaunchedEffect(Unit) {
                viewModel.addCommand(CopyValueCommand())
                viewModel.addCommand(MoveValueCommand())
            }

            GameView(
                viewModel = viewModel,
                onBackToMenu = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
private fun GameViewEmptyPreview() {
    MyApplicationTheme {
        Surface {
            val viewModel = viewModel<MainViewModel>()

            GameView(
                viewModel = viewModel,
                onBackToMenu = {}
            )
        }
    }
}