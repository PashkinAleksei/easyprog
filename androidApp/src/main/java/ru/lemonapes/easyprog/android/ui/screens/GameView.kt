package ru.lemonapes.easyprog.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.MyApplicationTheme
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.ui.columns.CodeColumn
import ru.lemonapes.easyprog.android.ui.columns.CommandsColumn
import ru.lemonapes.easyprog.android.ui.columns.SourceColumn
import ru.lemonapes.easyprog.android.ui.dialogs.ClearCommandsDialog
import ru.lemonapes.easyprog.android.ui.dialogs.LevelInfoDialog
import ru.lemonapes.easyprog.android.ui.dialogs.TryAgainDialog
import ru.lemonapes.easyprog.android.ui.dialogs.VictoryDialog
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions

@Composable
fun GameView(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    onBackToMenu: () -> Unit = {},
    onNextLevel: () -> Unit = {},
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
            verticalAlignment = Alignment.CenterVertically
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

            Spacer(
                modifier = Modifier
                    .padding(end = AppDimensions.dp16)
                    .size(AppDimensions.mainIconButtonSize)
            )

            val levelPrefix = stringResource(R.string.level_prefix)
            val levelTitle = "$levelPrefix${viewState.levelId} ${viewState.levelTitle}"

            Text(
                modifier = Modifier.weight(1f),
                text = levelTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.COLOR_ACCENT,
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = { viewModel.showLevelInfoDialog() },
                modifier = Modifier
                    .padding(start = AppDimensions.dp16)
                    .size(AppDimensions.mainIconButtonSize)
            ) {
                Icon(
                    modifier = Modifier.size(AppDimensions.questionIconSize),
                    painter = painterResource(R.drawable.ic_question_2),
                    contentDescription = stringResource(R.string.level_info),
                    tint = AppColors.COLOR_ACCENT
                )
            }

            IconButton(
                onClick = { viewModel.showClearCommandsDialog() },
                modifier = Modifier.size(AppDimensions.mainIconButtonSize),
                enabled = viewState.commandItems.isNotEmpty()
            ) {
                Icon(
                    modifier = Modifier.size(AppDimensions.clearCommandsIconSize),
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.clear_commands),
                    tint = if (viewState.commandItems.isNotEmpty())
                        AppColors.COLOR_ACCENT
                    else
                        AppColors.COLOR_ACCENT.copy(alpha = 0.3f)
                )
            }

            if (viewState.isCommandExecution) {
                IconButton(
                    onClick = { viewModel.stopExecution() },
                    modifier = Modifier
                        .padding(end = AppDimensions.dp16)
                        .size(AppDimensions.mainIconButtonSize)
                ) {
                    Icon(
                        modifier = Modifier.size(AppDimensions.stopIconSize),
                        painter = painterResource(R.drawable.ic_stop),
                        contentDescription = stringResource(R.string.stop_button_description),
                        tint = AppColors.StopButtonColor
                    )
                }
            } else {
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
        }

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.dp16)
        ) {
            CodeColumn(viewState.codeItems)
            CommandsColumn(viewState, viewModel)
            SourceColumn(viewModel, viewState.sourceItems, viewState.isCommandExecution)
        }
    }

    if (viewState.showLevelInfoDialog) {
        LevelInfoDialog(
            title = viewState.levelTitle,
            description = viewState.levelDescription,
            onDismiss = { viewModel.hideLevelInfoDialog() }
        )
    }

    if (viewState.showVictoryDialog) {
        VictoryDialog(
            hasNextLevel = viewModel.hasNextLevel(),
            onReplay = viewModel::onVictoryReplay,
            onMenu = { viewModel.onVictoryMenu(onBackToMenu) },
            onNextLevel = { viewModel.onVictoryNextLevel(onNextLevel) }
        )
    }

    if (viewState.showTryAgainDialog) {
        TryAgainDialog(
            onReplay = viewModel::onTryAgainReplay,
            onMenu = { viewModel.onTryAgainMenu(onBackToMenu) }
        )
    }

    if (viewState.showClearCommandsDialog) {
        ClearCommandsDialog(
            onConfirm = {
                viewModel.clearCommands()
                viewModel.hideClearCommandsDialog()
            },
            onCancel = { viewModel.hideClearCommandsDialog() }
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
private fun GameViewPreview() {
    MyApplicationTheme {
        Surface {
            val viewModel = viewModel<GameViewModel>()

            LaunchedEffect(Unit) {
                viewModel.addCommand(CopyValueCommand(), true)
                viewModel.addCommand(MoveValueCommand(), true)
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
            val viewModel = viewModel<GameViewModel>()
            viewModel.loadLevel(1)

            GameView(
                viewModel = viewModel,
                onBackToMenu = {}
            )
        }
    }
}