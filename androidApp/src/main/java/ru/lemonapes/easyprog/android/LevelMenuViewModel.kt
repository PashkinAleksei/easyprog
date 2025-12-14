package ru.lemonapes.easyprog.android

import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.lemonapes.easyprog.android.levels.LevelRepository

data class LevelMenuState(
    val levels: ImmutableList<Int> = (1..LevelRepository.getLevelCount()).toImmutableList(),
    val unlockedLevels: Int = 10,
)

class LevelMenuViewModel : ViewModel() {
    private val _state = MutableStateFlow(LevelMenuState())
    val state: StateFlow<LevelMenuState> = _state.asStateFlow()

    fun isLevelUnlocked(levelNumber: Int): Boolean {
        return levelNumber <= _state.value.unlockedLevels
    }
}