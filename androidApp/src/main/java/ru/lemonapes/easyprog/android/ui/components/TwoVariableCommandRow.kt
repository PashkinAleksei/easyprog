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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.GameListener
import ru.lemonapes.easyprog.android.MyApplicationTheme
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.JumpIfZeroCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.commands.PairCommand
import ru.lemonapes.easyprog.android.commands.TwoVariableCommand
import ru.lemonapes.easyprog.android.preview.PreviewGameListener
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp12
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp16
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp2
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp4
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp8
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp20
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp28
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun TwoVariableCommand.CommandRow(
    index: Int,
    codeItems: ImmutableList<CodePeace>,
    listener: GameListener,
    modifier: Modifier = Modifier,
) {
    val variables = remember(codeItems) {
        codeItems.filterIsInstance<CodePeace.IntVariable>().map { it }.toImmutableList()
    }

    Row(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.width(dp8))
        when (this@CommandRow) {
            is CopyValueCommand, is MoveValueCommand ->
                Box(
                    modifier = Modifier
                        .clip(AppShapes.CORNER_MEDIUM)
                        .background(AppColors.COLOR_ACCENT)

                ) {
                    Box(Modifier.padding(vertical = dp4, horizontal = dp8)) {
                        Image(
                            modifier = Modifier.size(AppDimensions.iconSize),
                            painter = painterResource(iconRes),
                            contentDescription = stringResource(textRes),
                            colorFilter = ColorFilter.tint(AppColors.MAIN_COLOR),
                        )
                    }
                }
        }
        Spacer(modifier = Modifier.width(dp12))

        IntVariableDropdownBox(
            selectedIndex = source,
            codeItems = codeItems,
            variables = variables,
            onVariableSelected = { variable ->
                when (this@CommandRow) {
                    is MoveValueCommand -> listener.onUpdateCommand(
                        index,
                        copy(source = codeItems.indexOf(variable))
                    )

                    is CopyValueCommand -> listener.onUpdateCommand(
                        index,
                        copy(source = codeItems.indexOf(variable))
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(dp2))
        Image(
            modifier = Modifier
                .padding(top = dp2)
                .size(dp28),
            painter = painterResource(R.drawable.ic_arrow_right_alt),
            contentDescription = stringResource(R.string.to),
            colorFilter = ColorFilter.tint(AppColors.COLOR_ACCENT),
        )
        Spacer(modifier = Modifier.width(dp2))

        IntVariableDropdownBox(
            selectedIndex = target,
            codeItems = codeItems,
            variables = variables,
            onVariableSelected = { variable ->
                when (this@CommandRow) {
                    is CopyValueCommand -> listener.onUpdateCommand(
                        index,
                        copy(target = codeItems.indexOf(variable))
                    )

                    is MoveValueCommand -> listener.onUpdateCommand(
                        index,
                        copy(target = codeItems.indexOf(variable))
                    )
                }
            }
        )

        Spacer(modifier = Modifier.weight(0.7f))
    }
}


@Preview(showBackground = true)
@Composable
private fun TwoVariableCommandRowPreview() {
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

    val commandFirst = CopyValueCommand(
        id = Calendar.getInstance().timeInMillis + 2,
        target = 0
    )

    MyApplicationTheme {
        Column {
            commandFirst.CommandRow(
                modifier = Modifier.padding(vertical = AppDimensions.dp8),
                index = 0,
                codeItems = codeItems,
                listener = PreviewGameListener()
            )
        }
    }
}