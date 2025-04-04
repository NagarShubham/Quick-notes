package com.example.quicknotes.ui

import com.example.quicknotes.db.NotesModel
import com.example.quicknotes.db.RoomStoreDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppRepo @Inject constructor(private val roomStoreDao: RoomStoreDao) {

    fun getAllNotes(): Flow<List<NotesModel>> {
        return roomStoreDao.getAllNotesList()
    }

    suspend fun insertNote(notesModel: NotesModel): Long {
        return roomStoreDao.insertNote(notesModel)
    }
}