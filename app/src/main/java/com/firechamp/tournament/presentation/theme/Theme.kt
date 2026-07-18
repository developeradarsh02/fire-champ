package com.firechamp.tournament.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark color scheme for Fire Champ app - matches Arise Battle UI
private val FireChampDarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = WhiteText,
    primaryContainer = PurpleDark,
    onPrimaryContainer = WhiteText,

    secondary = PurpleBright,
    onSecondary = WhiteText,
    secondaryContainer = PurpleDeep,
    onSecondaryContainer = WhiteText,

    tertiary = RedAccent,
    onTertiary = WhiteText,

    background = BlackBackground,
    onBackground = WhiteText,

    surface = BlackBackground,
    onSurface = WhiteText,
    surfaceVariant = PurpleDeep,
    onSurfaceVariant = WhiteText,

    error = ErrorRed,
    onError = WhiteText,

    outline = GreyDivider,
    outlineVariant = GreyHint
)

@Composable
fun FireChampTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = FireChampDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
