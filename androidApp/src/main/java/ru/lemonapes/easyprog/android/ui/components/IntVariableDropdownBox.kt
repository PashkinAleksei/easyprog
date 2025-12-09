package ru.lemonapes.easyprog.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@Composable
fun IntVariableDropdownBox(
    selectedIndex: Int?,
    codeItems: ImmutableList<CodePeace>,
    variables: ImmutableList<CodePeace.IntVariable>,
    onVariableSelected: (CodePeace.IntVariable) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expanded = remember { mutableStateOf(false) }

    Box(modifier.clickable { expanded.value = true }) {
        selectedIndex?.let { index ->
            val codePeace = codeItems[index]
            if (codePeace is CodePeace.IntVariable) {
                codePeace.VariableBox(Modifier.size(AppDimensions.commandVariableBoxSize))
            } else null
        } ?: Box(
            modifier = Modifier
                .size(AppDimensions.commandVariableBoxSize)
                .background(
                    color = AppColors.CommandAccent,
                    shape = AppShapes.cornerSmall
                )
        ) {
            Text(
                text = stringResource(R.string.unknown_variable),
                color = AppColors.CommandBackground,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = AppColors.CommandAccent,
                        shape = AppShapes.cornerSmall
                    )
                    .align(Alignment.Center)
            )
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            variables.forEach { variable ->
                DropdownMenuItem(
                    text = {
                        variable.VariableBox(Modifier.size(AppDimensions.commandVariableBoxSize))
                    },
                    onClick = {
                        onVariableSelected(variable)
                        expanded.value = false
                    }
                )
            }
        }
    }
}