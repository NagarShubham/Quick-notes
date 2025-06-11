package com.example.quicknotes.db

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(
    private val noteDao: NoteDao,
) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun addNote(title: String, content: String) {
        val note = Note(title = title, content = content)
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.insertNote(note) // Using insertNote with REPLACE strategy
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
}
