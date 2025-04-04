package com.example.quicknotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.db.NotesModel
import com.example.quicknotes.ui.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val appRepo: AppRepo) : ViewModel() {

    fun getAllNotesList(): Flow<List<NotesModel>> {
        return appRepo.getAllNotes()
    }

    fun addNote(notesModel: NotesModel) = viewModelScope.launch {
        appRepo.insertNote(notesModel)
    }
}