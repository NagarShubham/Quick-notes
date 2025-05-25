package com.example.quicknotes

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quicknotes.ui.screens.NotesListScreen
import com.example.quicknotes.db.Note
import com.example.quicknotes.ui.NotesViewModel
import com.example.quicknotes.ui.screens.AddNoteScreen
import com.example.quicknotes.ui.theme.DarkColorScheme
import com.example.quicknotes.ui.theme.LightColorScheme
import com.example.quicknotes.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainContent()
        }
    }
}

@Composable
private fun MainContent() {
    val systemInDarkTheme = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }

    // Theme transition animation
    val animatedColorScheme by animateColorSchemeAsState(isDarkTheme)

    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = Typography,
        content = {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "notes_list",
            ) {
                composable(
                    route = "notes_list",
                ) {
                    val viewModel = hiltViewModel<NotesViewModel>()
                    val notes by viewModel.notes.collectAsState()

                    NotesListScreen(
                        notes = notes,
                        onAddNoteClick = { navController.navigate("add_note") },
                        onDeleteNote = { viewModel.deleteNote(it) },
                        onEditNote = { note ->
                            val encodedTitle = Uri.encode(note.title)
                            val encodedContent = Uri.encode(note.content)
                            navController.navigate(
                                "add_note?noteId=${note.id}&title=$encodedTitle&content=$encodedContent&timestamp=${note.timestamp}",
                            )
                        },
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme },
                    )
                }

                composable(
                    route = "add_note?noteId={noteId}&title={title}&content={content}&timestamp={timestamp}",
                    arguments = listOf(
                        navArgument("noteId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("title") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("content") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("timestamp") {
                            type = NavType.LongType
                            defaultValue = -1L
                        },
                    ),
                    enterTransition = {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutCubic,
                            ),
                        ) + scaleIn(
                            initialScale = 0.92f,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutCubic,
                            ),
                        )
                    },
                    exitTransition = {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutCubic,
                            ),
                        ) + scaleOut(
                            targetScale = 0.92f,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutCubic,
                            ),
                        )
                    },
                    popEnterTransition = {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutCubic,
                            ),
                        ) + scaleIn(
                            initialScale = 0.92f,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutCubic,
                            ),
                        )
                    },
                    popExitTransition = {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutCubic,
                            ),
                        ) + scaleOut(
                            targetScale = 0.92f,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutCubic,
                            ),
                        )
                    },
                ) { backStackEntry ->
                    val viewModel = hiltViewModel<NotesViewModel>()
                    val noteId = backStackEntry.arguments?.getString("noteId")
                    val title = backStackEntry.arguments?.getString("title")?.let { Uri.decode(it) }
                    val content =
                        backStackEntry.arguments?.getString("content")?.let { Uri.decode(it) }
                    val timestamp = backStackEntry.arguments?.getLong("timestamp") ?: -1L

                    val existingNote =
                        if (noteId != null && title != null && content != null && timestamp != -1L) {
                            Note(
                                id = noteId,
                                title = title,
                                content = content,
                                timestamp = timestamp,
                            )
                        } else {
                            null
                        }

                    AddNoteScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNoteAdded = { title, content ->
                            viewModel.addNote(title, content)
                        },
                        onNoteUpdated = { note, title, content ->
                            viewModel.updateNote(note, title, content)
                        },
                        onNoteDeleted = { note ->
                            viewModel.deleteNote(note)
                        },
                        existingNote = existingNote,
                    )
                }
            }
        },
    )
}

@Composable
private fun animateColorSchemeAsState(
    isDarkTheme: Boolean,
): State<ColorScheme> {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    return animateColorScheme(
        targetColorScheme = colorScheme,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow,
        ),
    )
}

