package com.personal.habitos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.personal.habitos.ui.theme.LocalGlassColors

/**
 * Fondo de la app: color base + manchas de color difuminadas.
 * Se dibujan como degradados radiales, así que se ven igual en cualquier
 * versión de Android (Modifier.blur solo existe desde Android 12).
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glass = LocalGlassColors.current
    val base = MaterialTheme.colorScheme.background
    val alpha = if (glass.isDark) 0.75f else 0.95f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(base)
            .drawBehind {
                fun blob(color: Color, center: Offset, radius: Float) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color.copy(alpha = alpha), color.copy(alpha = 0f)),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }

                val w = size.width
                val h = size.height
                blob(glass.blobs[0], Offset(w * 0.05f, h * 0.02f), w * 0.75f)
                blob(glass.blobs[1], Offset(w * 1.05f, h * 0.45f), w * 0.70f)
                blob(glass.blobs[2], Offset(w * -0.05f, h * 0.95f), w * 0.75f)
            }
    ) {
        content()
    }
}

/** Tarjeta translúcida con borde claro, la pieza base de toda la interfaz. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    strong: Boolean = false,
    corner: Dp = 28.dp,
    contentPadding: Dp = 18.dp,
    spacing: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val glass = LocalGlassColors.current
    val shape = RoundedCornerShape(corner)

    Column(
        modifier = modifier
            .clip(shape)
            .background(if (strong) glass.glassStrong else glass.glass)
            .border(1.dp, glass.glassBorder, shape)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}

/** Variante sin padding, para listas donde cada fila lleva el suyo. */
@Composable
fun GlassList(
    modifier: Modifier = Modifier,
    corner: Dp = 28.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(
        modifier = modifier,
        corner = corner,
        contentPadding = 6.dp,
        spacing = 0.dp,
        content = content
    )
}
