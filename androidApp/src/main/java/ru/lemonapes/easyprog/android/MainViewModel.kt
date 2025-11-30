package ru.lemonapes.easyprog.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.commands.CopyVariableToVariable

class MainViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(
        MainViewState(
            codeItems = persistentListOf(
                CodePeace.IntVariable(name = "A", value = 5),
                CodePeace.IntVariable(name = "B", value = 10),
                CodePeace.IntVariable(name = "C", value = null)
            ),
            sourceItems = persistentListOf(
                CopyVariableToVariable()
            ),
        )
    )
    val viewState: StateFlow<MainViewState> = _viewState.asStateFlow()

    private val _draggedCommandItem = MutableStateFlow<CommandItem?>(null)
    val draggedCommandItem: StateFlow<CommandItem?> = _draggedCommandItem.asStateFlow()

    fun setDraggedCommandItem(item: CommandItem?) {
        _draggedCommandItem.value = item
    }

    // Управление командами
    fun addCommand(command: CommandItem) {
        _viewState.update { it.copy(commandItems = (it.commandItems + command).toImmutableList()) }
    }

    fun addCommandAtIndex(index: Int, command: CommandItem) {
        _viewState.update {
            val newList = it.commandItems.toMutableList()
            newList.add(index, command)
            it.copy(commandItems = newList.toImmutableList())
        }
    }

    fun removeCommand(index: Int): CommandItem? {
        var removedItem: CommandItem? = null
        _viewState.update {
            val newList = it.commandItems.toMutableList()
            removedItem = newList.removeAt(index)
            it.copy(commandItems = newList.toImmutableList())
        }
        return removedItem
    }

    fun updateCommand(index: Int, command: CommandItem) {
        _viewState.update {
            val newList = it.commandItems.toMutableList()
            newList[index] = command
            it.copy(commandItems = newList.toImmutableList())
        }
    }

    // Управление кодовыми элементами
    fun updateCodeItem(index: Int, item: CodePeace) {
        _viewState.update {
            val newList = it.codeItems.toMutableList()
            newList[index] = item
            it.copy(codeItems = newList.toImmutableList())
        }
    }

    fun resetCodeItems() {
        _viewState.update {
            it.copy(
                codeItems = persistentListOf(
                    CodePeace.IntVariable(name = "A", value = 5),
                    CodePeace.IntVariable(name = "B", value = 10),
                    CodePeace.IntVariable(name = "C", value = null)
                )
            )
        }
    }

    // Управление диалогами
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

    // Управление hover состоянием
    fun setHovered(isHovered: Boolean) {
        _viewState.update { it.copy(isHovered = isHovered) }
    }

    fun setItemIndexHovered(index: Int?) {
        _viewState.update { it.copy(itemIndexHovered = index) }
    }

    // Управление выполнением команд
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
                val newCodeItems = command(_viewState.value.codeItems).toImmutableList()
                _viewState.update { it.copy(codeItems = newCodeItems) }
                delay(500)

                // Команда завершена
                setExecutingCommandIndex(null)
            }

            // Проверка условия победы
            if (checkVictory()) {
                showVictoryDialog()
            } else {
                showTryAgainDialog()
            }
        }
    }

    private fun checkVictory(): Boolean {
        val codeItems = _viewState.value.codeItems
        val firstVariable = codeItems.getOrNull(0) as? CodePeace.IntVariable
        val secondVariable = codeItems.getOrNull(1) as? CodePeace.IntVariable

        return firstVariable?.value == 10 && secondVariable?.value == 5
    }

    private fun validateCommands(): Boolean {
        return _viewState.value.commandItems.all { command ->
            when (command) {
                is CopyVariableToVariable -> command.source != null && command.target != null
                else -> true
            }
        }
    }

    fun onVictoryDialogDismiss() {
        hideVictoryDialog()
        resetCodeItems()
    }

    fun onTryAgainDialogDismiss() {
        hideTryAgainDialog()
        resetCodeItems()
    }
}