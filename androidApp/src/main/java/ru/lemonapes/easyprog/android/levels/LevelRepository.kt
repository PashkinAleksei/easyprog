package ru.lemonapes.easyprog.android.levels

import kotlinx.collections.immutable.persistentListOf
import ru.lemonapes.easyprog.android.CodePeace
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand

/**
 * Справочник всех уровней игры.
 */
object LevelRepository {

    private val levels: Map<Int, LevelConfig> = buildMap {
        // Уровень 1: Простое копирование
        // Скопировать значение из первой переменной во вторую
        put(
            1, LevelConfig(
                id = 1,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 5, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = null, colorIndex = 1),
                ),
                availableCommands = persistentListOf(
                    CopyValueCommand(),
                ),
                victoryCondition = VictoryCondition.VariableEquals(index = 1, expectedValue = 5),
            )
        )

        // Уровень 2: Простое перемещение
        // Переместить значение из первой переменной во вторую
        put(
            2, LevelConfig(
                id = 2,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 10, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = null, colorIndex = 1),
                ),
                availableCommands = persistentListOf(
                    MoveValueCommand(),
                ),
                victoryCondition = VictoryCondition.All(
                    VictoryCondition.VariableEquals(index = 1, expectedValue = 10),
                    VictoryCondition.VariableIsNull(index = 0),
                ),
            )
        )

        // Уровень 3: Обмен значениями (swap)
        // Поменять местами значения двух переменных
        put(
            3, LevelConfig(
                id = 3,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 5, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = 10, colorIndex = 1),
                    CodePeace.IntVariable(id = 3, value = null, colorIndex = 2),
                ),
                availableCommands = persistentListOf(
                    MoveValueCommand(),
                    CopyValueCommand(),
                ),
                victoryCondition = VictoryCondition.All(
                    VictoryCondition.VariableEquals(index = 0, expectedValue = 10),
                    VictoryCondition.VariableEquals(index = 1, expectedValue = 5),
                ),
            )
        )

        // Уровень 4: Копирование в несколько переменных
        // Скопировать одно значение в две пустые переменные
        put(
            4, LevelConfig(
                id = 4,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 7, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = null, colorIndex = 1),
                    CodePeace.IntVariable(id = 3, value = null, colorIndex = 2),
                ),
                availableCommands = persistentListOf(
                    CopyValueCommand(),
                ),
                victoryCondition = VictoryCondition.All(
                    VictoryCondition.VariableEquals(index = 1, expectedValue = 7),
                    VictoryCondition.VariableEquals(index = 2, expectedValue = 7),
                ),
            )
        )

        // Уровень 5: Сортировка двух переменных
        // Упорядочить значения по возрастанию
        put(
            5, LevelConfig(
                id = 5,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 8, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = 3, colorIndex = 1),
                    CodePeace.IntVariable(id = 3, value = null, colorIndex = 2),
                ),
                availableCommands = persistentListOf(
                    MoveValueCommand(),
                    CopyValueCommand(),
                ),
                victoryCondition = VictoryCondition.VariablesSortedAscending(0, 1),
            )
        )

        // Уровень 6: Сохранить значение и обнулить источник
        put(
            6, LevelConfig(
                id = 6,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 15, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = 20, colorIndex = 1),
                ),
                availableCommands = persistentListOf(
                    MoveValueCommand(),
                ),
                victoryCondition = VictoryCondition.All(
                    VictoryCondition.VariableEquals(index = 0, expectedValue = 20),
                    VictoryCondition.VariableIsNull(index = 1),
                ),
            )
        )

        // Уровень 7: Три переменные - циклический сдвиг
        put(
            7, LevelConfig(
                id = 7,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 1, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = 2, colorIndex = 1),
                    CodePeace.IntVariable(id = 3, value = 3, colorIndex = 2),
                    CodePeace.IntVariable(id = 4, value = null, colorIndex = 3),
                ),
                availableCommands = persistentListOf(
                    MoveValueCommand(),
                    CopyValueCommand(),
                ),
                victoryCondition = VictoryCondition.All(
                    VictoryCondition.VariableEquals(index = 0, expectedValue = 2),
                    VictoryCondition.VariableEquals(index = 1, expectedValue = 3),
                    VictoryCondition.VariableEquals(index = 2, expectedValue = 1),
                ),
            )
        )

        // Уровень 8: Заполнить все переменные одинаковым значением
        put(
            8, LevelConfig(
                id = 8,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 42, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = null, colorIndex = 1),
                    CodePeace.IntVariable(id = 3, value = null, colorIndex = 2),
                    CodePeace.IntVariable(id = 4, value = null, colorIndex = 3),
                ),
                availableCommands = persistentListOf(
                    CopyValueCommand(),
                ),
                victoryCondition = VictoryCondition.All(
                    VictoryCondition.VariableEquals(index = 0, expectedValue = 42),
                    VictoryCondition.VariableEquals(index = 1, expectedValue = 42),
                    VictoryCondition.VariableEquals(index = 2, expectedValue = 42),
                    VictoryCondition.VariableEquals(index = 3, expectedValue = 42),
                ),
            )
        )

        // Уровень 9: Переместить все значения на одну позицию вправо
        put(
            9, LevelConfig(
                id = 9,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 10, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = 20, colorIndex = 1),
                    CodePeace.IntVariable(id = 3, value = 30, colorIndex = 2),
                    CodePeace.IntVariable(id = 4, value = null, colorIndex = 3),
                ),
                availableCommands = persistentListOf(
                    MoveValueCommand(),
                ),
                victoryCondition = VictoryCondition.All(
                    VictoryCondition.VariableIsNull(index = 0),
                    VictoryCondition.VariableEquals(index = 1, expectedValue = 10),
                    VictoryCondition.VariableEquals(index = 2, expectedValue = 20),
                    VictoryCondition.VariableEquals(index = 3, expectedValue = 30),
                ),
            )
        )

        // Уровень 10: Сортировка трех переменных по возрастанию
        put(
            10, LevelConfig(
                id = 10,
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 30, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = 10, colorIndex = 1),
                    CodePeace.IntVariable(id = 3, value = 20, colorIndex = 2),
                    CodePeace.IntVariable(id = 4, value = null, colorIndex = 3),
                    CodePeace.IntVariable(id = 5, value = null, colorIndex = 4),
                ),
                availableCommands = persistentListOf(
                    MoveValueCommand(),
                    CopyValueCommand(),
                ),
                victoryCondition = VictoryCondition.VariablesSortedAscending(0, 1, 2),
            )
        )
    }

    fun getLevel(levelId: Int): LevelConfig? = levels[levelId]

    fun getAllLevels(): List<LevelConfig> = levels.values.toList().sortedBy { it.id }

    fun getLevelCount(): Int = levels.size

    fun hasNextLevel(currentLevelId: Int): Boolean = levels.containsKey(currentLevelId + 1)
}