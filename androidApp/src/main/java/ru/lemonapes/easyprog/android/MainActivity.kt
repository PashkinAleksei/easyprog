package ru.lemonapes.easyprog.android

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.commands.CopyVariableToVariable
import ru.lemonapes.easyprog.android.drag_and_drop_target.createBotItemDragAndDropTarget
import ru.lemonapes.easyprog.android.drag_and_drop_target.createColumnDragAndDropTarget
import ru.lemonapes.easyprog.android.drag_and_drop_target.createGlobalDragAndDropTarget
import ru.lemonapes.easyprog.android.drag_and_drop_target.createTopItemDragAndDropTarget

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем edge-to-edge режим и скрываем системные бары
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            MyApplicationTheme {
                val globalDragAndDropTarget = remember {
                    createGlobalDragAndDropTarget(viewModel)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .dragAndDropTextTarget(globalDragAndDropTarget),
                ) { paddings ->
                    MainRow(
                        modifier = Modifier.padding(paddings),
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainRow(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {
    val viewState by viewModel.viewState.collectAsState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = {
            viewModel.executeCommands()
        }) {
            Text("Старт")
        }
    }

    if (viewState.showVictoryDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onVictoryDialogDismiss,
            title = { Text("Поздравляем!") },
            text = { Text("Вы победили!") },
            confirmButton = {
                TextButton(onClick = viewModel::onVictoryDialogDismiss) {
                    Text("OK")
                }
            }
        )
    }

    if (viewState.showTryAgainDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onTryAgainDialogDismiss,
            title = { Text("Попробуйте еще") },
            text = { Text("Условие победы не выполнено") },
            confirmButton = {
                TextButton(onClick = viewModel::onTryAgainDialogDismiss) {
                    Text("OK")
                }
            }
        )
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CodeColumn(viewState.codeItems)
        CommandsColumn(viewState, viewModel)
        SourceColumn(viewState.sourceItems)
    }
}

@Composable
private fun RowScope.SourceColumn(sourceItems: List<CommandItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.8f)
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(sourceItems) { item ->
            Text(
                text = item.text,
                modifier = Modifier
                    .dragAndDropSource { _ ->
                        DragAndDropTransferData(
                            ClipData.newPlainText("adding_item", item.text)
                        )
                    }
                    .background(
                        color = Color(0xFF2196F3),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                color = Color.White
            )
        }
    }
}

