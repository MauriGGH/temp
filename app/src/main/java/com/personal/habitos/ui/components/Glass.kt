package com.personal.habitos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.personal.habitos.ui.theme.LocalGlassColors

/**
 * Fondo neutro con un halo muy suave del color de acento.
 * Nada de manchas de colores: el color lo ponen los elementos de arriba.
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glass = LocalGlassColors.current
    val acento = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(glass.fondoAlt, glass.fondo, glass.fondo))
            )
            .drawBehind {
                val w = size.width
                val h = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            acento.copy(alpha = if (glass.isDark) 0.16f else 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.95f, h * 0.06f),
                        radius = w * 0.85f
                    ),
                    radius = w * 0.85f,
                    center = Offset(w * 0.95f, h * 0.06f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            acento.copy(alpha = if (glass.isDark) 0.07f else 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.02f, h * 0.88f),
                        radius = w * 0.75f
                    ),
                    radius = w * 0.75f,
                    center = Offset(w * 0.02f, h * 0.88f)
                )
            }
    ) {
        content()
    }
}

/**
 * Tarjeta de vidrio: fondo translúcido, brillo diagonal y borde luminoso arriba.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    strong: Boolean = false,
    corner: Dp = 26.dp,
    contentPadding: Dp = 18.dp,
    spacing: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val glass = LocalGlassColors.current
    val shape = RoundedCornerShape(corner)

    Box(
        modifier = modifier
            .shadow(
                elevation = if (glass.isDark) 18.dp else 12.dp,
                shape = shape,
                spotColor = Color(0x66000000),
                ambientColor = Color(0x33000000)
            )
            .clip(shape)
            .background(if (strong) glass.glassStrong else glass.glass)
            .background(
                Brush.linearGradient(
                    colors = listOf(glass.glassGlow, Color.Transparent, Color.Transparent),
                    start = Offset.Zero,
                    end = Offset(700f, 900f)
                )
            )
            .border(1.dp, glass.glassBorder, shape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(spacing),
            content = content
        )
    }
}

/** Variante sin padding interno, para listas donde cada fila lleva el suyo. */
@Composable
fun GlassList(
    modifier: Modifier = Modifier,
    corner: Dp = 26.dp,
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

/** Bloque con título arriba, al estilo de los widgets de las referencias. */
@Composable
fun Widget(
    titulo: String,
    modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = titulo.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = LocalGlassColors.current.textMuted,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            if (accion != null) {
                Box(modifier = Modifier.align(Alignment.CenterEnd)) { accion() }
            }
        }
        content()
    }
}
