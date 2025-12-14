package ru.lemonapes.easyprog.android.levels

import kotlinx.collections.immutable.ImmutableList
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.commands.CommandItem

/**
 * Конфигурация уровня.
 *
 * @param id уникальный идентификатор уровня (номер уровня)
 * @param codeItems начальное состояние переменных
 * @param availableCommands доступные пользователю команды
 * @param victoryCondition условие победы
 */
data class LevelConfig(
    val id: Int,
    val codeItems: ImmutableList<CodePeace>,
    val availableCommands: ImmutableList<CommandItem>,
    val victoryCondition: VictoryCondition,
)