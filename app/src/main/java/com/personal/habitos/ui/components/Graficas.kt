package com.personal.habitos.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.personal.habitos.ui.theme.LocalGlassColors

/** Anillo de progreso con degradado y punta redonda. */
@Composable
fun AnilloProgreso(
    progreso: Float,
    modifier: Modifier = Modifier,
    tamano: Dp = 88.dp,
    grosor: Dp = 10.dp,
    centro: (@Composable () -> Unit)? = null
) {
    val glass = LocalGlassColors.current
    val acento = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.size(tamano), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(tamano)) {
            val trazo = grosor.toPx()
            val radio = (size.minDimension - trazo) / 2f
            val esquina = Offset(size.width / 2f - radio, size.height / 2f - radio)
            val medida = Size(radio * 2f, radio * 2f)

            drawArc(
                color = glass.divider,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = esquina,
                size = medida,
                style = Stroke(width = trazo, cap = StrokeCap.Round)
            )
            if (progreso > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(acento.copy(alpha = 0.45f), acento, acento.copy(alpha = 0.45f))
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * progreso.coerceIn(0f, 1f),
                    useCenter = false,
                    topLeft = esquina,
                    size = medida,
                    style = Stroke(width = trazo, cap = StrokeCap.Round)
                )
            }
        }
        centro?.invoke()
    }
}

/** Barras de la semana: una por día, con los días sin actividad atenuados. */
@Composable
fun BarrasSemana(
    valores: List<Float>,
    etiquetas: List<String>,
    modifier: Modifier = Modifier,
    alto: Dp = 96.dp
) {
    val glass = LocalGlassColors.current
    val acento = MaterialTheme.colorScheme.primary
    val maximo = (valores.maxOrNull() ?: 1f).coerceAtLeast(1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(alto),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            valores.forEach { valor ->
                val fraccion = (valor / maximo).coerceIn(0f, 1f)
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(alto)
                ) {
                    val ancho = size.width
                    val radio = ancho / 2f
                    drawRoundRect(
                        color = glass.divider,
                        topLeft = Offset(0f, 0f),
                        size = Size(ancho, size.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(radio, radio)
                    )
                    val altoBarra = (size.height * fraccion).coerceAtLeast(if (valor > 0f) ancho else 0f)
                    if (altoBarra > 0f) {
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(acento, acento.copy(alpha = 0.55f))
                            ),
                            topLeft = Offset(0f, size.height - altoBarra),
                            size = Size(ancho, altoBarra),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radio, radio)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            etiquetas.forEach { etiqueta ->
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = glass.textMuted,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/** Punto de un día: lleno, vacío o no aplica. */
@Composable
fun PuntoDia(hecho: Boolean, aplica: Boolean, etiqueta: String, modifier: Modifier = Modifier) {
    val glass = LocalGlassColors.current
    val acento = MaterialTheme.colorScheme.primary

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(22.dp)) {
            val radio = size.minDimension / 2f
            when {
                hecho -> drawCircle(
                    brush = Brush.verticalGradient(listOf(acento, acento.copy(alpha = 0.7f))),
                    radius = radio
                )
                aplica -> drawCircle(
                    color = glass.divider,
                    radius = radio,
                    style = Stroke(width = 2.dp.toPx())
                )
                else -> drawCircle(color = glass.divider, radius = radio * 0.45f)
            }
        }
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = glass.textMuted,
            fontWeight = FontWeight.SemiBold
        )
    }
}
