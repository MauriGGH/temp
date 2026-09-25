package com.personal.habitos.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.rotate
import com.personal.habitos.data.Recompensa
import com.personal.habitos.data.Repo
import com.personal.habitos.data.Reto
import com.personal.habitos.data.nivelDe
import com.personal.habitos.data.progresoRango
import com.personal.habitos.data.rangoDe
import com.personal.habitos.data.rangos
import com.personal.habitos.data.siguienteRango
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.theme.LocalGlassColors
import com.personal.habitos.ui.theme.RankColors

private fun colorRango(nombre: String): Color = when (nombre) {
    "Plata" -> RankColors.Plata
    "Oro" -> RankColors.Oro
    "Platino" -> RankColors.Platino
    "Diamante" -> RankColors.Diamante
    "Ascendente" -> RankColors.Ascendente
    "Inmortal" -> RankColors.Inmortal
    else -> RankColors.Radiante
}

@Composable
fun RetosScreen(contentPadding: PaddingValues, onVolver: () -> Unit) {
    val estado = Repo.estado
    val rango = rangoDe(estado.puntos)
    val siguiente = siguienteRango(estado.puntos)
    var creandoReto by remember { mutableStateOf(false) }
    var creandoRecompensa by remember { mutableStateOf(false) }

    Pantalla(
        titulo = "Retos",
        contentPadding = contentPadding,
        subtitulo = "Metas de proceso. Los puntos solo suben: nunca bajas de rango.",
        onVolver = onVolver
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Mascota(color = MaterialTheme.colorScheme.primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Nivel ${nivelDe(estado.puntos)}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            rango.nombre,
                            color = colorRango(rango.nombre),
                            fontWeight = FontWeight.ExtraBold
                        )
                        LinearProgressIndicator(
                            progress = { progresoRango(estado.puntos) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            color = colorRango(rango.nombre),
                            trackColor = LocalGlassColors.current.divider
                        )
                        Textito(
                            if (siguiente != null)
                                "${estado.puntos} de ${siguiente.minimo} puntos para ${siguiente.nombre}"
                            else "${estado.puntos} puntos"
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rangos.forEach { r ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Insignia(
                                nombre = r.nombre,
                                activo = r.nombre == rango.nombre
                            )
                            Text(
                                r.nombre.take(4),
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color = colorRango(r.nombre)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Retos activos", style = MaterialTheme.typography.titleMedium)
                BotonRedondo(onClick = { creandoReto = true }, descripcion = "Nuevo reto") {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }

            estado.retos.forEach { reto ->
                GlassCard(modifier = Modifier.fillMaxWidth(), spacing = 10.dp) {
                    Text(reto.titulo, fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(
                        progress = { (reto.progreso.toFloat() / reto.meta).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = LocalGlassColors.current.divider
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Textito("${reto.progreso} de ${reto.meta}")
                        if (reto.completado) Etiqueta("Completado")
                        else BotonTexto("Borrar") { Repo.borrarReto(reto.id) }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mis recompensas", style = MaterialTheme.typography.titleMedium)
                BotonRedondo(onClick = { creandoRecompensa = true }, descripcion = "Nueva recompensa") {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }

            GlassList(modifier = Modifier.fillMaxWidth()) {
                estado.recompensas.forEachIndexed { indice, recompensa ->
                    if (indice > 0) Separador()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { Repo.alternarRecompensa(recompensa.id) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(recompensa.titulo, fontWeight = FontWeight.Bold)
                            Textito(recompensa.condicion)
                        }
                        Etiqueta(if (recompensa.lograda) "Lograda" else "Pendiente")
                    }
                }
            }
            Textito("Elige recompensas que no sean comida, para que no compitan con lo que construyes.")
        }
    }

    if (creandoReto) {
        DialogoReto { creandoReto = false }
    }
    if (creandoRecompensa) {
        DialogoRecompensa { creandoRecompensa = false }
    }
}

/** Mascota original, dibujada con formas simples. */
@Composable
private fun Mascota(color: Color) {
    Canvas(modifier = Modifier.size(84.dp)) {
        val ancho = size.width
        val alto = size.height
        drawOval(
            color = color,
            topLeft = Offset(ancho * 0.18f, alto * 0.12f),
            size = Size(ancho * 0.64f, alto * 0.74f)
        )
        drawCircle(color = color, radius = ancho * 0.09f, center = Offset(ancho * 0.28f, alto * 0.16f))
        drawCircle(color = color, radius = ancho * 0.09f, center = Offset(ancho * 0.72f, alto * 0.16f))
        drawOval(
            color = Color.White.copy(alpha = 0.9f),
            topLeft = Offset(ancho * 0.28f, alto * 0.42f),
            size = Size(ancho * 0.44f, alto * 0.34f)
        )
        drawCircle(color = Color(0xFF1C1F2A), radius = ancho * 0.055f, center = Offset(ancho * 0.39f, alto * 0.45f))
        drawCircle(color = Color(0xFF1C1F2A), radius = ancho * 0.055f, center = Offset(ancho * 0.61f, alto * 0.45f))
        drawCircle(color = Color.White, radius = ancho * 0.02f, center = Offset(ancho * 0.41f, alto * 0.43f))
        drawCircle(color = Color.White, radius = ancho * 0.02f, center = Offset(ancho * 0.63f, alto * 0.43f))
    }
}

/** Símbolo de cada rango: una figura distinta por rango. */
@Composable
private fun Insignia(nombre: String, activo: Boolean) {
    val color = colorRango(nombre)
    Canvas(modifier = Modifier.size(if (activo) 34.dp else 28.dp)) {
        val s = size.minDimension
        val centro = Offset(s / 2f, s / 2f)
        val relleno = if (activo) color else color.copy(alpha = 0.35f)
        when (nombre) {
            "Plata" -> drawCircle(relleno, radius = s * 0.30f, center = centro)
            "Oro" -> {
                drawCircle(relleno, radius = s * 0.34f, center = centro)
                drawCircle(Color.White.copy(alpha = 0.6f), radius = s * 0.14f, center = centro)
            }
            "Platino" -> drawRect(
                relleno,
                topLeft = Offset(s * 0.18f, s * 0.18f),
                size = Size(s * 0.64f, s * 0.64f)
            )
            "Diamante" -> {
                rotate(45f) {
                    drawRect(
                        relleno,
                        topLeft = Offset(s * 0.22f, s * 0.22f),
                        size = Size(s * 0.56f, s * 0.56f)
                    )
                }
            }
            "Ascendente" -> drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(s * 0.5f, s * 0.15f)
                    lineTo(s * 0.85f, s * 0.8f)
                    lineTo(s * 0.15f, s * 0.8f)
                    close()
                },
                color = relleno
            )
            "Inmortal" -> drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(s * 0.5f, s * 0.12f)
                    cubicTo(s * 0.9f, s * 0.45f, s * 0.78f, s * 0.9f, s * 0.5f, s * 0.9f)
                    cubicTo(s * 0.22f, s * 0.9f, s * 0.1f, s * 0.45f, s * 0.5f, s * 0.12f)
                    close()
                },
                color = relleno
            )
            else -> {
                drawCircle(relleno, radius = s * 0.22f, center = centro)
                repeat(8) { i ->
                    rotate(i * 45f) {
                        drawRect(
                            relleno,
                            topLeft = Offset(s * 0.48f, s * 0.02f),
                            size = Size(s * 0.04f, s * 0.16f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoReto(onCerrar: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var meta by remember { mutableStateOf("4") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nuevo reto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(titulo, "Reto (de proceso)") { titulo = it }
                Campo(meta, "Meta (veces)") { texto -> meta = texto.filter { it.isDigit() } }
                Textito("Ejemplo: comer sin pantalla 4 veces esta semana.")
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                val objetivo = meta.toIntOrNull() ?: 1
                if (titulo.isNotBlank()) {
                    Repo.agregarReto(Reto(titulo = titulo.trim(), meta = objetivo))
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}

@Composable
private fun DialogoRecompensa(onCerrar: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var condicion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nueva recompensa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(titulo, "Recompensa") { titulo = it }
                Campo(condicion, "¿Cuándo la ganas?") { condicion = it }
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                if (titulo.isNotBlank()) {
                    Repo.agregarRecompensa(
                        Recompensa(titulo = titulo.trim(), condicion = condicion.trim())
                    )
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}
