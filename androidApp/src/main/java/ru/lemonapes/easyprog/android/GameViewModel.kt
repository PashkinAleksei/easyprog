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
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.GotoCommand
import ru.lemonapes.easyprog.android.commands.IncValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.commands.PairCommand
import ru.lemonapes.easyprog.android.data.GameRepository
import ru.lemonapes.easyprog.android.levels.LevelConfig
import ru.lemonapes.easyprog.android.levels.LevelRepository

class GameViewModel : ViewModel() {

    private val gameRepository: GameRepository = EasyProgApplication.getInstance().gameRepository

    private var currentLevelConfig: LevelConfig? = null
    private var initialCodeItems: ImmutableList<CodePeace> = persistentListOf()

    private val _viewState = MutableStateFlow(GameViewState())
    val viewState: StateFlow<GameViewState> = _viewState.asStateFlow()

    private var executionJob: Job? = null

    fun loadLevel(levelId: Int) {
        currentLevelConfig = LevelRepository.getLevel(levelId)

        currentLevelConfig?.let { config ->
            initialCodeItems = config.codeItems
            _viewState.update {
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
                    _viewState.update { it.copy(commandItems = savedCommands) }
                }
            }
        }
    }

    private fun saveCommandsToDb() {
        viewModelScope.launch {
            gameRepository.saveCommands(
                levelId = _viewState.value.levelId,
                commands = _viewState.value.commandItems
            )
        }
    }

    private val _draggedCommandItem = MutableStateFlow<CommandItem?>(null)
    val draggedCommandItem: StateFlow<CommandItem?> = _draggedCommandItem.asStateFlow()

    fun setDraggedCommandItem(item: CommandItem?) {
        _draggedCommandItem.value = item
    }

    // Управление командами
    fun addCommand(command: CommandItem, isNewItem: Boolean): Boolean {
        _viewState.update {
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
                                val colorIndex = findFirstAvailableColorIndex(it.commandItems)
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

    fun addCommandAtIndex(index: Int, command: CommandItem, isNewItem: Boolean): Boolean {
        _viewState.update {
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
                val colorIndex = findFirstAvailableColorIndex(it.commandItems)
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

    fun removeCommand(index: Int): CommandItem? {
        var removedItem: CommandItem? = null
        _viewState.update {
            val newList = it.commandItems.toMutableList()
            removedItem = newList.removeAt(index)
            it.copy(commandItems = newList.toImmutableList())
        }
        saveCommandsToDb()
        return removedItem
    }

    fun removeCommandPair(command: PairCommand) {
        when (command) {
            is GotoCommand -> {
                viewState.value.commandItems.forEachIndexed loop@{ index, found ->
                    if (found is GotoCommand && command.pairId == found.pairId) {
                        removeCommand(index)
                        return@loop
                    }
                }
            }
        }
    }

    fun updateCommand(index: Int, command: CommandItem) {
        _viewState.update {
            val newList = it.commandItems.toMutableList()
            newList[index] = command
            it.copy(commandItems = newList.toImmutableList())
        }
        saveCommandsToDb()
    }

    fun resetCodeItems() {
        _viewState.update {
            it.copy(codeItems = initialCodeItems)
        }
    }

    fun clearCommands() {
        _viewState.update {
            it.copy(commandItems = persistentListOf())
        }
        saveCommandsToDb()
    }

    fun showVictoryDialog() {
        _viewState.update { it.copy(showVictoryDialog = true) }
    }

    fun hideVictoryDialog() {
        _viewState.update { it.copy(showVictoryDialog = false) }
    }

    fun showTryAgainDialog() {
        _viewState.update { it.copy(showTryAgainDialog = true) }
    }

    fun hideTryAgainDialog() {
        _viewState.update { it.copy(showTryAgainDialog = false) }
    }

    fun showLevelInfoDialog() {
        _viewState.update { it.copy(showLevelInfoDialog = true) }
    }

    fun hideLevelInfoDialog() {
        _viewState.update { it.copy(showLevelInfoDialog = false) }
    }

    fun showClearCommandsDialog() {
        _viewState.update { it.copy(showClearCommandsDialog = true) }
    }

    fun hideClearCommandsDialog() {
        _viewState.update { it.copy(showClearCommandsDialog = false) }
    }

    fun setHovered(isHovered: Boolean) {
        _viewState.update { it.copy(isHovered = isHovered && it.commandItems.isEmpty()) }
    }

    fun setItemIndexHovered(index: Int?) {
        _viewState.update { it.copy(itemIndexHovered = index) }
    }

    fun setExecutingCommandIndex(index: Int?) {
        _viewState.update { it.copy(executingCommandIndex = index) }
    }

    fun clearScrollToIndex() {
        _viewState.update { it.copy(scrollToIndex = null) }
    }

    fun cycleExecutionSpeed() {
        _viewState.update { it.copy(executionSpeed = it.executionSpeed.next()) }
    }

    fun executeCommands() {
        executionJob = viewModelScope.launch {
            if (!validateCommands()) {
                return@launch
            }

            var currentCommandIndex = 0
            val maxIterations = _viewState.value.commandItems.size * 100 // Защита от бесконечных циклов
            var iterationCount = 0

            while (currentCommandIndex < _viewState.value.commandItems.size && iterationCount < maxIterations) {
                iterationCount++

                val command = _viewState.value.commandItems[currentCommandIndex]
                val delayMs = _viewState.value.executionSpeed.delayMs

                // Команда становится выделенной (выполняется)
                setExecutingCommandIndex(currentCommandIndex)
                delay(delayMs)

                // Команда выполняется и обновляет состояние
                val commandResult =
                    command.execute(
                        codeItems = _viewState.value.codeItems,
                        commandItems = _viewState.value.commandItems,
                        currentCommandIndex = currentCommandIndex
                    )
                _viewState.update { it.copy(codeItems = commandResult.newCodeItems) }
                delay(delayMs)

                // Команда завершена
                setExecutingCommandIndex(null)

                // Проверка условия победы после каждой команды
                if (checkVictory()) {
                    gameRepository.markLevelCompleted(_viewState.value.levelId)
                    showVictoryDialog()
                    executionJob = null
                    return@launch
                }

                //Если команда последняя, завершаем цикл
                if (_viewState.value.commandItems.size <= commandResult.nextCommandIndex) break

                // Определяем следующий индекс команды
                currentCommandIndex = commandResult.nextCommandIndex
            }

            showTryAgainDialog()
            executionJob = null
        }
    }

    fun abortExecution() {
        executionJob?.cancel()
        executionJob = null
        setExecutingCommandIndex(null)
        resetCodeItems()
    }

    private fun checkVictory(): Boolean {
        val config = currentLevelConfig ?: return false
        return config.victoryCondition.check(_viewState.value.codeItems)
    }

    private fun validateCommands(): Boolean {
        //TODO:перенести в сами классы как функцию
        return _viewState.value.commandItems.all { command ->
            when (command) {
                is CopyValueCommand -> command.source != null && command.target != null
                is MoveValueCommand -> command.source != null && command.target != null
                is IncValueCommand -> command.target != null
                is GotoCommand -> true // Goto команды не требуют параметров
                else -> false
            }
        }
    }

    fun onVictoryReplay() {
        hideVictoryDialog()
        resetCodeItems()
    }

    fun onVictoryMenu(navigateToMenu: () -> Unit) {
        hideVictoryDialog()
        resetCodeItems()
        navigateToMenu()
    }

    fun onVictoryNextLevel(navigateToNextLevel: () -> Unit) {
        hideVictoryDialog()
        resetCodeItems()
        navigateToNextLevel()
    }

    fun onTryAgainReplay() {
        hideTryAgainDialog()
        resetCodeItems()
    }

    fun onTryAgainMenu(navigateToMenu: () -> Unit) {
        hideTryAgainDialog()
        resetCodeItems()
        navigateToMenu()
    }

    fun getCurrentLevelId(): Int = _viewState.value.levelId

    fun hasNextLevel(): Boolean = LevelRepository.hasNextLevel(_viewState.value.levelId)

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
     * Находит первый незанятый индекс цвета для новой GoTo команды.
     * Возвращает минимальный индекс от 0 до MAX_GOTO_COMMANDS-1, который еще не используется.
     */
    private fun findFirstAvailableColorIndex(commands: ImmutableList<CommandItem>): Int {
        val usedColorIndices = mutableSetOf<Int>()
        commands.forEach { command ->
            if (command is GotoCommand) {
                usedColorIndices.add(command.colorIndex)
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