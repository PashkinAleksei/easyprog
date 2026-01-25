package ru.lemonapes.easyprog.android.preview

import android.icu.util.Calendar
import kotlinx.collections.immutable.persistentListOf
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.GameViewState
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand

object PreviewGameState {
    fun getLevel1() = GameViewState(
        levelId = 1,
        levelTitle = "Введение",
        codeItems = persistentListOf(
            CodePeace.IntVariable(
                id = Calendar.getInstance().timeInMillis,
                value = 5,
                isMutable = false,
                colorIndex = 0
            ),
            CodePeace.IntVariable(
                id = Calendar.getInstance().timeInMillis + 1,
                value = null,
                isMutable = true,
                colorIndex = 1
            )
        ),
        commandItems = persistentListOf(
            CopyValueCommand(id = Calendar.getInstance().timeInMillis + 2)
        ),
        sourceItems = persistentListOf(
            CopyValueCommand(id = Calendar.getInstance().timeInMillis + 3),
            MoveValueCommand(id = Calendar.getInstance().timeInMillis + 4)
        )
    )
}