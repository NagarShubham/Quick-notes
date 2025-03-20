package com.example.quicknotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.quicknotes.ui.HomeScreen
import com.example.quicknotes.ui.theme.QuickNotesTheme

class MainActivity : ComponentActivity() {
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
            composable(NavigationRoutes.Home.route) { HomeScreen() }
        }
    }
}