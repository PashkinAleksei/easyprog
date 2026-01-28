package ru.lemonapes.easyprog.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.icu.util.Calendar
import androidx.compose.ui.draganddrop.DragAndDropEvent
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.GotoCommand
import ru.lemonapes.easyprog.android.commands.IncValueCommand
import ru.lemonapes.easyprog.android.commands.JumpIfZeroCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.commands.PairCommand
import ru.lemonapes.easyprog.android.commands.SingleVariableCommand
import ru.lemonapes.easyprog.android.commands.TwoVariableCommand
import ru.lemonapes.easyprog.android.data.GameRepository
import ru.lemonapes.easyprog.android.drag_and_drop_target.label
import ru.lemonapes.easyprog.android.levels.LevelConfig
import ru.lemonapes.easyprog.android.levels.LevelRepository

class GameViewModel : ViewModel(), GameListener {

    private val gameRepository: GameRepository = EasyProgApplication.getInstance().gameRepository

    private var currentLevelConfig: LevelConfig? = null
    private var initialCodeItems: ImmutableList<CodePeace> = persistentListOf()

    private val _viewStateHandler = MutableStateFlow(GameViewState())
    val viewStateHandler: StateFlow<GameViewState> = _viewStateHandler.asStateFlow()

    private var executionJob: Job? = null

    fun loadLevel(levelId: Int) {
        currentLevelConfig = LevelRepository.getLevel(levelId)

        currentLevelConfig?.let { config ->
            initialCodeItems = config.codeItems
            _viewStateHandler.update {
                GameViewState(
                    levelId = levelId,
                    codeItems = config.codeItems,
                    sourceItems = config.availableCommands,
                    levelTitle = config.title,
                    levelDescription = config.description,
                    showLevelInfoDialog = true,
                )
            }

            viewModelScope.launch {
                val savedCommands = gameRepository.getSavedCommands(levelId)
                if (savedCommands.isNotEmpty()) {
                    _viewStateHandler.update { it.copy(commandItems = savedCommands) }
                }
            }
        }
    }

    private fun saveCommandsToDb() {
        viewModelScope.launch {
            gameRepository.saveCommands(
                levelId = _viewStateHandler.value.levelId,
                commands = _viewStateHandler.value.commandItems
            )
        }
    }

    // Реализация GameListener
    override fun onSetDraggedCommandItem(item: CommandItem?) {
        _viewStateHandler.update { it.copy(draggedCommandItem = item) }
    }

    override fun onBotItemDrop(index: Int, event: DragAndDropEvent): Boolean {
        val viewState = _viewStateHandler.value

        // Игнорировать drop во время выполнения команд
        val addingResult = if (!viewState.isCommandExecution) {
            val isNewItem = event.label == "new_item"
            event.toCommandItem(viewState.draggedCommandItem)
                ?.let { command ->
                    onAddCommandAtIndex(
                        index = index + 1,
                        command = command,
                        isNewItem = isNewItem
                    )
                } ?: false
        } else false
        _viewStateHandler.update { viewState ->
            viewState.copy(itemIndexHovered = null, draggedCommandItem = null)
        }
        return addingResult
    }

    override fun onTopItemDrop(index: Int, event: DragAndDropEvent): Boolean {
        val viewState = viewStateHandler.value

        // Игнорировать drop во время выполнения команд
        val addingResult = if (!viewState.isCommandExecution) {
            val isNewItem = event.label == "new_item"
            event.toCommandItem(viewState.draggedCommandItem)?.let { command ->
                onAddCommandAtIndex(
                    index = index,
                    command = command,
                    isNewItem = isNewItem
                )
            } ?: false
        } else false
        _viewStateHandler.update { viewState ->
            viewState.copy(itemIndexHovered = null, draggedCommandItem = null)
        }
        return addingResult
    }

    override fun onGlobalItemDrop(event: DragAndDropEvent): Boolean {
        val viewState = viewStateHandler.value

        val isNewItem = event.label == "new_item"
        // Игнорировать drop во время выполнения команд
        if (!viewState.isCommandExecution) {
            event.toCommandItem(viewState.draggedCommandItem)?.let { command ->
                when (command) {
                    is PairCommand -> if (!isNewItem) removeCommandPair(command)
                    else -> Unit
                }
            }

        } else onSetItemIndexHovered(null)
        return !isNewItem
    }

    override fun onColumnItemDrop(event: DragAndDropEvent): Boolean {
        val viewState = viewStateHandler.value

        // Игнорировать drop во время выполнения команд
        val addingResult = if (!viewState.isCommandExecution) {
            val isNewItem = event.label == "new_item"
            event.toCommandItem(viewState.draggedCommandItem)?.let { command ->
                onAddCommand(command, isNewItem)
            } ?: false
        } else false
        _viewStateHandler.update { viewState ->
            viewState.copy(itemIndexHovered = null, draggedCommandItem = null, isCommandColumnHovered = false)
        }
        return addingResult
    }

