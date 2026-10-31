package com.example.acaieaqui.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AcaiClaro = lightColorScheme(
    primary = Color(0xFF6B2FA0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEDCFB),
    onPrimaryContainer = Color(0xFF2A0B47),
    secondary = Color(0xFF2E7D4F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3F0DE),
    onSecondaryContainer = Color(0xFF0B2E1A),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1E1A22),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1E1A22)
)

private val AcaiEscuro = darkColorScheme(
    primary = Color(0xFFD8A9F6),
    onPrimary = Color(0xFF3F1263),
    primaryContainer = Color(0xFF55297D),
    onPrimaryContainer = Color(0xFFEEDCFB),
    secondary = Color(0xFF8FD5A9),
    onSecondary = Color(0xFF00391E),
    secondaryContainer = Color(0xFF1B5233),
    onSecondaryContainer = Color(0xFFD3F0DE),
    background = Color(0xFF151218),
    onBackground = Color(0xFFE8E0EB),
    surface = Color(0xFF151218),
    onSurface = Color(0xFFE8E0EB)
)

@Composable
fun AcaiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) AcaiEscuro else AcaiClaro,
        content = content
    )
}
