package ru.lemonapes.easyprog.android.data

import android.icu.util.Calendar
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lemonapes.easyprog.android.commands.CommandItem
import ru.lemonapes.easyprog.android.commands.CopyValueCommand
import ru.lemonapes.easyprog.android.commands.MoveValueCommand

class GameRepository(
    private val levelProgressDao: LevelProgressDao,
    private val savedCommandDao: SavedCommandDao,
) {
    fun getUnlockedLevelFlow(): Flow<Int> {
        return levelProgressDao.getMaxCompletedLevelFlow().map { maxCompleted ->
            (maxCompleted ?: 0) + 1
        }
    }

    suspend fun markLevelCompleted(levelId: Int) {
        levelProgressDao.insertOrUpdate(LevelProgressEntity(levelId = levelId, isCompleted = true))
    }

    suspend fun getSavedCommands(levelId: Int): ImmutableList<CommandItem> {
        val entities = savedCommandDao.getCommandsForLevel(levelId)
        return entities.map { entity ->
            when (entity.commandType) {
                COMMAND_TYPE_COPY -> CopyValueCommand(
                    id = Calendar.getInstance().timeInMillis + entity.orderIndex,
                    source = entity.sourceIndex,
                    target = entity.targetIndex,
                )
                COMMAND_TYPE_MOVE -> MoveValueCommand(
                    id = Calendar.getInstance().timeInMillis + entity.orderIndex,
                    source = entity.sourceIndex,
                    target = entity.targetIndex,
                )
                else -> throw IllegalArgumentException("Unknown command type: ${entity.commandType}")
            }
        }.toImmutableList()
    }

    suspend fun saveCommands(levelId: Int, commands: List<CommandItem>) {
        val entities = commands.mapIndexed { index, command ->
            SavedCommandEntity(
                levelId = levelId,
                orderIndex = index,
                commandType = when (command) {
                    is CopyValueCommand -> COMMAND_TYPE_COPY
                    is MoveValueCommand -> COMMAND_TYPE_MOVE
                },
                sourceIndex = when (command) {
                    is CopyValueCommand -> command.source
                    is MoveValueCommand -> command.source
                },
                targetIndex = when (command) {
                    is CopyValueCommand -> command.target
                    is MoveValueCommand -> command.target
                },
            )
        }
        savedCommandDao.replaceCommandsForLevel(levelId, entities)
    }

    suspend fun clearCommands(levelId: Int) {
        savedCommandDao.deleteCommandsForLevel(levelId)
    }

    companion object {
        private const val COMMAND_TYPE_COPY = "copy"
        private const val COMMAND_TYPE_MOVE = "move"
    }
}