    override fun onAddCommand(command: CommandItem, isNewItem: Boolean): Boolean {
        _viewStateHandler.update {
            fun addCommandRaw() = it.copy(commandItems = (it.commandItems + command).toImmutableList())
            // Если добавляется goto команда, создаем пару
            when (command) {
                is PairCommand -> {
                    if (isNewItem) {
                        when (command) {
                            is GotoCommand -> {
                                // Проверяем количество существующих GoTo команд
                                val currentGotoCount = countGotoCommands(it.commandItems)
                                if (currentGotoCount >= Config.MAX_GOTO_COMMANDS) {
                                    // Если лимит достигнут, не добавляем новую команду
                                    return false
                                }

                                val pairId = Calendar.getInstance().timeInMillis
                                val colorIndex = findFirstAvailableColorIndexForGoto(it.commandItems)
                                val startCommand = command.copy(
                                    pairId = pairId,
                                    colorIndex = colorIndex
                                )
                                val targetCommand = GotoCommand(
                                    type = PairCommand.PairType.SECOND,
                                    pairId = pairId,
                                    colorIndex = colorIndex
                                )
                                val newList = (it.commandItems + startCommand + targetCommand).toImmutableList()
                                it.copy(
                                    commandItems = newList,
                                    scrollToIndex = newList.lastIndex
                                )
                            }

                            is JumpIfZeroCommand -> {
                                // Проверяем количество существующих GoTo команд
                                val currentGotoCount = countGotoCommands(it.commandItems)
                                if (currentGotoCount >= Config.MAX_GOTO_COMMANDS) {
                                    // Если лимит достигнут, не добавляем новую команду
                                    return false
                                }

                                val pairId = Calendar.getInstance().timeInMillis
                                val colorIndex = findFirstAvailableJumpIfZeroColorIndex(it.commandItems)
                                val startCommand = command.copy(
                                    pairId = pairId,
                                    colorIndex = colorIndex
                                )
                                val targetCommand = GotoCommand(
                                    type = PairCommand.PairType.SECOND,
                                    pairId = pairId,
                                    colorIndex = colorIndex
                                )
                                val newList = (it.commandItems + startCommand + targetCommand).toImmutableList()
                                it.copy(
                                    commandItems = newList,
                                    scrollToIndex = newList.lastIndex
                                )
                            }
                        }
                    } else {
                        addCommandRaw()
                    }
                }

                is IncValueCommand, is CopyValueCommand, is MoveValueCommand -> addCommandRaw()

            }
        }
        saveCommandsToDb()
        return true
    }

    override fun onAddCommandAtIndex(index: Int, command: CommandItem, isNewItem: Boolean): Boolean {
        _viewStateHandler.update {
            val newList = it.commandItems.toMutableList()

            // Если добавляется goto команда, создаем пару
            if (command is GotoCommand && isNewItem) {
                // Проверяем количество существующих GoTo команд
                val currentGotoCount = countGotoCommands(it.commandItems)
                if (currentGotoCount >= Config.MAX_GOTO_COMMANDS) {
                    // Если лимит достигнут, не добавляем новую команду
                    return false
                }

                val pairId = Calendar.getInstance().timeInMillis
                val colorIndex = findFirstAvailableColorIndexForGoto(it.commandItems)
                newList.add(index, command.copy(pairId = pairId, colorIndex = colorIndex))
                //Важно, чтобы ID у пар был разным
                newList.add(
                    index + 1,
                    GotoCommand(
                        type = PairCommand.PairType.SECOND,
                        pairId = pairId,
                        colorIndex = colorIndex
                    )
                )
                it.copy(
                    commandItems = newList.toImmutableList(),
                    scrollToIndex = index + 1
                )
            } else if (command is JumpIfZeroCommand && isNewItem) {
                // Проверяем количество существующих JumpIfZero команд
                val currentJumpIfZeroCount = countJumpIfZeroCommands(it.commandItems)
                if (currentJumpIfZeroCount >= Config.MAX_GOTO_COMMANDS) {
                    // Если лимит достигнут, не добавляем новую команду
                    return false
                }

                val pairId = Calendar.getInstance().timeInMillis
                val colorIndex = findFirstAvailableJumpIfZeroColorIndex(it.commandItems)
                newList.add(index, command.copy(pairId = pairId, colorIndex = colorIndex))
                //Важно, чтобы ID у пар был разным
                newList.add(
                    index + 1,
                    JumpIfZeroCommand(
                        type = PairCommand.PairType.SECOND,
                        pairId = pairId,
                        colorIndex = colorIndex,
                        target = null // SECOND не имеет переменной
                    )
                )
                it.copy(
                    commandItems = newList.toImmutableList(),
                    scrollToIndex = index + 1
                )
            } else {
                newList.add(index, command)
                it.copy(
                    commandItems = newList.toImmutableList(),
                    scrollToIndex = index
                )
            }
        }
        saveCommandsToDb()
        return true
    }

