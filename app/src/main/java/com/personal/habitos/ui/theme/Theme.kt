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
    val divider: Color,
    val textMuted: Color,
    val blobs: List<Color>,
    val isDark: Boolean
)

val LocalGlassColors = staticCompositionLocalOf {
    GlassColors(
        glass = Palette.LightGlass,
        glassStrong = Palette.LightGlassStrong,
        glassBorder = Palette.LightGlassBorder,
        divider = Palette.LightDivider,
        textMuted = Palette.LightTextMuted,
        blobs = listOf(Palette.BlobLavender, Palette.BlobPeach, Palette.BlobBlue),
        isDark = false
    )
}

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

    val glass = if (darkTheme) {
        GlassColors(
            glass = Palette.DarkGlass,
            glassStrong = Palette.DarkGlassStrong,
            glassBorder = Palette.DarkGlassBorder,
            divider = Palette.DarkDivider,
            textMuted = Palette.DarkTextMuted,
            blobs = listOf(Palette.DarkBlobIndigo, Palette.DarkBlobRust, Palette.DarkBlobBlue),
            isDark = true
        )
    } else {
        GlassColors(
            glass = Palette.LightGlass,
            glassStrong = Palette.LightGlassStrong,
            glassBorder = Palette.LightGlassBorder,
            divider = Palette.LightDivider,
            textMuted = Palette.LightTextMuted,
            blobs = listOf(Palette.BlobLavender, Palette.BlobPeach, Palette.BlobBlue),
            isDark = false
        )
    }

    CompositionLocalProvider(LocalGlassColors provides glass) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
