package com.example.quicknotes.ui

import com.example.quicknotes.db.NotesModel
import com.example.quicknotes.db.RoomStoreDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AppRepo @Inject constructor(private val roomStoreDao: RoomStoreDao) {

    fun getAllNotes(): StateFlow<List<NotesModel>> {
        return roomStoreDao.getAllNotesList()
    }

    fun insertNote(notesModel: NotesModel){
        roomStoreDao.insertNote(notesModel)
    }
}