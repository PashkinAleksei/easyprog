package ru.lemonapes.easyprog.android.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object LevelMenu : Screens()

    @Serializable
    data class Game(val levelId: Int) : Screens()
}