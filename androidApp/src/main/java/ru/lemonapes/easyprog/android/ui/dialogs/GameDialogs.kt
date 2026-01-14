package ru.lemonapes.easyprog.android.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun VictoryDialog(
    hasNextLevel: Boolean,
    onReplay: () -> Unit,
    onMenu: () -> Unit,
    onNextLevel: () -> Unit,
) {
    GameDialog(
        title = stringResource(R.string.victory_title),
        message = stringResource(R.string.victory_message)
    ) {
        DialogActionButton(
            iconRes = R.drawable.ic_replay,
            contentDescription = stringResource(R.string.replay),
            onClick = onReplay
        )
        DialogActionButton(
            iconRes = R.drawable.ic_menu,
            contentDescription = stringResource(R.string.menu),
            onClick = onMenu
        )
        if (hasNextLevel) {
            DialogActionButton(
                iconRes = R.drawable.ic_next,
                contentDescription = stringResource(R.string.next_level),
                onClick = onNextLevel
            )
        }
    }
}

@Composable
fun TryAgainDialog(
    onReplay: () -> Unit,
    onMenu: () -> Unit,
) {
    GameDialog(
        title = stringResource(R.string.try_again_title),
        message = stringResource(R.string.try_again_message)
    ) {
        DialogActionButton(
            iconRes = R.drawable.ic_replay,
            contentDescription = stringResource(R.string.replay),
            onClick = onReplay
        )
        DialogActionButton(
            iconRes = R.drawable.ic_menu,
            contentDescription = stringResource(R.string.menu),
            onClick = onMenu
        )
    }
}

@Composable
fun LevelInfoDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
) {
    GameDialog(
        title = title,
        message = description,
        dismissable = true,
        onDismissRequest = onDismiss,
    ) {
        DialogActionButton(
            iconRes = R.drawable.ic_check,
            contentDescription = stringResource(R.string.ok),
            onClick = onDismiss
        )
    }
}

@Composable
fun ClearCommandsDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    GameDialog(
        title = stringResource(R.string.clear_commands_title),
        message = stringResource(R.string.clear_commands_message),
        dismissable = true,
        onDismissRequest = onCancel,
    ) {
        DialogActionButton(
            iconRes = R.drawable.ic_check,
            contentDescription = stringResource(R.string.yes),
            onClick = onConfirm
        )
        DialogActionButton(
            iconRes = R.drawable.ic_close,
            contentDescription = stringResource(R.string.no),
            onClick = onCancel
        )
    }
}

@Composable
private fun GameDialog(
    title: String,
    message: String,
    dismissable: Boolean = false,
    onDismissRequest: () -> Unit = {},
    buttons: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissable,
            dismissOnClickOutside = dismissable
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = AppColors.COLOR_ACCENT,
                    shape = AppShapes.CORNER_MEDIUM
                )
                .border(
                    width = AppDimensions.columnBorderWidth,
                    color = AppColors.MAIN_COLOR,
                    shape = AppShapes.CORNER_MEDIUM
                )
                .padding(AppDimensions.dp32)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.dp16)
            ) {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.MAIN_COLOR,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = message,
                    fontSize = 18.sp,
                    color = AppColors.MAIN_COLOR,
                    textAlign = TextAlign.Center,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.dp16),
                    modifier = Modifier.padding(top = AppDimensions.dp8)
                ) {
                    buttons()
                }
            }
        }
    }
}

@Composable
private fun DialogActionButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(AppDimensions.dialogIconButtonSize)
            .background(
                color = AppColors.MAIN_COLOR,
                shape = AppShapes.CORNER_SMALL
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = AppColors.COLOR_ACCENT,
            modifier = Modifier.size(AppDimensions.iconSize)
        )
    }
}