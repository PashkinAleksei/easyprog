package ru.lemonapes.easyprog.android.ui.components

import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.GameListener
import ru.lemonapes.easyprog.android.MyApplicationTheme
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.JumpIfZeroCommand
import ru.lemonapes.easyprog.android.commands.PairCommand
import ru.lemonapes.easyprog.android.preview.PreviewGameListener
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp6
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp12
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp16
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp2
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp20
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp28
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp32
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp4
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp8
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

/**
 * Компонент для отображения команды JumpIfZero.
 * FIRST тип: показывает цветную метку, выбор переменной и условную иконку перехода.
 * SECOND тип: показывает цветную метку и иконку цели перехода.
 */
@Composable
fun JumpIfZeroCommand.CommandRow(
    modifier: Modifier = Modifier,
    index: Int,
    codeItems: ImmutableList<CodePeace>,
    listener: GameListener,
) {
    val variables = remember(codeItems) {
        codeItems.filterIsInstance<CodePeace.IntVariable>().toImmutableList()
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(dp8))

        if (type == PairCommand.PairType.FIRST) {

            // Иконка условия "Если 0"
            Box(
                modifier = Modifier
                    .clip(AppShapes.CORNER_MEDIUM)
                    .background(AppColors.COLOR_ACCENT)
                    .padding(vertical = dp4, horizontal = dp8)
            ) {
                Image(
                    modifier = Modifier.size(AppDimensions.iconSize),
                    painter = painterResource(R.drawable.ic_jump_if_zero),
                    contentDescription = stringResource(R.string.command_jump_if_zero_start),
                    colorFilter = ColorFilter.tint(AppColors.MAIN_COLOR),
                )
            }

            Spacer(modifier = Modifier.width(dp12))

            // Выбор переменной для проверки
            IntVariableDropdownBox(
                selectedIndex = target,
                codeItems = codeItems,
                variables = variables,
                onVariableSelected = { variable ->
                    listener.onUpdateCommand(
                        index,
                        copy(target = codeItems.indexOf(variable))
                    )
                }
            )

            Text(
                modifier = Modifier.padding(start = dp4),
                text = "=0?",
                fontSize = 18.sp,
                color = AppColors.COLOR_ACCENT,
                fontWeight = FontWeight.Bold,
            )

            Image(
                modifier = Modifier
                    .padding(top = dp2)
                    .size(dp28),
                painter = painterResource(R.drawable.ic_arrow_right_alt),
                contentDescription = stringResource(R.string.to),
                colorFilter = ColorFilter.tint(AppColors.COLOR_ACCENT),
            )
        }

        // Цветная метка для визуального связывания пары
        Box(
            modifier = Modifier
                .size(AppDimensions.commandVariableBoxSize)
                .clip(AppShapes.CORNER_SMALL)
                .background(AppColors.LabelColors[colorIndex])
                .border(2.dp, AppColors.COLOR_ACCENT, AppShapes.CORNER_SMALL)
        )

        if (type != PairCommand.PairType.FIRST) {
            // SECOND: только иконка цели перехода
            Image(
                modifier = Modifier
                    .padding(top = dp6)
                    .size(dp28),
                painter = painterResource(R.drawable.ic_arrow_right_down),
                contentDescription = stringResource(R.string.command_jump_if_zero_target),
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

    val command1 = JumpIfZeroCommand(
        id = Calendar.getInstance().timeInMillis + 2,
        type = PairCommand.PairType.FIRST,
        pairId = 12345L,
        colorIndex = 0,
        target = 0
    )

    val command2 = JumpIfZeroCommand(
        id = Calendar.getInstance().timeInMillis + 3,
        type = PairCommand.PairType.SECOND,
        pairId = 12345L,
        colorIndex = 0,
        target = null
    )

    val command3 = CopyValueCommand(
        id = Calendar.getInstance().timeInMillis + 2,
        target = 0
    )

    MyApplicationTheme {
        Column {
            command1.CommandRow(
                modifier = Modifier.padding(vertical = AppDimensions.dp8),
                index = 0,
                codeItems = codeItems,
                listener = PreviewGameListener()
            )
            command3.CommandRow(
                modifier = Modifier.padding(vertical = AppDimensions.dp8),
                index = 0,
                codeItems = codeItems,
                listener = PreviewGameListener()
            )
            command2.CommandRow(
                modifier = Modifier.padding(vertical = AppDimensions.dp8),
                index = 1,
                codeItems = codeItems,
                listener = PreviewGameListener()
            )
        }
    }
}