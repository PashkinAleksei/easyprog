package ru.lemonapes.easyprog.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_commands")
data class SavedCommandEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val levelId: Int,
    val orderIndex: Int,
    val commandType: String,
    val sourceIndex: Int?,
    val targetIndex: Int?,
    val colorIndex: Int = 0,
)