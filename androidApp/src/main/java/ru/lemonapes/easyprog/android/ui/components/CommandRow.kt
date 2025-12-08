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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(horizontal = AppDimensions.padding16)
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
            .padding(AppDimensions.padding12),
    ) {
        Spacer(modifier = Modifier.weight(0.7f))
        when (this@CommandRow) {
            is CopyValueCommand ->
                Box(
                    modifier = Modifier
                        .clip(AppShapes.cornerMedium)
                        .background(AppColors.CommandAccent)

                ) {
                    Box(Modifier.padding(vertical = AppDimensions.padding4, horizontal = AppDimensions.padding8)) {
                        Image(
                            modifier = Modifier.size(AppDimensions.iconSize),
                            painter = painterResource(R.drawable.copy),
                            contentDescription = "Copy command",
                            colorFilter = ColorFilter.tint(AppColors.CommandBackground),
                        )
                    }
                }

            is MoveValueCommand ->
                Box(
                    modifier = Modifier
                        .clip(AppShapes.cornerMedium)
                        .background(AppColors.CommandAccent)
                ) {
                    Box(Modifier.padding(vertical = AppDimensions.padding4, horizontal = AppDimensions.padding8)) {
                        Image(
                            modifier = Modifier.size(AppDimensions.iconSize),
                            painter = painterResource(R.drawable.cut),
                            contentDescription = "Cut command",
                            colorFilter = ColorFilter.tint(AppColors.CommandBackground),
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // First dropdown
        Box(Modifier.clickable { expanded1.value = true }) {
            source?.let { index ->
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
                    text = "?",
                    color = AppColors.CommandBackground,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight= FontWeight.SemiBold,
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
                expanded = expanded1.value,
                onDismissRequest = { expanded1.value = false }
            ) {
                variables.forEach { variable ->
                    DropdownMenuItem(
                        text = {
                            variable.VariableBox(Modifier.size(AppDimensions.commandVariableBoxSize))
                        },
                        onClick = {
                            when (this@CommandRow) {
                                is MoveValueCommand -> viewModel.updateCommand(
                                    index,
                                    copy(source = codeItems.indexOf(variable))
                                )

                                is CopyValueCommand -> viewModel.updateCommand(
                                    index,
                                    copy(source = codeItems.indexOf(variable))
                                )
                            }
                            expanded1.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(AppDimensions.spacing2))

        Image(
            modifier = Modifier.padding(top=AppDimensions.spacing2).size(28.dp),
            painter = painterResource(R.drawable.arrow_right_alt),
            contentDescription = "to",
            colorFilter = ColorFilter.tint(AppColors.CommandAccent),
        )

        Spacer(modifier = Modifier.width(AppDimensions.spacing2))

        // Second dropdown
        Box(Modifier.clickable { expanded2.value = true }) {
            target?.let { index ->
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
                    .clickable { expanded2.value = true }) {
                Text(
                    text = "?",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    color = AppColors.CommandBackground,
                    fontWeight= FontWeight.Bold,
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
                        text = {
                            variable.VariableBox(Modifier.size(AppDimensions.commandVariableBoxSize))
                        },
                        onClick = {
                            when (this@CommandRow) {
                                is CopyValueCommand -> viewModel.updateCommand(
                                    index,
                                    copy(target = codeItems.indexOf(variable))
                                )

                                is MoveValueCommand -> viewModel.updateCommand(
                                    index,
                                    copy(target = codeItems.indexOf(variable))
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