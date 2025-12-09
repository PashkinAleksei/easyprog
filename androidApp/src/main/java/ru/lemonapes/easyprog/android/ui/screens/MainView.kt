package ru.lemonapes.easyprog.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.lemonapes.easyprog.android.MainViewModel
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.ui.columns.CodeColumn
import ru.lemonapes.easyprog.android.ui.columns.CommandsColumn
import ru.lemonapes.easyprog.android.ui.columns.SourceColumn
import ru.lemonapes.easyprog.android.ui.dialogs.TryAgainDialog
import ru.lemonapes.easyprog.android.ui.dialogs.VictoryDialog
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {
    val viewState by viewModel.viewState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.padding16)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppDimensions.spacing2)
                .height(60.dp),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.padding16)
        ) {
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { viewModel.executeCommands() },
                modifier = Modifier
                    .padding(end = AppDimensions.padding16)
                    .size(60.dp)
            ) {
                Icon(
                    modifier = Modifier.size(AppDimensions.mainIconSize),
                    painter = painterResource(R.drawable.play_triangle),
                    contentDescription = stringResource(R.string.start_button_description),
                    tint = AppColors.PlayButtonColor
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.padding16)
        ) {
            CodeColumn(viewState.codeItems)
            CommandsColumn(viewState, viewModel)
            SourceColumn(viewModel, viewState.sourceItems)
        }
    }


    if (viewState.showVictoryDialog) {
        VictoryDialog(onDismiss = viewModel::onVictoryDialogDismiss)
    }

    if (viewState.showTryAgainDialog) {
        TryAgainDialog(onDismiss = viewModel::onTryAgainDialogDismiss)
    }
}