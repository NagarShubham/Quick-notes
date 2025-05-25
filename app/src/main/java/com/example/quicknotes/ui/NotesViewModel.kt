package com.example.quicknotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.db.Note
import com.example.quicknotes.db.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NotesRepository,
) : ViewModel() {

    val notes: StateFlow<List<Note>> = repository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            repository.addNote(title, content)
        }
    }

    fun updateNote(note: Note, newTitle: String, newContent: String) {
        viewModelScope.launch {
            repository.updateNote(
                note.copy(
                    title = newTitle,
                    content = newContent,
                    timestamp = System.currentTimeMillis(),
                ),
            )
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun getNoteById(id: String, onResult: (Note?) -> Unit) {
        viewModelScope.launch {
            val note = repository.getNoteById(id)
            onResult(note)
        }
    }
}
