package com.example.quicknotes.db

import androidx.room.Database

@Database(entities = [NotesModel::class], version = 1)
abstract class NotesDataBase {
    abstract val notesDao: NotesDao


}