package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Gaonova is strictly light-themed: Clean White, Warm Linen & Restrained Earthy Accents
private val GaonovaLightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = TerracottaContainerLight,
    onPrimaryContainer = TerracottaDark,
    secondary = SaffronGold,
    onSecondary = Color.White,
    secondaryContainer = SaffronGoldContainer,
    onSecondaryContainer = Color(0xFF451A03),
    tertiary = PeacockTurquoise,
    background = RawSilkCream,
    onBackground = TextPrimary,
    surface = RawSilkSurface,
    onSurface = TextPrimary,
    surfaceVariant = RawSilkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = CardStrokeBorder,
    outlineVariant = CardStrokeBorderSubtle
)

@Composable
fun GaonovaTheme(
    darkTheme: Boolean = false, // Strictly false: Gaonova stays in its warm white light theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = true
                    isAppearanceLightNavigationBars = true
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = GaonovaLightColorScheme,
        typography = Typography,
        content = content
    )
}

