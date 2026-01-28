package ru.lemonapes.easyprog.android.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.GameListener
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.JumpIfZeroCommand
import ru.lemonapes.easyprog.android.commands.PairCommand
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp6
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp12
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp16
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp20
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp28
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
        Spacer(modifier = Modifier.width(dp16))

        // Цветная метка для визуального связывания пары
        Box(
            modifier = Modifier
                .size(AppDimensions.commandVariableBoxSize)
                .clip(AppShapes.CORNER_SMALL)
                .background(AppColors.LabelColors[colorIndex])
        )

        if (type == PairCommand.PairType.FIRST) {
            // FIRST: показываем выбор переменной + условную иконку
            Spacer(modifier = Modifier.width(dp12))

            // Иконка условия "Если 0"
            Box(
                modifier = Modifier
                    .clip(AppShapes.CORNER_MEDIUM)
                    .background(AppColors.COLOR_ACCENT)
                    .padding(vertical = dp6, horizontal = dp12)
            ) {
                Image(
                    modifier = Modifier.size(AppDimensions.iconSize),
                    painter = painterResource(R.drawable.ic_jump_if_zero),
                    contentDescription = stringResource(R.string.command_jump_if_zero_start),
                    colorFilter = ColorFilter.tint(AppColors.MAIN_COLOR),
                )
            }

            Spacer(modifier = Modifier.width(dp20))

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

            Spacer(modifier = Modifier.weight(0.7f))
        } else {
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