package ru.lemonapes.easyprog.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.IncValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand
import ru.lemonapes.easyprog.android.data.GameRepository
import ru.lemonapes.easyprog.android.levels.LevelConfig
import ru.lemonapes.easyprog.android.levels.LevelRepository

class GameViewModel : ViewModel() {

    private val gameRepository: GameRepository = EasyProgApplication.getInstance().gameRepository

    private var currentLevelConfig: LevelConfig? = null
    private var initialCodeItems: ImmutableList<CodePeace> = persistentListOf()

    private val _viewState = MutableStateFlow(MainViewState())
    val viewState: StateFlow<MainViewState> = _viewState.asStateFlow()

    fun loadLevel(levelId: Int) {
        currentLevelConfig = LevelRepository.getLevel(levelId)

        currentLevelConfig?.let { config ->
            initialCodeItems = config.codeItems
            _viewState.update {
                MainViewState(
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
    fun addCommand(command: CommandItem) {
        _viewState.update { it.copy(commandItems = (it.commandItems + command).toImmutableList()) }
        saveCommandsToDb()
    }

    fun addCommandAtIndex(index: Int, command: CommandItem) {
        _viewState.update {
            val newList = it.commandItems.toMutableList()
            newList.add(index, command)
            it.copy(commandItems = newList.toImmutableList())
        }
        saveCommandsToDb()
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

    fun setHovered(isHovered: Boolean) {
        _viewState.update { it.copy(isHovered = isHovered && it.commandItems.isEmpty()) }
    }

    fun setItemIndexHovered(index: Int?) {
        _viewState.update { it.copy(itemIndexHovered = index) }
    }

    fun setExecutingCommandIndex(index: Int?) {
        _viewState.update { it.copy(executingCommandIndex = index) }
    }

    fun executeCommands() {
        viewModelScope.launch {
            if (!validateCommands()) {
                return@launch
            }

            _viewState.value.commandItems.forEachIndexed { index, command ->
                // Команда становится выделенной (выполняется)
                setExecutingCommandIndex(index)
                delay(500)

                // Команда выполняется и обновляет состояние
                val newCodeItems = command(_viewState.value.codeItems)
                _viewState.update { it.copy(codeItems = newCodeItems) }
                delay(500)

                // Команда завершена
                setExecutingCommandIndex(null)
            }

            // Проверка условия победы
            if (checkVictory()) {
                gameRepository.markLevelCompleted(_viewState.value.levelId)
                showVictoryDialog()
            } else {
                showTryAgainDialog()
            }
        }
    }

    private fun checkVictory(): Boolean {
        val config = currentLevelConfig ?: return false
        return config.victoryCondition.check(_viewState.value.codeItems)
    }

    private fun validateCommands(): Boolean {
        return _viewState.value.commandItems.all { command ->
            when (command) {
                is CopyValueCommand -> command.source != null && command.target != null
                is MoveValueCommand -> command.source != null && command.target != null
                is IncValueCommand -> command.target != null
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
}