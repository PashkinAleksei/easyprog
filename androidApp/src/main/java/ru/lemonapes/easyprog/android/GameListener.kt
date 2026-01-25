package ru.lemonapes.easyprog.android

import androidx.compose.ui.draganddrop.DragAndDropEvent
import kotlinx.collections.immutable.ImmutableList
import ru.lemonapes.easyprog.android.commands.CommandItem

/**
 * Интерфейс для обработки событий игры.
 * Используется для передачи в Composable-функции вместо ViewModel.
 */
interface GameListener {
    // Управление командами
    fun onAddCommand(command: CommandItem, isNewItem: Boolean): Boolean
    fun onAddCommandAtIndex(index: Int, command: CommandItem, isNewItem: Boolean): Boolean
    fun onRemoveCommand(index: Int): CommandItem?
    fun onUpdateCommand(index: Int, command: CommandItem)
    fun onClearCommands()

    // Выполнение команд
    fun onExecuteCommands()
    fun onAbortExecution()
    fun onCycleExecutionSpeed()

    // Drag and drop
    fun onSetDraggedCommandItem(item: CommandItem?)
    fun onBotItemDrop(index:Int, event: DragAndDropEvent): Boolean
    fun onTopItemDrop(index:Int, event: DragAndDropEvent): Boolean
    fun onGlobalItemDrop(event: DragAndDropEvent): Boolean
    fun onColumnItemDrop(event: DragAndDropEvent): Boolean

    // UI состояния
    fun onSetComandColumnHovered(isHovered: Boolean)
    fun onSetItemIndexHovered(index: Int?)
    fun onSetLastItemHovered()
    fun onClearScrollToIndex()

    // Диалоги
    fun onShowLevelInfoDialog()
    fun onHideLevelInfoDialog()
    fun onShowClearCommandsDialog()
    fun onHideClearCommandsDialog()

    // Победа
    fun onVictoryReplay()
    fun onVictoryMenu(navigateToMenu: () -> Unit)
    fun onVictoryNextLevel(navigateToNextLevel: () -> Unit)

    // Попробовать снова
    fun onTryAgainReplay()
    fun onTryAgainMenu(navigateToMenu: () -> Unit)

    // Вспомогательные функции
    fun hasNextLevel(): Boolean
}