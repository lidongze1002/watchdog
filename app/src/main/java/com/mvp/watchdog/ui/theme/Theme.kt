package com.mvp.watchdog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2A5BDB),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCE4FF),
    onPrimaryContainer = Color(0xFF001452),
    secondary = Color(0xFF00639B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCDE5FF),
    onSecondaryContainer = Color(0xFF001D32),
    tertiary = Color(0xFF7A4FA0),
    background = Color(0xFFF9F9FF),
    onBackground = Color(0xFF1A1B20),
    surface = Color(0xFFF9F9FF),
    onSurface = Color(0xFF1A1B20),
    surfaceVariant = Color(0xFFE2E1EC),
    onSurfaceVariant = Color(0xFF45464F),
    outline = Color(0xFF757780),
    error = Color(0xFFBA1A1A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB4C4FF),
    onPrimary = Color(0xFF00277C),
    primaryContainer = Color(0xFF0B3EAE),
    onPrimaryContainer = Color(0xFFDCE4FF),
    secondary = Color(0xFF93CCFF),
    onSecondary = Color(0xFF003354),
    secondaryContainer = Color(0xFF004A75),
    onSecondaryContainer = Color(0xFFCDE5FF),
    background = Color(0xFF1A1B20),
    onBackground = Color(0xFFE3E1E9),
    surface = Color(0xFF1A1B20),
    onSurface = Color(0xFFE3E1E9),
    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC6C6D0)
)

@Composable
fun WatchdogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
