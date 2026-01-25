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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.GameListener
import ru.lemonapes.easyprog.android.commands.IncValueCommand
import ru.lemonapes.easyprog.android.commands.SingleVariableCommand
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp4
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp8
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp16
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions.dp20
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun SingleVariableCommand.CommandRow(
    modifier: Modifier = Modifier,
    index: Int,
    codeItems: ImmutableList<CodePeace>,
    listener: GameListener,
) {
    val variables = remember(codeItems) {
        codeItems.filterIsInstance<CodePeace.IntVariable>().map { it }.toImmutableList()
    }

    Row(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.width(dp16))
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
        Spacer(modifier = Modifier.width(dp20))

        IntVariableDropdownBox(
            selectedIndex = target,
            codeItems = codeItems,
            variables = variables,
            onVariableSelected = { variable ->
                when (this@CommandRow) {
                    is IncValueCommand -> listener.onUpdateCommand(
                        index,
                        copy(target = codeItems.indexOf(variable))
                    )
                }
            }
        )

        Spacer(modifier = Modifier.weight(0.7f))
    }
}