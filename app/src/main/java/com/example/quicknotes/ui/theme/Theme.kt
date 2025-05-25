package com.example.quicknotes.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF86B6F6),
    secondary = Color(0xFF176B87),
    tertiary = Color(0xFFB4D4FF),
    background = Color(0xFF001F3F),
    surface = Color(0xFF002952),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    primaryContainer = Color(0xFF176B87),
    onPrimaryContainer = Color.White,
    surfaceVariant = Color(0xFF1D3B66),
    onSurfaceVariant = Color(0xFFB4D4FF),
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF176B87),
    secondary = Color(0xFF86B6F6),
    tertiary = Color(0xFFB4D4FF),
    background = Color(0xFFF8FBFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF001F3F),
    onSurface = Color(0xFF001F3F),
    primaryContainer = Color(0xFF86B6F6),
    onPrimaryContainer = Color(0xFF001F3F),
    surfaceVariant = Color(0xFFEEF5FF),
    onSurfaceVariant = Color(0xFF176B87),

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MyDemoProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
