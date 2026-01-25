package ru.lemonapes.easyprog.android.preview

import androidx.compose.ui.draganddrop.DragAndDropEvent
import ru.lemonapes.easyprog.android.GameListener
import ru.lemonapes.easyprog.android.commands.CommandItem

/**
 * Фейковая реализация GameListener для использования в Compose Preview.
 * Все методы имеют пустую реализацию, чтобы избежать побочных эффектов
 * (доступ к БД, сети, I/O) во время рендеринга Preview.
 */
class PreviewGameListener : GameListener {
    override fun onAddCommand(command: CommandItem, isNewItem: Boolean): Boolean = true

    override fun onAddCommandAtIndex(index: Int, command: CommandItem, isNewItem: Boolean): Boolean = true

    override fun onRemoveCommand(index: Int): CommandItem? = null

    override fun onUpdateCommand(index: Int, command: CommandItem) {}

    override fun onClearCommands() {}

    override fun onExecuteCommands() {}

    override fun onAbortExecution() {}

    override fun onCycleExecutionSpeed() {}

    override fun onSetDraggedCommandItem(item: CommandItem?) {}

    override fun onBotItemDrop(index: Int, event: DragAndDropEvent): Boolean = false

    override fun onTopItemDrop(index: Int, event: DragAndDropEvent): Boolean = false

    override fun onGlobalItemDrop(event: DragAndDropEvent): Boolean = false

    override fun onColumnItemDrop(event: DragAndDropEvent): Boolean = false

    override fun onSetComandColumnHovered(isHovered: Boolean) {}

    override fun onSetItemIndexHovered(index: Int?) {}

    override fun onSetLastItemHovered() {}

    override fun onClearScrollToIndex() {}

    override fun onShowLevelInfoDialog() {}

    override fun onHideLevelInfoDialog() {}

    override fun onShowClearCommandsDialog() {}

    override fun onHideClearCommandsDialog() {}

    override fun onVictoryReplay() {}

    override fun onVictoryMenu(navigateToMenu: () -> Unit) {}

    override fun onVictoryNextLevel(navigateToNextLevel: () -> Unit) {}

    override fun onTryAgainReplay() {}

    override fun onTryAgainMenu(navigateToMenu: () -> Unit) {}

    override fun hasNextLevel(): Boolean = true
}