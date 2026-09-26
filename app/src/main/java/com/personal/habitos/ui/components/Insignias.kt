package com.personal.habitos.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.personal.habitos.ui.theme.RankColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class ParRango(val claro: Color, val oscuro: Color, val lados: Int)

fun coloresRango(nombre: String): ParRango = when (nombre) {
    "Plata" -> ParRango(RankColors.Plata, RankColors.PlataOscuro, 4)
    "Oro" -> ParRango(RankColors.Oro, RankColors.OroOscuro, 5)
    "Platino" -> ParRango(RankColors.Platino, RankColors.PlatinoOscuro, 6)
    "Diamante" -> ParRango(RankColors.Diamante, RankColors.DiamanteOscuro, 7)
    "Ascendente" -> ParRango(RankColors.Ascendente, RankColors.AscendenteOscuro, 8)
    "Inmortal" -> ParRango(RankColors.Inmortal, RankColors.InmortalOscuro, 4)
    else -> ParRango(RankColors.Radiante, RankColors.RadianteOscuro, 4)
}

private fun poligono(centro: Offset, radio: Float, lados: Int, giro: Float = -90f): Path {
    val path = Path()
    for (i in 0 until lados) {
        val angulo = Math.toRadians((giro + i * 360f / lados).toDouble())
        val x = centro.x + radio * cos(angulo).toFloat()
        val y = centro.y + radio * sin(angulo).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun estrella(centro: Offset, radio: Float, puntas: Int, hundido: Float): Path {
    val path = Path()
    val total = puntas * 2
    for (i in 0 until total) {
        val r = if (i % 2 == 0) radio else radio * hundido
        val angulo = (-PI / 2) + i * PI / puntas
        val x = centro.x + r * cos(angulo).toFloat()
        val y = centro.y + r * sin(angulo).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun DrawScope.destellos(centro: Offset, radio: Float, color: Color, cantidad: Int) {
    repeat(cantidad) { i ->
        rotate(i * (360f / cantidad), centro) {
            val punta = estrella(
                Offset(centro.x, centro.y - radio),
                radio * 0.16f,
                4,
                0.28f
            )
            drawPath(punta, color)
        }
    }
}

/**
 * Emblema de rango: base oscura, placa poligonal, gema con facetas
 * y adornos que crecen con el rango.
 */
@Composable
fun InsigniaRango(
    nombre: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 56.dp,
    activo: Boolean = true
) {
    val par = coloresRango(nombre)
    val alta = nombre in listOf("Diamante", "Ascendente", "Inmortal", "Radiante")

    Box(modifier = modifier.size(tamano)) {
        Canvas(modifier = Modifier.size(tamano)) {
            val s = size.minDimension
            val centro = Offset(s / 2f, s / 2f)
            val alfa = if (activo) 1f else 0.38f

            // Halo
            if (activo) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(par.claro.copy(alpha = 0.30f), Color.Transparent),
                        center = centro,
                        radius = s * 0.55f
                    ),
                    radius = s * 0.55f,
                    center = centro
                )
            }

            // Placa base
            drawPath(
                poligono(centro, s * 0.40f, par.lados),
                color = Color(0xFF232733).copy(alpha = alfa)
            )
            drawPath(
                poligono(centro, s * 0.40f, par.lados),
                color = par.claro.copy(alpha = 0.55f * alfa),
                style = Stroke(width = s * 0.035f)
            )

            // Gema
            val gema = when (nombre) {
                "Inmortal" -> estrella(centro, s * 0.30f, 4, 0.34f)
                "Radiante" -> estrella(centro, s * 0.30f, 4, 0.30f)
                "Ascendente" -> estrella(centro, s * 0.30f, 3, 0.45f)
                else -> poligono(centro, s * 0.26f, maxOf(par.lados, 4), -90f)
            }
            drawPath(
                gema,
                brush = Brush.verticalGradient(
                    listOf(par.claro.copy(alpha = alfa), par.oscuro.copy(alpha = alfa)),
                    startY = centro.y - s * 0.3f,
                    endY = centro.y + s * 0.3f
                )
            )

            // Faceta clara
            val faceta = Path().apply {
                moveTo(centro.x, centro.y - s * 0.26f)
                lineTo(centro.x + s * 0.10f, centro.y)
                lineTo(centro.x, centro.y + s * 0.10f)
                lineTo(centro.x - s * 0.10f, centro.y)
                close()
            }
            drawPath(faceta, Color.White.copy(alpha = 0.55f * alfa))

            // Alas laterales para rangos altos
            if (alta) {
                listOf(-1f, 1f).forEach { lado ->
                    val ala = Path().apply {
                        moveTo(centro.x + lado * s * 0.40f, centro.y - s * 0.06f)
                        lineTo(centro.x + lado * s * 0.50f, centro.y)
                        lineTo(centro.x + lado * s * 0.40f, centro.y + s * 0.06f)
                        close()
                    }
                    drawPath(ala, par.claro.copy(alpha = 0.85f * alfa))
                }
            }

            // Destellos: Inmortal y Radiante
            if (nombre == "Inmortal" && activo) {
                destellos(centro, s * 0.44f, par.claro.copy(alpha = 0.9f), 4)
            }
            if (nombre == "Radiante" && activo) {
                destellos(centro, s * 0.46f, Color.White.copy(alpha = 0.9f), 6)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.Transparent, par.claro.copy(alpha = 0.35f)),
                        center = centro,
                        radius = s * 0.52f
                    ),
                    radius = s * 0.52f,
                    center = centro,
                    style = Stroke(width = s * 0.03f)
                )
            }
        }
    }
}
