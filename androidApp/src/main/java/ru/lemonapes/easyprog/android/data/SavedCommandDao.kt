package ru.lemonapes.easyprog.android.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface SavedCommandDao {
    @Query("SELECT * FROM saved_commands WHERE levelId = :levelId ORDER BY orderIndex ASC")
    suspend fun getCommandsForLevel(levelId: Int): List<SavedCommandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(commands: List<SavedCommandEntity>)

    @Query("DELETE FROM saved_commands WHERE levelId = :levelId")
    suspend fun deleteCommandsForLevel(levelId: Int)

    @Transaction
    suspend fun replaceCommandsForLevel(levelId: Int, commands: List<SavedCommandEntity>) {
        deleteCommandsForLevel(levelId)
        insertAll(commands)
    }
}