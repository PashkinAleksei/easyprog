package ru.lemonapes.easyprog.android

import android.content.ClipData
import android.content.ClipDescription
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import ru.lemonapes.easyprog.Utils.Companion.log
import kotlin.math.max

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
    var target: Pair<String, Int?>? = null,
    var source: Pair<String, Int?>? = null,
) : CommandItem {
    override val text
        get() = "Copy value"

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
            CodePeace.IntVariable(name = "a", value = 5),
            CodePeace.IntVariable(name = "b", value = 10),
            CodePeace.IntVariable(name = "c", value = null)
        )
    }

    val commandItems = remember {
        mutableStateListOf<CommandItem>()
    }

    val showVictoryDialog = remember { mutableStateOf(false) }
    val showTryAgainDialog = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = {
            if (validateCommands(commandItems)) {
                commandsExecution(
                    codeItems = codeItems,
                    commandItems = commandItems,
                    showVictoryDialog = showVictoryDialog,
                    showTryAgainDialog = showTryAgainDialog,
                )
            }
        }) {
            Text("Старт")
        }
    }

    if (showVictoryDialog.value) {
        AlertDialog(
            onDismissRequest = { showVictoryDialog.value = false },
            title = { Text("Поздравляем!") },
            text = { Text("Вы победили!") },
            confirmButton = {
                TextButton(onClick = { showVictoryDialog.value = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showTryAgainDialog.value) {
        AlertDialog(
            onDismissRequest = { showTryAgainDialog.value = false },
            title = { Text("Попробуйте еще") },
            text = { Text("Условие победы не выполнено") },
            confirmButton = {
                TextButton(onClick = { showTryAgainDialog.value = false }) {
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
        CommandsColumn(isHovered, itemIndexHovered, codeItems, commandItems)
        SourceColumn()
    }
}

private fun commandsExecution(
    codeItems: SnapshotStateList<CodePeace>,
    commandItems: SnapshotStateList<CommandItem>,
    showVictoryDialog: MutableState<Boolean>,
    showTryAgainDialog: MutableState<Boolean>,
) {
    commandItems.forEach { command ->
        command.invoke(codeItems)
    }
    if (checkVictory(codeItems)) {
        showVictoryDialog.value = true
    } else {
        showTryAgainDialog.value = true
    }

    // Возвращаем codeItems в начальное состояние
    codeItems.clear()
    codeItems.addAll(
        listOf(
            CodePeace.IntVariable(name = "a", value = 5),
            CodePeace.IntVariable(name = "b", value = 10),
            CodePeace.IntVariable(name = "c", value = null)
        )
    )
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
                            ClipData.newPlainText("dragged_item", item.text)
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
                    val prefix = if (item.isMutable) "var" else "val"
                    Text("$prefix ${item.name} = ${item.value}")
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
                                        codeItems = codeItems
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
) {
    val variables = codeItems.filterIsInstance<CodePeace.IntVariable>().map { it }

    val expanded1 = remember { mutableStateOf(false) }

    val expanded2 = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .dragAndDropSource { _ ->
                commandItems.removeAt(index)
                DragAndDropTransferData(
                    ClipData.newPlainText("dragged_item", text)
                )
            }
            .padding(horizontal = 16.dp)
            .background(
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Copy",
            color = Color.White,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

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
                            source = variable.name to codeItems.indexOf(variable)
                            expanded1.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))


        Text(
            text = "to",
            color = Color.White,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

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
                            target = variable.name to codeItems.indexOf(variable)
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
    return toAndroidDragEvent()
        .clipData
        ?.getItemAt(0)
        ?.text
        ?.toString()
        ?.let { CopyVariableToVariable() }
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

