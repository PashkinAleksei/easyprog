package ru.lemonapes.easyprog.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.lemonapes.easyprog.android.MainViewModel
import ru.lemonapes.easyprog.android.ui.columns.CodeColumn
import ru.lemonapes.easyprog.android.ui.columns.CommandsColumn
import ru.lemonapes.easyprog.android.ui.columns.SourceColumn
import ru.lemonapes.easyprog.android.ui.dialogs.TryAgainDialog
import ru.lemonapes.easyprog.android.ui.dialogs.VictoryDialog
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {
    val viewState by viewModel.viewState.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = AppDimensions.padding)
            .padding(horizontal = AppDimensions.padding),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.padding)
    ) {
        Button(onClick = {
            viewModel.executeCommands()
        }) {
            Text("Старт")
        }
    }

    if (viewState.showVictoryDialog) {
        VictoryDialog(onDismiss = viewModel::onVictoryDialogDismiss)
    }

    if (viewState.showTryAgainDialog) {
        TryAgainDialog(onDismiss = viewModel::onTryAgainDialogDismiss)
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(top = AppDimensions.topPadding)
            .padding(AppDimensions.padding),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.padding)
    ) {
        CodeColumn(viewState.codeItems)
        CommandsColumn(viewState, viewModel)
        SourceColumn(viewModel, viewState.sourceItems)
    }
}