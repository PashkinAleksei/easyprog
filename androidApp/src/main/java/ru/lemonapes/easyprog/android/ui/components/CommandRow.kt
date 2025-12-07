package ru.lemonapes.easyprog.android.ui.components

import android.content.ClipData
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.MainViewModel
import ru.lemonapes.easyprog.android.R
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.commands.TwoVariableCommand
import ru.lemonapes.easyprog.android.ui.theme.AppColors
import ru.lemonapes.easyprog.android.ui.theme.AppDimensions
import ru.lemonapes.easyprog.android.ui.theme.AppShapes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TwoVariableCommand.CommandRow(
    index: Int,
    codeItems: List<CodePeace>,
    isExecuting: Boolean,
    viewModel: MainViewModel,
) {
    val variables = codeItems.filterIsInstance<CodePeace.IntVariable>().map { it }

    val expanded1 = remember { mutableStateOf(false) }

    val expanded2 = remember { mutableStateOf(false) }
    val backgroundColor = if (isExecuting) AppColors.CommandBackgroundExecuting else AppColors.CommandBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.padding)
            .background(
                color = backgroundColor,
                shape = AppShapes.cornerMedium
            )
            .dragAndDropSource { _ ->
                viewModel.setDraggedCommandItem(viewModel.removeCommand(index))
                DragAndDropTransferData(
                    ClipData.newPlainText("dragged_item", text)
                )
            }
            .padding(AppDimensions.padding),
    ) {
        Spacer(modifier = Modifier.weight(0.7f))
        when (this@CommandRow) {
            is CopyValueCommand ->
                Box(
                    modifier = Modifier
                        .clip(AppShapes.cornerMedium)
                        .background(AppColors.CommandDarkGreen)

                ) {
                    Box(Modifier.padding(vertical = AppDimensions.paddingSmall, horizontal = AppDimensions.paddingMedium)) {
                        Image(
                            modifier = Modifier.size(AppDimensions.iconSize),
                            painter = painterResource(R.drawable.copy),
                            contentDescription = "Copy command"
                        )
                    }
                }

            is MoveValueCommand ->
                Box(
                    modifier = Modifier
                        .clip(AppShapes.cornerMedium)
                        .background(AppColors.CommandDarkGreen)
                ) {
                    Box(Modifier.padding(vertical = AppDimensions.paddingSmall, horizontal = AppDimensions.paddingMedium)) {
                        Image(
                            modifier = Modifier.size(AppDimensions.iconSize),
                            painter = painterResource(R.drawable.cut),
                            contentDescription = "Cut command"
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // First dropdown
        Box(Modifier.clickable { expanded1.value = true }) {
            source?.second?.let { index ->
                val codePeace = codeItems[index]
                if (codePeace is CodePeace.IntVariable) {
                    codePeace.VariableBox(Modifier.size(AppDimensions.variableBoxSmall))
                } else null
            } ?: Box(
                modifier = Modifier
                    .size(AppDimensions.variableBoxSmall)
                    .background(
                        color = AppColors.CommandDarkGreen,
                        shape = AppShapes.cornerSmall
                    )
            ) {
                Text(
                    text = source?.first ?: "?",
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = AppColors.CommandDarkGreen,
                            shape = AppShapes.cornerSmall
                        )
                        .align(Alignment.Center)
                )
            }
            DropdownMenu(
                expanded = expanded1.value,
                onDismissRequest = { expanded1.value = false }
            ) {
                variables.forEach { variable ->
                    DropdownMenuItem(
                        text = { Text(variable.name) },
                        onClick = {
                            when (this@CommandRow) {
                                is MoveValueCommand -> viewModel.updateCommand(
                                    index,
                                    copy(source = variable.name to codeItems.indexOf(variable))
                                )

                                is CopyValueCommand -> viewModel.updateCommand(
                                    index,
                                    copy(source = variable.name to codeItems.indexOf(variable))
                                )
                            }
                            expanded1.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))

        Text(
            text = "\u2192",
            color = AppColors.TextPrimary,
            style = TextStyle(
                fontWeight = FontWeight.W900,
                fontSize = AppDimensions.arrowFontSize,
            )
        )

        Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))

        // Second dropdown
        Box(Modifier.clickable { expanded2.value = true }) {
            target?.second?.let { index ->
                val codePeace = codeItems[index]
                if (codePeace is CodePeace.IntVariable) {
                    codePeace.VariableBox(Modifier.size(AppDimensions.variableBoxSmall))
                } else null
            } ?: Box(
                modifier = Modifier
                    .size(AppDimensions.variableBoxSmall)
                    .background(
                        color = AppColors.CommandDarkGreen,
                        shape = AppShapes.cornerSmall
                    )
                    .clickable { expanded2.value = true }) {
                Text(
                    text = target?.first ?: "?",
                    textAlign = TextAlign.Center,
                    color = AppColors.TextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
            DropdownMenu(
                expanded = expanded2.value,
                onDismissRequest = { expanded2.value = false }
            ) {
                variables.forEach { variable ->
                    DropdownMenuItem(
                        text = { Text(variable.name) },
                        onClick = {
                            when (this@CommandRow) {
                                is CopyValueCommand -> viewModel.updateCommand(
                                    index,
                                    copy(target = variable.name to codeItems.indexOf(variable))
                                )

                                is MoveValueCommand -> viewModel.updateCommand(
                                    index,
                                    copy(target = variable.name to codeItems.indexOf(variable))
                                )
                            }
                            expanded2.value = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(0.7f))
    }
}