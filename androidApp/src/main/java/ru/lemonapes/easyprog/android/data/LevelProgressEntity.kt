package ru.lemonapes.easyprog.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey
    val levelId: Int,
    val isCompleted: Boolean = false,
)