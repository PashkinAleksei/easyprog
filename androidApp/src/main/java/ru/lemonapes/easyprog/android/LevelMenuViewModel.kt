package ru.lemonapes.easyprog.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.lemonapes.easyprog.android.data.GameRepository
import ru.lemonapes.easyprog.android.levels.LevelRepository

data class LevelMenuState(
    val levels: ImmutableList<Int> = (1..LevelRepository.getLevelCount()).toImmutableList(),
    val unlockedLevels: Int = 1,
)

class LevelMenuViewModel : ViewModel() {

    private val gameRepository: GameRepository = EasyProgApplication.getInstance().gameRepository

    private val _state = MutableStateFlow(LevelMenuState())
    val state: StateFlow<LevelMenuState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.getUnlockedLevelFlow().collect { unlockedLevel ->
                _state.update { it.copy(unlockedLevels = unlockedLevel) }
            }
        }
    }

    fun isLevelUnlocked(levelNumber: Int): Boolean {
        return levelNumber <= _state.value.unlockedLevels
    }
}