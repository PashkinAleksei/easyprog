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
        put(
            1, LevelConfig(
                id = 1,
                title = "Копирование",
                description = "Скопируй значение из первой переменной во вторую",
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

        put(
            2, LevelConfig(
                id = 2,
                title = "Перемещение",
                description = "Перемести значение из первой переменной во вторую. Первая переменная должна остаться пустой",
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

        put(
            3, LevelConfig(
                id = 3,
                title = "Обмен",
                description = "Поменяй местами значения двух переменных. Используй третью переменную как временное хранилище",
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

        put(
            4, LevelConfig(
                id = 4,
                title = "Клонирование",
                description = "Скопируй значение первой переменной в обе пустые переменные",
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

        put(
            5, LevelConfig(
                id = 5,
                title = "Сортировка",
                description = "Расположи значения по возрастанию: меньшее значение должно быть в первой переменной",
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

        put(
            6, LevelConfig(
                id = 6,
                title = "Замена",
                description = "Перемести значение из второй переменной в первую. Вторая должна остаться пустой",
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

        put(
            7, LevelConfig(
                id = 7,
                title = "Ротация",
                description = "Сдвинь все значения на одну позицию влево по кругу: 1→2→3→1",
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

        put(
            8, LevelConfig(
                id = 8,
                title = "Заполнение",
                description = "Заполни все переменные одинаковым значением 42",
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

        put(
            9, LevelConfig(
                id = 9,
                title = "Сдвиг",
                description = "Сдвинь все значения на одну позицию вправо. Первая переменная должна остаться пустой",
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

        put(
            10, LevelConfig(
                id = 10,
                title = "Полная сортировка",
                description = "Отсортируй три переменные по возрастанию",
                codeItems = persistentListOf(
                    CodePeace.IntVariable(id = 1, value = 30, colorIndex = 0),
                    CodePeace.IntVariable(id = 2, value = 10, colorIndex = 1),
                    CodePeace.IntVariable(id = 3, value = 20, colorIndex = 2),
                    CodePeace.IntVariable(id = 4, value = null, colorIndex = 3),
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