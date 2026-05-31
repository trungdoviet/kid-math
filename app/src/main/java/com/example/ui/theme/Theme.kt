package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = ColorGreenPrimary,
    secondary = ColorCreamSurface,
    tertiary = ColorTealProgress,
    background = ColorBg,
    surface = ColorBg,
    onPrimary = Color.White,
    onSecondary = ColorHeading,
    onBackground = ColorText,
    onSurface = ColorText
)

private val LightColorScheme = lightColorScheme(
    primary = ColorGreenPrimary,
    secondary = ColorCreamSurface,
    tertiary = ColorTealProgress,
    background = ColorBg,
    surface = ColorBg,
    onPrimary = Color.White,
    onSecondary = ColorHeading,
    onBackground = ColorText,
    onSurface = ColorText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Standardizing to false so our hand-picked Natural Tones are fully respected
    dynamicColor: Boolean = false,
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
        content = content
    )
}
