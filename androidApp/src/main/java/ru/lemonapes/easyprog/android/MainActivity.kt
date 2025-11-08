package ru.lemonapes.easyprog.android

import android.content.ClipData
import android.content.ClipDescription
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.lemonapes.easyprog.Utils.Companion.log
import kotlin.math.max
import kotlin.math.min

data class ListItem(
    val id: Int,
    val text: String,
    val y: Float = 0f,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    DragDropExample(
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
}

private data class TextItem(val text: String, override val id: Long = Calendar.getInstance().timeInMillis) : ColumnItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragDropExample(
    modifier: Modifier = Modifier,
    isHovered: MutableState<Boolean>,
    itemIndexHovered: MutableState<Int?>,
) {
    val sourceItems = listOf(
        TextItem("Элемент 1"), TextItem("Элемент 2"),
        TextItem("Элемент 3"), TextItem("Элемент 4"),
        TextItem("Элемент 5"), TextItem("Элемент 6"),
        TextItem("Элемент 7"), TextItem("Элемент 8"),
        TextItem("Элемент 9"), TextItem("Элемент 10"),
    )
    val columnItems = remember {
        mutableStateListOf<ColumnItem>(sourceItems.first())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row с исходными элементами
        Text(
            "Row - Горизонтальный список",
            style = MaterialTheme.typography.titleLarge
        )

        Row {

            val state = rememberLazyListState()
            val scope = rememberCoroutineScope()

            TextButton(onClick = {
                scope.launch {
                    state.animateScrollToItem(max(0, state.firstVisibleItemIndex - 2))
                }
            }) {
                Text("Back")
            }

            LazyRow(
                modifier = Modifier.weight(1f),
                state = state,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(sourceItems) { item ->
                    Text(
                        text = item.text,
                        modifier = Modifier
                            .dragAndDropSource { ->
                                detectTapGestures(
                                    onPress = { offset ->
                                        log("startTransfer")
                                        startTransfer(
                                            transferData = DragAndDropTransferData(
                                                clipData = ClipData.newPlainText("item", item.text)
                                            )
                                        )
                                    })
                            }
                            .background(
                                color = Color(0xFF2196F3),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(26.dp),
                        color = Color.White
                    )
                }
            }

            TextButton(onClick = {
                scope.launch {
                    state.animateScrollToItem(min(sourceItems.lastIndex, state.firstVisibleItemIndex + 2))
                }
            }) {
                Text("Forw")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Column для сброса элементов
        Text(
            "Column - Вертикальный список",
            style = MaterialTheme.typography.titleLarge
        )
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
                .fillMaxSize()
                .heightIn(min = 200.dp)
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
        ) {
            if (columnItems.isEmpty()) {
                item {
                    Text(
                        "сюда",
                        color = Color.Gray,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                itemsIndexed(columnItems, key = { _, item -> item.id }) { index, item ->
                    when (item) {
                        is TextItem -> {
                            val topPadding = if (index == 0) 16.dp else 8.dp
                            val botPadding = if (index == columnItems.lastIndex) 116.dp else 0.dp

                            val rowDragAndDropTarget = remember(index, item) {
                                createItemsRowDragAndDropTarget(
                                    index = index,
                                    itemIndexHovered = itemIndexHovered,
                                    columnItems = columnItems,
                                )
                            }
                            val spacerDragAndDropTarget = remember(index, item) {
                                createSpacerDragAndDropTarget(
                                    index = index,
                                    itemIndexHovered = itemIndexHovered,
                                    columnItems = columnItems,
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = botPadding)
                            ) {
                                val spacerHeight = (if (itemIndexHovered.value == index) 60.dp else 0.dp) + topPadding
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(spacerHeight)
                                        .dragAndDropTextTarget(spacerDragAndDropTarget)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .dragAndDropTextTarget(rowDragAndDropTarget)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .height(100.dp)
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
                                        TextButton(
                                            onClick = {
                                                columnItems.removeAt(index)
                                            }
                                        ) {
                                            Text("X", color = Color.White)
                                        }
                                    }
                                }
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
            val item = event
                .toAndroidDragEvent()
                .clipData
                ?.getItemAt(0)
                ?.text
                ?.toString()
                ?.let { TextItem(it) }

            if (item != null) columnItems.add(item)
            isHovered.value = false
            itemIndexHovered.value = null
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("Column onEntered")
            itemIndexHovered.value = null
            isHovered.value = true
        }

        override fun onExited(event: DragAndDropEvent) {
            isHovered.value = false
        }
    }
}

private fun createSpacerDragAndDropTarget(
    index: Int,
    itemIndexHovered: MutableState<Int?>,
    columnItems: MutableList<ColumnItem>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            val item = event
                .toAndroidDragEvent()
                .clipData
                ?.getItemAt(0)
                ?.text
                ?.toString()
                ?.let { TextItem(it) }

            if (item != null) columnItems.add(index, item)
            itemIndexHovered.value = null
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("spacer $index onEntered")
        }
    }
}

private fun createItemsRowDragAndDropTarget(
    index: Int,
    itemIndexHovered: MutableState<Int?>,
    columnItems: MutableList<ColumnItem>,
): DragAndDropTarget {
    return object : DragAndDropTarget {
        override fun onDrop(event: DragAndDropEvent): Boolean {
            val item = event
                .toAndroidDragEvent()
                .clipData
                ?.getItemAt(0)
                ?.text
                ?.toString()
                ?.let { TextItem(it) }

            if (item != null) columnItems.add(min(index + 1, columnItems.lastIndex), item)
            itemIndexHovered.value = null
            return true
        }

        override fun onEntered(event: DragAndDropEvent) {
            log("item $index onEntered")
            if (itemIndexHovered.value == index) {
                if (index < columnItems.lastIndex) itemIndexHovered.value = index + 1 else itemIndexHovered.value = null
            } else {
                itemIndexHovered.value = index
            }
        }
    }
}
