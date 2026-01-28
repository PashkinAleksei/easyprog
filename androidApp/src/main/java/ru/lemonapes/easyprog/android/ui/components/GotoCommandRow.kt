package ru.lemonapes.easyprog.android.ui.components

import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.persistentListOf
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.GameViewModel
import ru.lemonapes.easyprog.android.MyApplicationTheme
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.GotoCommand
import ru.lemonapes.easyprog.android.commands.JumpIfZeroCommand
import ru.lemonapes.easyprog.android.commands.PairCommand
import ru.lemonapes.easyprog.android.preview.PreviewGameListener
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp6
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp16
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp28
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp8
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun GotoCommand.CommandRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.width(dp8))
        Box(
            modifier = Modifier
                .size(AppDimensions.commandVariableBoxSize)
                .clip(AppShapes.CORNER_SMALL)
                .background(AppColors.LabelColors[colorIndex])
        )
        if (type == PairCommand.PairType.FIRST) {
            Image(
                modifier = Modifier
                    .size(dp28),
                painter = painterResource(R.drawable.ic_arrow_down_left),
                contentDescription = stringResource(R.string.to_portal),
                colorFilter = ColorFilter.tint(AppColors.COLOR_ACCENT),
            )
        } else {
            Image(
                modifier = Modifier
                    .padding(top = dp6)
                    .size(dp28),
                painter = painterResource(R.drawable.ic_arrow_right_down),
                contentDescription = stringResource(R.string.from_portal),
                colorFilter = ColorFilter.tint(AppColors.COLOR_ACCENT),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JumpIfZeroCommandRowPairPreview() {
    val codeItems = persistentListOf(
        CodePeace.IntVariable(
            id = Calendar.getInstance().timeInMillis,
            value = 0,
            isMutable = true,
            colorIndex = 0,
        ),
        CodePeace.IntVariable(
            id = Calendar.getInstance().timeInMillis + 1,
            value = 5,
            isMutable = true,
            colorIndex = 1,
        )
    )

    val command1 = GotoCommand(
        id = Calendar.getInstance().timeInMillis + 2,
        type = PairCommand.PairType.FIRST,
        pairId = 12345L,
        colorIndex = 0,
    )

    val command2 = GotoCommand(
        id = Calendar.getInstance().timeInMillis + 3,
        type = PairCommand.PairType.SECOND,
        pairId = 12345L,
        colorIndex = 0,
    )

    val command3 = CopyValueCommand(
        id = Calendar.getInstance().timeInMillis + 2,
        target = 0
    )

    MyApplicationTheme {
        Column {
            command1.CommandRow(
                modifier = Modifier.padding(vertical = AppDimensions.dp8),
            )
            command3.CommandRow(
                modifier = Modifier.padding(vertical = AppDimensions.dp8),
                index = 0,
                codeItems = codeItems,
                listener = PreviewGameListener()
            )
            command2.CommandRow(
                modifier = Modifier.padding(vertical = AppDimensions.dp8),
            )
        }
    }
}