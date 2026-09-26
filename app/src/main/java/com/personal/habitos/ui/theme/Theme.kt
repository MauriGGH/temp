package com.personal.habitos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Colores propios del estilo vidrio que Material 3 no cubre. */
@Immutable
data class GlassColors(
    val glass: Color,
    val glassStrong: Color,
    val glassBorder: Color,
    val glassGlow: Color,
    val divider: Color,
    val textMuted: Color,
    val fondo: Color,
    val fondoAlt: Color,
    val isDark: Boolean
)

private val glassClaro = GlassColors(
    glass = Palette.LightGlass,
    glassStrong = Palette.LightGlassStrong,
    glassBorder = Palette.LightGlassBorder,
    glassGlow = Palette.LightGlassGlow,
    divider = Palette.LightDivider,
    textMuted = Palette.LightTextMuted,
    fondo = Palette.LightBackground,
    fondoAlt = Palette.LightBackgroundAlt,
    isDark = false
)

private val glassOscuro = GlassColors(
    glass = Palette.DarkGlass,
    glassStrong = Palette.DarkGlassStrong,
    glassBorder = Palette.DarkGlassBorder,
    glassGlow = Palette.DarkGlassGlow,
    divider = Palette.DarkDivider,
    textMuted = Palette.DarkTextMuted,
    fondo = Palette.DarkBackground,
    fondoAlt = Palette.DarkBackgroundAlt,
    isDark = true
)

val LocalGlassColors = staticCompositionLocalOf { glassClaro }

@Composable
fun HabitosTheme(
    accent: AccentOption = AccentOption.NaranjaQuemado,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = accent.color,
            onPrimary = Color.White,
            background = Palette.DarkBackground,
            onBackground = Palette.DarkText,
            surface = Palette.DarkBackground,
            onSurface = Palette.DarkText
        )
    } else {
        lightColorScheme(
            primary = accent.color,
            onPrimary = Color.White,
            background = Palette.LightBackground,
            onBackground = Palette.LightText,
            surface = Palette.LightBackground,
            onSurface = Palette.LightText
        )
    }

    val glass = if (darkTheme) glassOscuro else glassClaro

    CompositionLocalProvider(LocalGlassColors provides glass) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
