package ru.lemonapes.easyprog.android.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions

@Composable
fun VictoryDialog(
    onReplay: () -> Unit,
    onMenu: () -> Unit,
    onNextLevel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.victory_title)) },
        text = { Text(stringResource(R.string.victory_message)) },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.dp16)
            ) {
                IconButton(
                    onClick = onReplay,
                    modifier = Modifier.size(AppDimensions.dialogIconButtonSize)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_replay),
                        contentDescription = stringResource(R.string.replay)
                    )
                }
                IconButton(
                    onClick = onMenu,
                    modifier = Modifier.size(AppDimensions.dialogIconButtonSize)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = stringResource(R.string.menu)
                    )
                }
                IconButton(
                    onClick = onNextLevel,
                    modifier = Modifier.size(AppDimensions.dialogIconButtonSize)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_next),
                        contentDescription = stringResource(R.string.next_level)
                    )
                }
            }
        }
    )
}

@Composable
fun TryAgainDialog(
    onReplay: () -> Unit,
    onMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.try_again_title)) },
        text = { Text(stringResource(R.string.try_again_message)) },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.dp16)
            ) {
                IconButton(
                    onClick = onReplay,
                    modifier = Modifier.size(AppDimensions.dialogIconButtonSize)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_replay),
                        contentDescription = stringResource(R.string.replay)
                    )
                }
                IconButton(
                    onClick = onMenu,
                    modifier = Modifier.size(AppDimensions.dialogIconButtonSize)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = stringResource(R.string.menu)
                    )
                }
            }
        }
    )
}