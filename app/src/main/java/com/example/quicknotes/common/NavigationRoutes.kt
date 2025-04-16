package com.example.quicknotes.common

sealed class NavigationRoutes(val route: String) {
    data object Home : NavigationRoutes("home_screen")
    data object NoteScreen : NavigationRoutes("note_screen")
}