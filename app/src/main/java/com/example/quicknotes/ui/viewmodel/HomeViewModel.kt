package com.example.quicknotes.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.db.NotesModel
import com.example.quicknotes.ui.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(private val appRepo: AppRepo) : ViewModel() {
    private var mNoteList = MutableStateFlow<List<NotesModel>>(emptyList())
    val noteList: StateFlow<List<NotesModel>> = mNoteList

    private var selectedNote: NotesModel? = null

    init {
        getAllNotesList()
    }

    private fun getAllNotesList() = viewModelScope.launch(Dispatchers.IO) {
        appRepo.getAllNotes().collect { mNoteList.emit(it) }
    }

    fun addNote(notesModel: NotesModel) = viewModelScope.launch(Dispatchers.IO) {
        Log.e(TAG, "addNote: ${appRepo.insertNote(notesModel)}")
    }

    fun deleteNote(notesModel: NotesModel) = viewModelScope.launch(Dispatchers.IO) {
        Log.e(TAG, "delete: ${appRepo.deleteNote(notesModel)}")
    }

    fun setSelectedNote(notesModel: NotesModel?) {
        selectedNote = notesModel
    }

    fun getSelectedNote(): NotesModel? = selectedNote
}