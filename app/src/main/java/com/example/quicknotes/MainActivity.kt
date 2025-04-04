package com.example.quicknotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quicknotes.common.NavigationRoutes
import com.example.quicknotes.ui.CreateNote
import com.example.quicknotes.ui.HomeScreen
import com.example.quicknotes.ui.theme.QuickNotesTheme
import com.example.quicknotes.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickNotesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { NavigationStack(it) }
            }
        }
    }

    @Composable
    private fun NavigationStack(innerPadding: PaddingValues) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = NavigationRoutes.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(NavigationRoutes.Home.route) { HomeScreen(navController, homeViewModel) }
            composable(NavigationRoutes.AddNote.route) { CreateNote(navController, homeViewModel) }
        }
    }
}