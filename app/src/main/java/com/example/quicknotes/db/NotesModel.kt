package com.example.quicknotes.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QuickNote")
data class NotesModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val updatedAt: Long
)