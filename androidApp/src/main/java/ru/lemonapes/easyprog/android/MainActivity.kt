package ru.lemonapes.easyprog.android

import android.content.ClipData
import android.content.ClipDescription
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.lemonapes.easyprog.Utils.Companion.log
import kotlin.math.max

private var draggedCommandItem: CommandItem? = null

class MainActivity : ComponentActivity() {
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
                val isHovered = remember { mutableStateOf(false) }
                val itemIndexHovered: MutableState<Int?> = remember { mutableStateOf(null) }

                val globalDragAndDropTarget = remember {
                    createGlobalDragAndDropTarget(
                        isHovered = isHovered,
                        itemIndexHovered = itemIndexHovered,
                    )
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .dragAndDropTextTarget(globalDragAndDropTarget),
                ) { paddings ->
                    MainRow(
                        modifier = Modifier.padding(paddings),
                        isHovered = isHovered,
                        itemIndexHovered = itemIndexHovered,
                    )
                }
            }
        }
    }
}

sealed interface CommandItem {
    val id: Long
    val text: String

    operator fun invoke(codeItems: SnapshotStateList<CodePeace>)
}

private data class CopyVariableToVariable(
    override val id: Long = Calendar.getInstance().timeInMillis,
    val target: Pair<String, Int?>? = null,
    val source: Pair<String, Int?>? = null,
) : CommandItem {
    override val text
        get() = "Копировать"

    override fun invoke(codeItems: SnapshotStateList<CodePeace>) {
        source?.second?.let { sourceIndex ->
            target?.second?.let { targetIndex ->
                val sourceItem = codeItems[sourceIndex] as CodePeace.IntVariable
                val targetItem = codeItems.removeAt(targetIndex) as CodePeace.IntVariable
                codeItems.add(targetIndex, targetItem.copy(value = sourceItem.value))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainRow(
    modifier: Modifier = Modifier,
    isHovered: MutableState<Boolean>,
    itemIndexHovered: MutableState<Int?>,
) {
    val codeItems = remember {
        mutableStateListOf<CodePeace>(
            CodePeace.IntVariable(name = "A", value = 5),
            CodePeace.IntVariable(name = "B", value = 10),
            CodePeace.IntVariable(name = "C", value = null)
        )
    }

    val commandItems = remember {
        mutableStateListOf<CommandItem>()
    }

    val showVictoryDialog = remember { mutableStateOf(false) }
    val showTryAgainDialog = remember { mutableStateOf(false) }
    val executingCommandIndex = remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = {
            if (validateCommands(commandItems)) {
                coroutineScope.launch {
                    commandsExecution(
                        codeItems = codeItems,
                        commandItems = commandItems,
                        showVictoryDialog = showVictoryDialog,
                        showTryAgainDialog = showTryAgainDialog,
                        executingCommandIndex = executingCommandIndex,
                    )
                }
            }
        }) {
            Text("Старт")
        }
    }

    if (showVictoryDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showVictoryDialog.value = false
                resetCodeItems(codeItems)
            },
            title = { Text("Поздравляем!") },
            text = { Text("Вы победили!") },
            confirmButton = {
                TextButton(onClick = {
                    showVictoryDialog.value = false
                    resetCodeItems(codeItems)
                }) {
                    Text("OK")
                }
            }
        )
    }

    if (showTryAgainDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showTryAgainDialog.value = false
                resetCodeItems(codeItems)
            },
            title = { Text("Попробуйте еще") },
            text = { Text("Условие победы не выполнено") },
            confirmButton = {
                TextButton(onClick = {
                    showTryAgainDialog.value = false
                    resetCodeItems(codeItems)
                }) {
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
        CodeColumn(codeItems)
        CommandsColumn(isHovered, itemIndexHovered, codeItems, commandItems, executingCommandIndex)
        SourceColumn()
    }
}

private suspend fun commandsExecution(
    codeItems: SnapshotStateList<CodePeace>,
    commandItems: SnapshotStateList<CommandItem>,
    showVictoryDialog: MutableState<Boolean>,
    showTryAgainDialog: MutableState<Boolean>,
    executingCommandIndex: MutableState<Int?>,
) {
    commandItems.forEachIndexed { index, command ->
        // Команда становится красной
        executingCommandIndex.value = index
        delay(500)

        // Команда выполняется
        command.invoke(codeItems)
        delay(500)

        // Команда становится обратно зеленой
        executingCommandIndex.value = null
    }

    if (checkVictory(codeItems)) {
        showVictoryDialog.value = true
    } else {
        showTryAgainDialog.value = true
    }
}

@Composable
private fun RowScope.SourceColumn() {
    val sourceItems = listOf(
        CopyVariableToVariable()
    )

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
private fun RowScope.CodeColumn(codeItems: SnapshotStateList<CodePeace>) {
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
    isHovered: MutableState<Boolean>,
    itemIndexHovered: MutableState<Int?>,
    codeItems: SnapshotStateList<CodePeace>,
    commandItems: SnapshotStateList<CommandItem>,
    executingCommandIndex: MutableState<Int?>,
) {

    val isColumnVisualHovered = isHovered.value && commandItems.isEmpty()

    val columnDragAndDropTarget = remember {
        createColumnDragAndDropTarget(
            isHovered = isHovered,
            commandItems = commandItems,
            itemIndexHovered = itemIndexHovered,
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
        if (commandItems.isEmpty()) {
            item {
                Text(
                    "Колонка для комманд",
                    color = Color.Gray,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            itemsIndexed(commandItems, key = { _, item -> item.id }) { index, item ->
                when (item) {
                    is CopyVariableToVariable -> {
                        val topPadding = if (index == 0) 8.dp else 0.dp

                        Box {
                            Column(
                                modifier = Modifier
                                    .padding(top = topPadding)
                                    .fillMaxWidth()
                            ) {
                                if (itemIndexHovered.value == index) {
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
                                        commandItems = commandItems,
                                        index = index,
                                        codeItems = codeItems,
                                        isExecuting = executingCommandIndex.value == index
                                    )
                                }
                                if (index == commandItems.lastIndex) {
                                    if ((itemIndexHovered.value ?: -1) > commandItems.lastIndex) {
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
                                        itemIndexHovered = itemIndexHovered,
                                        commandItems = commandItems,
                                    )
                                }
                                val botItemDragAndDropTarget = remember(index, item) {
                                    createBotItemDragAndDropTarget(
                                        index = index,
                                        itemIndexHovered = itemIndexHovered,
                                        commandItems = commandItems,
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
    commandItems: SnapshotStateList<CommandItem>,
    index: Int,
    codeItems: SnapshotStateList<CodePeace>,
    isExecuting: Boolean,
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
                draggedCommandItem = commandItems.removeAt(index)
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
                            commandItems.removeAt(index)
                            commandItems.add(index, copy(source = variable.name to codeItems.indexOf(variable)))
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
                            commandItems.removeAt(index)
                            commandItems.add(index, copy(target = variable.name to codeItems.indexOf(variable)))
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

private fun createGlobalDragAndDropTarget(
    isHovered: MutableState<Boolean>,
    itemIndexHovered: MutableState<Int?>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            itemIndexHovered.value = null
            return false
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Global onEntered")
            isHovered.value = false
            itemIndexHovered.value = null
        }
    }
}

private fun createColumnDragAndDropTarget(
    isHovered: MutableState<Boolean>,
    commandItems: MutableList<CommandItem>,
    itemIndexHovered: MutableState<Int?>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem()?.let { item -> commandItems.add(item) }
            isHovered.value = false
            itemIndexHovered.value = null
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Column onEntered")
            itemIndexHovered.value = commandItems.lastIndex + 1
            isHovered.value = true
        }
    }
}

private fun createTopItemDragAndDropTarget(
    index: Int,
    itemIndexHovered: MutableState<Int?>,
    commandItems: MutableList<CommandItem>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem()?.let { item -> commandItems.add(index, item) }
            itemIndexHovered.value = null
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            itemIndexHovered.value = index
        }
    }
}

private fun createBotItemDragAndDropTarget(
    index: Int,
    itemIndexHovered: MutableState<Int?>,
    commandItems: MutableList<CommandItem>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem()?.let { item -> commandItems.add(max(index + 1, commandItems.lastIndex), item) }
            itemIndexHovered.value = null
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("item $index onEntered")
            itemIndexHovered.value = index + 1
        }
    }
}

private fun DragAndDropEvent.toItem(): CommandItem? {
    val label = toAndroidDragEvent()
        .clipData
        ?.description
        ?.label
        ?.toString()

    return when (label) {
        "adding_item" -> CopyVariableToVariable()
        "dragged_item" -> draggedCommandItem
        else -> null
    }
}

private fun checkVictory(codeItems: SnapshotStateList<CodePeace>): Boolean {
    val firstVariable = codeItems[0] as? CodePeace.IntVariable
    val secondVariable = codeItems[1] as? CodePeace.IntVariable

    return firstVariable?.value == 10 && secondVariable?.value == 5
}

private fun validateCommands(commandItems: SnapshotStateList<CommandItem>): Boolean {
    return commandItems.all { command ->
        when (command) {
            is CopyVariableToVariable -> command.source != null && command.target != null
            else -> true
        }
    }
}

private fun resetCodeItems(codeItems: SnapshotStateList<CodePeace>) {
    codeItems.clear()
    codeItems.addAll(
        listOf(
            CodePeace.IntVariable(name = "A", value = 5),
            CodePeace.IntVariable(name = "B", value = 10),
            CodePeace.IntVariable(name = "C", value = null)
        )
    )
}