@Composable
private fun animateColorScheme(
    targetColorScheme: ColorScheme,
    animationSpec: AnimationSpec<Color> = spring(),
): State<ColorScheme> {
    val primary by animateColorAsState(
        targetValue = targetColorScheme.primary,
        animationSpec = animationSpec,
        label = "primary",
    )
    val onPrimary by animateColorAsState(
        targetValue = targetColorScheme.onPrimary,
        animationSpec = animationSpec,
        label = "onPrimary",
    )
    val primaryContainer by animateColorAsState(
        targetValue = targetColorScheme.primaryContainer,
        animationSpec = animationSpec,
        label = "primaryContainer",
    )
    val onPrimaryContainer by animateColorAsState(
        targetValue = targetColorScheme.onPrimaryContainer,
        animationSpec = animationSpec,
        label = "onPrimaryContainer",
    )
    val secondary by animateColorAsState(
        targetValue = targetColorScheme.secondary,
        animationSpec = animationSpec,
        label = "secondary",
    )
    val onSecondary by animateColorAsState(
        targetValue = targetColorScheme.onSecondary,
        animationSpec = animationSpec,
        label = "onSecondary",
    )
    val secondaryContainer by animateColorAsState(
        targetValue = targetColorScheme.secondaryContainer,
        animationSpec = animationSpec,
        label = "secondaryContainer",
    )
    val onSecondaryContainer by animateColorAsState(
        targetValue = targetColorScheme.onSecondaryContainer,
        animationSpec = animationSpec,
        label = "onSecondaryContainer",
    )
    val tertiary by animateColorAsState(
        targetValue = targetColorScheme.tertiary,
        animationSpec = animationSpec,
        label = "tertiary",
    )
    val onTertiary by animateColorAsState(
        targetValue = targetColorScheme.onTertiary,
        animationSpec = animationSpec,
        label = "onTertiary",
    )
    val tertiaryContainer by animateColorAsState(
        targetValue = targetColorScheme.tertiaryContainer,
        animationSpec = animationSpec,
        label = "tertiaryContainer",
    )
    val onTertiaryContainer by animateColorAsState(
        targetValue = targetColorScheme.onTertiaryContainer,
        animationSpec = animationSpec,
        label = "onTertiaryContainer",
    )
    val surface by animateColorAsState(
        targetValue = targetColorScheme.surface,
        animationSpec = animationSpec,
        label = "surface",
    )
    val onSurface by animateColorAsState(
        targetValue = targetColorScheme.onSurface,
        animationSpec = animationSpec,
        label = "onSurface",
    )
    val surfaceTint by animateColorAsState(
        targetValue = targetColorScheme.surfaceTint,
        animationSpec = animationSpec,
        label = "surfaceTint",
    )
    val inverseSurface by animateColorAsState(
        targetValue = targetColorScheme.inverseSurface,
        animationSpec = animationSpec,
        label = "inverseSurface",
    )
    val inverseOnSurface by animateColorAsState(
        targetValue = targetColorScheme.inverseOnSurface,
        animationSpec = animationSpec,
        label = "inverseOnSurface",
    )
    val background by animateColorAsState(
        targetValue = targetColorScheme.background,
        animationSpec = animationSpec,
        label = "background",
    )
    val onBackground by animateColorAsState(
        targetValue = targetColorScheme.onBackground,
        animationSpec = animationSpec,
        label = "onBackground",
    )
    val surfaceVariant by animateColorAsState(
        targetValue = targetColorScheme.surfaceVariant,
        animationSpec = animationSpec,
        label = "surfaceVariant",
    )
    val onSurfaceVariant by animateColorAsState(
        targetValue = targetColorScheme.onSurfaceVariant,
        animationSpec = animationSpec,
        label = "onSurfaceVariant",
    )
    val error by animateColorAsState(
        targetValue = targetColorScheme.error,
        animationSpec = animationSpec,
        label = "error",
    )
    val onError by animateColorAsState(
        targetValue = targetColorScheme.onError,
        animationSpec = animationSpec,
        label = "onError",
    )
    val errorContainer by animateColorAsState(
        targetValue = targetColorScheme.errorContainer,
        animationSpec = animationSpec,
        label = "errorContainer",
    )
    val onErrorContainer by animateColorAsState(
        targetValue = targetColorScheme.onErrorContainer,
        animationSpec = animationSpec,
        label = "onErrorContainer",
    )
    val outline by animateColorAsState(
        targetValue = targetColorScheme.outline,
        animationSpec = animationSpec,
        label = "outline",
    )
    val outlineVariant by animateColorAsState(
        targetValue = targetColorScheme.outlineVariant,
        animationSpec = animationSpec,
        label = "outlineVariant",
    )
    val scrim by animateColorAsState(
        targetValue = targetColorScheme.scrim,
        animationSpec = animationSpec,
        label = "scrim",
    )
    val inversePrimary by animateColorAsState(
        targetValue = targetColorScheme.inversePrimary,
        animationSpec = animationSpec,
        label = "inversePrimary",
    )

    return remember(
        primary, onPrimary, primaryContainer, onPrimaryContainer,
        secondary, onSecondary, secondaryContainer, onSecondaryContainer,
        tertiary, onTertiary, tertiaryContainer, onTertiaryContainer,
        surface, onSurface, surfaceTint, inverseSurface, inverseOnSurface,
        background, onBackground, surfaceVariant, onSurfaceVariant,
        error, onError, errorContainer, onErrorContainer,
        outline, outlineVariant, scrim, inversePrimary,
    ) {
        mutableStateOf(
            ColorScheme(
                primary = primary,
                onPrimary = onPrimary,
                primaryContainer = primaryContainer,
                onPrimaryContainer = onPrimaryContainer,
                inversePrimary = inversePrimary,
                secondary = secondary,
                onSecondary = onSecondary,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                tertiary = tertiary,
                onTertiary = onTertiary,
                tertiaryContainer = tertiaryContainer,
                onTertiaryContainer = onTertiaryContainer,
                background = background,
                onBackground = onBackground,
                surface = surface,
                onSurface = onSurface,
                surfaceVariant = surfaceVariant,
                onSurfaceVariant = onSurfaceVariant,
                surfaceTint = surfaceTint,
                inverseSurface = inverseSurface,
                inverseOnSurface = inverseOnSurface,
                error = error,
                onError = onError,
                errorContainer = errorContainer,
                onErrorContainer = onErrorContainer,
                outline = outline,
                outlineVariant = outlineVariant,
                scrim = scrim,
            ),
        )
    }
}