    override fun onRemoveCommand(index: Int): CommandItem? {
        var removedItem: CommandItem? = null
        _viewStateHandler.update {
            val newList = it.commandItems.toMutableList()
            removedItem = newList.removeAt(index)
            it.copy(commandItems = newList.toImmutableList())
        }
        saveCommandsToDb()
        return removedItem
    }

    fun removeCommandPair(command: PairCommand) {
        viewStateHandler.value.commandItems.forEachIndexed loop@{ index, found ->
            if (found is PairCommand && command.pairId == found.pairId) {
                onRemoveCommand(index)
                return@loop
            }
        }
    }

    override fun onUpdateCommand(index: Int, command: CommandItem) {
        _viewStateHandler.update {
            val newList = it.commandItems.toMutableList()
            newList[index] = command
            it.copy(commandItems = newList.toImmutableList())
        }
        saveCommandsToDb()
    }

    fun resetCodeItems() {
        _viewStateHandler.update {
            it.copy(codeItems = initialCodeItems)
        }
    }

    override fun onClearCommands() {
        _viewStateHandler.update {
            it.copy(commandItems = persistentListOf())
        }
        saveCommandsToDb()
    }

    private fun showVictoryDialog() {
        _viewStateHandler.update { it.copy(showVictoryDialog = true) }
    }

    private fun hideVictoryDialog() {
        _viewStateHandler.update { it.copy(showVictoryDialog = false) }
    }

    private fun showTryAgainDialog() {
        _viewStateHandler.update { it.copy(showTryAgainDialog = true) }
    }

    private fun hideTryAgainDialog() {
        _viewStateHandler.update { it.copy(showTryAgainDialog = false) }
    }

    override fun onShowLevelInfoDialog() {
        _viewStateHandler.update { it.copy(showLevelInfoDialog = true) }
    }

    override fun onHideLevelInfoDialog() {
        _viewStateHandler.update { it.copy(showLevelInfoDialog = false) }
    }

    override fun onShowClearCommandsDialog() {
        _viewStateHandler.update { it.copy(showClearCommandsDialog = true) }
    }

    override fun onHideClearCommandsDialog() {
        _viewStateHandler.update { it.copy(showClearCommandsDialog = false) }
    }

    override fun onSetComandColumnHovered(isHovered: Boolean) {
        _viewStateHandler.update { it.copy(isCommandColumnHovered = isHovered) }
    }

    override fun onSetItemIndexHovered(index: Int?) {
        _viewStateHandler.update { it.copy(itemIndexHovered = index) }
    }

    override fun onSetLastItemHovered() {
        _viewStateHandler.update { it.copy(itemIndexHovered = it.commandItems.lastIndex + 1) }
    }

    private fun setExecutingCommandIndex(index: Int?) {
        _viewStateHandler.update { it.copy(executingCommandIndex = index) }
    }

    override fun onClearScrollToIndex() {
        _viewStateHandler.update { it.copy(scrollToIndex = null) }
    }

    override fun onCycleExecutionSpeed() {
        _viewStateHandler.update { it.copy(executionSpeed = it.executionSpeed.next()) }
    }

    override fun onExecuteCommands() {
        executionJob = viewModelScope.launch {
            if (!validateCommands()) {
                return@launch
            }

            var currentCommandIndex = 0
            val maxIterations = _viewStateHandler.value.commandItems.size * 100 // Защита от бесконечных циклов
            var iterationCount = 0

            while (currentCommandIndex < _viewStateHandler.value.commandItems.size && iterationCount < maxIterations) {
                iterationCount++

                val command = _viewStateHandler.value.commandItems[currentCommandIndex]
                val delayMs = _viewStateHandler.value.executionSpeed.delayMs

                // Команда становится выделенной (выполняется)
                setExecutingCommandIndex(currentCommandIndex)
                delay(delayMs)

                // Команда выполняется и обновляет состояние
                val commandResult =
                    command.execute(
                        codeItems = _viewStateHandler.value.codeItems,
                        commandItems = _viewStateHandler.value.commandItems,
                        currentCommandIndex = currentCommandIndex
                    )
                _viewStateHandler.update { it.copy(codeItems = commandResult.newCodeItems) }
                delay(delayMs)

                // Команда завершена
                setExecutingCommandIndex(null)

                // Проверка условия победы после каждой команды
                if (checkVictory()) {
                    gameRepository.markLevelCompleted(_viewStateHandler.value.levelId)
                    showVictoryDialog()
                    executionJob = null
                    return@launch
                }

                //Если команда последняя, завершаем цикл
                if (_viewStateHandler.value.commandItems.size <= commandResult.nextCommandIndex) break

                // Определяем следующий индекс команды
                currentCommandIndex = commandResult.nextCommandIndex
            }

            showTryAgainDialog()
            executionJob = null
        }
    }