@Composable
private fun RowScope.CodeColumn(codeItems: List<CodePeace>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1.2f)
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            ),
        contentPadding = PaddingValues(16.dp),
    ) {

        itemsIndexed(codeItems) { index, item ->
            when (item) {
                is CodePeace.IntVariable -> {
                    Column {
                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .width(25.dp)
                                .background(Color.White)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            item.value?.let { value ->
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = value.toString(),
                                    color = Color.Black
                                )
                            }
                        }
                        Box {
                            Image(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(R.drawable.box),
                                contentDescription = "Box ${item.name}"
                            )
                            Surface(
                                modifier = Modifier.align(Alignment.Center),
                                color = Color(0x66000000),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    text = item.name,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFFF9900)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.CommandsColumn(
    viewState: MainViewState,
    viewModel: MainViewModel,
) {
    val isColumnVisualHovered = viewState.isHovered && viewState.commandItems.isEmpty()

    val columnDragAndDropTarget = remember {
        createColumnDragAndDropTarget(
            viewModel = viewModel,
            commandItems = viewState.commandItems,
            draggedCommandItem = viewModel.draggedCommandItem
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .border(
                width = 2.dp,
                color = if (isColumnVisualHovered) Color(0xFF4CAF50)
                else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = if (isColumnVisualHovered) Color(0xFFE8F5E9)
                else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .dragAndDropTextTarget(columnDragAndDropTarget),
        contentPadding = PaddingValues(bottom = 8.dp),
    ) {
        if (viewState.commandItems.isEmpty()) {
            item {
                Text(
                    "Колонка для комманд",
                    color = Color.Gray,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            itemsIndexed(viewState.commandItems, key = { _, item -> item.id }) { index, item ->
                when (item) {
                    is CopyVariableToVariable -> {
                        val topPadding = if (index == 0) 8.dp else 0.dp

                        Box {
                            Column(
                                modifier = Modifier
                                    .padding(top = topPadding)
                                    .fillMaxWidth()
                            ) {
                                if (viewState.itemIndexHovered == index) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        thickness = 2.dp,
                                        color = Red
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                when (item) {
                                    is CopyVariableToVariable -> item.CommandRow(
                                        index = index,
                                        codeItems = viewState.codeItems,
                                        isExecuting = viewState.executingCommandIndex == index,
                                        viewModel = viewModel
                                    )
                                }
                                if (index == viewState.commandItems.lastIndex) {
                                    if ((viewState.itemIndexHovered ?: -1) > viewState.commandItems.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            thickness = 2.dp,
                                            color = Red
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .matchParentSize()
                            ) {
                                val topItemDragAndDropTarget = remember(index, item) {
                                    createTopItemDragAndDropTarget(
                                        index = index,
                                        viewModel = viewModel,
                                        draggedCommandItem = viewModel.draggedCommandItem
                                    )
                                }
                                val botItemDragAndDropTarget = remember(index, item) {
                                    createBotItemDragAndDropTarget(
                                        index = index,
                                        viewModel = viewModel,
                                        commandItems = viewState.commandItems,
                                        draggedCommandItem = viewModel.draggedCommandItem,
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .dragAndDropTextTarget(topItemDragAndDropTarget)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .dragAndDropTextTarget(botItemDragAndDropTarget)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CopyVariableToVariable.CommandRow(
    index: Int,
    codeItems: List<CodePeace>,
    isExecuting: Boolean,
    viewModel: MainViewModel,
) {
    val variables = codeItems.filterIsInstance<CodePeace.IntVariable>().map { it }

    val expanded1 = remember { mutableStateOf(false) }

    val expanded2 = remember { mutableStateOf(false) }
    val backgroundColor = if (isExecuting) Color(0xFFFF9900) else Color(0xFF4CAF50)

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .dragAndDropSource { _ ->
                viewModel.setDraggedCommandItem(viewModel.removeCommand(index))
                DragAndDropTransferData(
                    ClipData.newPlainText("dragged_item", text)
                )
            }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Копировать",
            color = Color.White,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // First dropdown
        Box {
            Text(
                text = source?.first ?: "?",
                color = Color.White,
                modifier = Modifier
                    .background(
                        color = Color(0xFF2E7D32),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable { expanded1.value = true }
            )
            DropdownMenu(
                expanded = expanded1.value,
                onDismissRequest = { expanded1.value = false }
            ) {
                variables.forEach { variable ->
                    DropdownMenuItem(
                        text = { Text(variable.name) },
                        onClick = {
                            viewModel.updateCommand(index, copy(source = variable.name to codeItems.indexOf(variable)))
                            expanded1.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "\u2192",
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.W900,
                fontSize = 22.sp,
            )
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Second dropdown
        Box {
            Text(
                text = target?.first ?: "?",
                color = Color.White,
                modifier = Modifier
                    .background(
                        color = Color(0xFF2E7D32),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable { expanded2.value = true }
            )
            DropdownMenu(
                expanded = expanded2.value,
                onDismissRequest = { expanded2.value = false }
            ) {
                variables.forEach { variable ->
                    DropdownMenuItem(
                        text = { Text(variable.name) },
                        onClick = {
                            viewModel.updateCommand(index, copy(target = variable.name to codeItems.indexOf(variable)))
                            expanded2.value = false
                        }
                    )
                }
            }
        }
    }
}

private fun Modifier.dragAndDropTextTarget(
    target: DragAndDropTarget,
) = dragAndDropTarget(
    shouldStartDragAndDrop = { event ->
        event
            .mimeTypes()
            .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
    },
    target = target
)