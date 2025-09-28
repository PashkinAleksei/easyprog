package ru.lemonapes.easyprog.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ru.lemonapes.easyprog.Utils.Companion.log
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DragAndDropCanvas()
                }
            }
        }
    }
}

const val itemHeight = 80f
const val itemPadding = 10f

//@OptIn(InternalComposeApi::class)
@Composable
fun DragAndDropCanvas() {
    var items by remember {
        mutableStateOf(
            (1..10).map { ListItem(it, "Item $it") }
        )
    }
    var draggedItem by remember { mutableStateOf<ListItem?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    //val composer = currentComposer
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(items) {
        items = items.mapIndexed { index, item ->
            item.copy(y = index * (itemHeight + itemPadding))
        }
    }


    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val clickedItem = items.find { item ->
                            offset.y >= item.y && offset.y <= item.y + itemHeight
                        }
                        draggedItem = clickedItem
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, _ ->
                        dragOffset += change.position - change.previousPosition
                    },
                    onDragEnd = {
                        draggedItem?.let { dragged ->
                            val targetY = dragged.y + dragOffset.y
                            val targetIndex = (targetY / (itemHeight + itemPadding))
                                .roundToInt()
                                .coerceIn(0, items.size - 1)

                            val currentIndex = items.indexOf(dragged)
                            if (targetIndex != currentIndex) {
                                val newItems = items.toMutableList()
                                newItems.removeAt(currentIndex)
                                newItems.add(targetIndex, dragged)
                                items = newItems.mapIndexed { index, item ->
                                    item.copy(y = index * (itemHeight + itemPadding))
                                }
                            }
                        }
                        draggedItem = null
                        dragOffset = Offset.Zero
                    }
                )
            }
    ) {
        drawDragAndDropList(items, draggedItem, dragOffset, textMeasurer)
    }
}

fun DrawScope.drawDragAndDropList(
    items: List<ListItem>,
    draggedItem: ListItem?,
    dragOffset: Offset,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
) {
    // Рассчитываем смещения для всех элементов
    val itemOffsets = items.mapIndexed { index, item ->
        val isDragged = item == draggedItem
        when {
            isDragged -> {
                // Перетаскиваемый элемент - используем его оригинальную позицию + dragOffset
                item.y + dragOffset.y
            }

            draggedItem != null -> {
                // Не перетаскиваемые элементы - рассчитываем их смещение
                val draggedCurrentY = draggedItem.y + dragOffset.y
                val draggedOriginalIndex = items.indexOf(draggedItem)
                val posFullHeight = itemHeight + itemPadding
                val draggedCurrentFullPosScrolled = (dragOffset.y / posFullHeight).toInt()

                // Определяем куда должен попасть перетаскиваемый элемент
                val targetIndex = (draggedCurrentY / (posFullHeight)).roundToInt()
                    .coerceIn(0, items.size - 1)

                val draggedCurrentIndex = draggedOriginalIndex + draggedCurrentFullPosScrolled

                when {
                    //Перетаскиваемый элемент движется вниз
                    dragOffset.y > 0 &&
                            index > draggedOriginalIndex &&
                            index <= (draggedCurrentIndex + 1) -> {
                        max(
                            item.y - dragOffset.y + posFullHeight * (index - draggedOriginalIndex - 1),
                            item.y - posFullHeight
                        )
                    }

                    //Перетаскиваемый элемент движется вeрх
                    dragOffset.y < 0 &&
                            index < draggedOriginalIndex &&
                            index >= (draggedCurrentIndex - 1) -> {
                        min(
                            item.y - dragOffset.y - posFullHeight * (draggedOriginalIndex - index - 1),
                            item.y + posFullHeight
                        )
                    }

                    else -> item.y
                }
            }

            else -> item.y
        }
    }

    items.forEachIndexed { index, item ->
        val isDragged = item == draggedItem
        val itemY = itemOffsets[index]
        val alpha = if (isDragged) 0.7f else 1f

        drawRect(
            color = if (isDragged) Color.LightGray else Color.White,
            topLeft = Offset(20f, itemY),
            size = Size(size.width - 40f, itemHeight),
            alpha = alpha
        )

        val textResult = textMeasurer.measure(
            text = item.text,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        )

        drawText(
            textLayoutResult = textResult,
            topLeft = Offset(
                40f,
                itemY + (itemHeight - textResult.size.height) / 2
            ),
            alpha = alpha
        )
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        DragAndDropCanvas()
    }
}