    override fun onAbortExecution() {
        executionJob?.cancel()
        executionJob = null
        setExecutingCommandIndex(null)
        resetCodeItems()
    }

    private fun checkVictory(): Boolean {
        val config = currentLevelConfig ?: return false
        return config.victoryCondition.check(_viewStateHandler.value.codeItems)
    }

    private fun validateCommands(): Boolean {
        return _viewStateHandler.value.commandItems.all { command ->
            command.validate()
        }
    }

    override fun onVictoryReplay() {
        hideVictoryDialog()
        resetCodeItems()
    }

    override fun onVictoryMenu(navigateToMenu: () -> Unit) {
        hideVictoryDialog()
        resetCodeItems()
        navigateToMenu()
    }

    override fun onVictoryNextLevel(navigateToNextLevel: () -> Unit) {
        hideVictoryDialog()
        resetCodeItems()
        navigateToNextLevel()
    }

    override fun onTryAgainReplay() {
        hideTryAgainDialog()
        resetCodeItems()
    }

    override fun onTryAgainMenu(navigateToMenu: () -> Unit) {
        hideTryAgainDialog()
        resetCodeItems()
        navigateToMenu()
    }

    fun getCurrentLevelId(): Int = _viewStateHandler.value.levelId

    override fun hasNextLevel(): Boolean = LevelRepository.hasNextLevel(_viewStateHandler.value.levelId)

    /**
     * Подсчитывает количество уникальных GoTo команд (пар) в списке команд.
     * Каждая пара GoTo команд считается как одна команда.
     */
    private fun countGotoCommands(commands: ImmutableList<CommandItem>): Int {
        val uniquePairIds = mutableSetOf<Long>()
        commands.forEach { command ->
            if (command is GotoCommand) {
                uniquePairIds.add(command.pairId)
            }
        }
        return uniquePairIds.size
    }

    /**
     * Подсчитывает количество уникальных JumpIfZero команд (пар) в списке команд.
     * Каждая пара JumpIfZero команд считается как одна команда.
     */
    private fun countJumpIfZeroCommands(commands: ImmutableList<CommandItem>): Int {
        val uniquePairIds = mutableSetOf<Long>()
        commands.forEach { command ->
            if (command is JumpIfZeroCommand) {
                uniquePairIds.add(command.pairId)
            }
        }
        return uniquePairIds.size
    }

    /**
     * Находит первый незанятый индекс цвета для новой GotoCommand.
     * Возвращает минимальный индекс от 0 до MAX_GOTO_COMMANDS-1, который еще не используется.
     */
    private fun findFirstAvailableColorIndexForGoto(commands: ImmutableList<CommandItem>): Int {
        return findFirstAvailableColorIndex(commands, GotoCommand::class.java)
    }

    /**
     * Находит первый незанятый индекс цвета для новой JumpIfZeroCommand.
     * Возвращает минимальный индекс от 0 до MAX_GOTO_COMMANDS-1, который еще не используется.
     */
    private fun findFirstAvailableJumpIfZeroColorIndex(commands: ImmutableList<CommandItem>): Int {
        return findFirstAvailableColorIndex(commands, JumpIfZeroCommand::class.java)
    }

    /**
     * Базовый метод для поиска первого незанятого индекса цвета для парных команд.
     * Учитывает только команды указанного типа.
     *
     * @param commands список всех команд
     * @param commandType класс команды, цветовые индексы которой нужно учитывать
     * @return минимальный свободный индекс от 0 до MAX_GOTO_COMMANDS-1
     */
    private fun <T : PairCommand> findFirstAvailableColorIndex(
        commands: ImmutableList<CommandItem>,
        commandType: Class<T>,
    ): Int {
        val usedColorIndices = mutableSetOf<Int>()
        commands.forEach { command ->
            if (commandType.isInstance(command)) {
                val pairCommand = commandType.cast(command)
                if (pairCommand != null) {
                    usedColorIndices.add(pairCommand.colorIndex)
                }
            }
        }

        // Находим первый незанятый индекс
        for (i in 0 until Config.MAX_GOTO_COMMANDS) {
            if (i !in usedColorIndices) {
                return i
            }
        }

        // Если все индексы заняты (не должно произойти из-за проверки лимита)
        return 0
    }
}