package ru.lemonapes.easyprog.android.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress WHERE levelId = :levelId")
    suspend fun getLevelProgress(levelId: Int): LevelProgressEntity?

    @Query("SELECT * FROM level_progress WHERE isCompleted = 1")
    fun getCompletedLevelsFlow(): Flow<List<LevelProgressEntity>>

    @Query("SELECT MAX(levelId) FROM level_progress WHERE isCompleted = 1")
    fun getMaxCompletedLevelFlow(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(levelProgress: LevelProgressEntity)

    @Query("UPDATE level_progress SET isCompleted = 1 WHERE levelId = :levelId")
    suspend fun markLevelCompleted(levelId: Int)
}