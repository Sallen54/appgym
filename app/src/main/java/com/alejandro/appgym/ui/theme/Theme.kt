package com.alejandro.appgym.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val AppColorScheme = darkColorScheme(
    primary = Volt,
    onPrimary = OnVolt,
    secondary = GroupBiceps,
    onSecondary = OnVolt,
    background = Ink,
    onBackground = TextPrimary,
    surface = Surface1,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = Surface1,
    surfaceContainerHigh = Surface2,
    outline = Outline,
    outlineVariant = OutlineSoft,
    error = Danger,
    onError = Ink
)

/** Píldora completa: botones, chips, badges y FAB. */
val Pill = RoundedCornerShape(99.dp)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(11.dp),   // steppers
    small = RoundedCornerShape(14.dp),        // inputs, miniaturas
    medium = RoundedCornerShape(18.dp),       // filas internas
    large = RoundedCornerShape(24.dp),        // tarjetas
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AppGymTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
