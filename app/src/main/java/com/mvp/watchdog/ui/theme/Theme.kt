package com.mvp.watchdog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEEF0FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    secondary = Color(0xFF6B7280),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF3F4F6),
    onSecondaryContainer = Color(0xFF1F2937),
    tertiary = Color(0xFF0EA5E9),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF5F5F7),
    onBackground = Color(0xFF1C1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1C1E),
    surfaceVariant = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF6E6E73),
    outline = Color(0xFFE5E5EA),
    outlineVariant = Color(0xFFF0F0F2),
    error = Color(0xFFD92D20),
    onError = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA5B4FC),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = Color(0xFFEEF0FF),
    secondary = Color(0xFF9CA3AF),
    onSecondary = Color(0xFF1F2937),
    secondaryContainer = Color(0xFF374151),
    onSecondaryContainer = Color(0xFFF3F4F6),
    background = Color(0xFF111114),
    onBackground = Color(0xFFEDEDF0),
    surface = Color(0xFF1C1C20),
    onSurface = Color(0xFFEDEDF0),
    surfaceVariant = Color(0xFF2A2A2F),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF3A3A40),
    outlineVariant = Color(0xFF2A2A2F),
    error = Color(0xFFFF6B6B)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun WatchdogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = AppShapes,
        typography = AppTypography,
        content = content
    )
}
