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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

sealed interface ColumnItem {
    val id: Long
    val text: String
}

private data class CopyVariableToVariable(
    override val id: Long = Calendar.getInstance().timeInMillis,
) : ColumnItem {
    override val text
        get() = "Copy value"
    var target: Pair<String, Int>? = null
    var source: Pair<String, Int>? = null
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainRow(
    modifier: Modifier = Modifier,
    isHovered: MutableState<Boolean>,
    itemIndexHovered: MutableState<Int?>,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 50.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CodeColumn()
        TargetColumn(isHovered, itemIndexHovered)
        SourceColumn()
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
private fun RowScope.CodeColumn() {
    val codeItems = remember {
        mutableStateListOf<CodePeace>(
            CodePeace.IntVariable("a", 5),
            CodePeace.IntVariable("b", 10),
            CodePeace.IntVariable("c", null)
        )
    }
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
private fun RowScope.TargetColumn(
    isHovered: MutableState<Boolean>,
    itemIndexHovered: MutableState<Int?>,
) {
    val columnItems = remember {
        mutableStateListOf<ColumnItem>()
    }

    val isColumnVisualHovered = isHovered.value && columnItems.isEmpty()

    val columnDragAndDropTarget = remember {
        createColumnDragAndDropTarget(
            isHovered = isHovered,
            columnItems = columnItems,
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
        if (columnItems.isEmpty()) {
            item {
                Text(
                    "Колонка для комманд",
                    color = Color.Gray,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            itemsIndexed(columnItems, key = { _, item -> item.id }) { index, item ->
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
                                Row(
                                    modifier = Modifier
                                        .dragAndDropSource { _ ->
                                            columnItems.removeAt(index)
                                            DragAndDropTransferData(
                                                ClipData.newPlainText("dragged_item", item.text)
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
                                        text = item.text,
                                        color = Color.White
                                    )
                                }
                                if (index == columnItems.lastIndex) {
                                    if ((itemIndexHovered.value ?: -1) > columnItems.lastIndex) {
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
                                        columnItems = columnItems,
                                    )
                                }
                                val botItemDragAndDropTarget = remember(index, item) {
                                    createBotItemDragAndDropTarget(
                                        index = index,
                                        itemIndexHovered = itemIndexHovered,
                                        columnItems = columnItems,
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
    columnItems: MutableList<ColumnItem>,
    itemIndexHovered: MutableState<Int?>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem()?.let { item -> columnItems.add(item) }
            isHovered.value = false
            itemIndexHovered.value = null
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Column onEntered")
            itemIndexHovered.value = columnItems.lastIndex + 1
            isHovered.value = true
        }
    }
}

private fun createTopItemDragAndDropTarget(
    index: Int,
    itemIndexHovered: MutableState<Int?>,
    columnItems: MutableList<ColumnItem>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem()?.let { item -> columnItems.add(index, item) }
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
    columnItems: MutableList<ColumnItem>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            event.toItem()?.let { item -> columnItems.add(max(index + 1, columnItems.lastIndex), item) }
            itemIndexHovered.value = null
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("item $index onEntered")
            itemIndexHovered.value = index + 1
        }
    }
}

private fun DragAndDropEvent.toItem(): ColumnItem? {
    return toAndroidDragEvent()
        .clipData
        ?.getItemAt(0)
        ?.text
        ?.toString()
        ?.let { CopyVariableToVariable() }
}

