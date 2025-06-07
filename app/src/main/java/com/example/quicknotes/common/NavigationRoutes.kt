package com.example.quicknotes.common

sealed class NavigationRoutes(val route: String) {
    data object NoteListScreen : NavigationRoutes("notes_list")
    data object AddNoteScreen : NavigationRoutes("add_note")
}