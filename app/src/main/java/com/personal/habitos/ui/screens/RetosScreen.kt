package com.personal.habitos.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.personal.habitos.ui.components.InsigniaRango
import com.personal.habitos.ui.components.coloresRango

private fun colorRango(nombre: String): Color = coloresRango(nombre).claro

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
                    Mascota(color = MaterialTheme.colorScheme.primary, rango = rango.nombre)
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
                Separador()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    rangos.forEach { r ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            InsigniaRango(
                                nombre = r.nombre,
                                tamano = if (r.nombre == rango.nombre) 62.dp else 50.dp,
                                activo = estado.puntos >= r.minimo
                            )
                            Text(
                                r.nombre,
                                style = MaterialTheme.typography.labelSmall,
                                color = colorRango(r.nombre),
                                fontWeight = if (r.nombre == rango.nombre) FontWeight.ExtraBold
                                else FontWeight.Normal
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

            if (estado.retos.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Textito("Sin retos activos. Crea uno de proceso, por ejemplo: comer sin pantalla 4 veces esta semana.")
                }
            }

            estado.retos.forEach { reto ->
                GlassCard(modifier = Modifier.fillMaxWidth(), spacing = 10.dp) {
                    Text(reto.titulo, fontWeight = FontWeight.Bold)
                    Textito(
                        when {
                            reto.objetivo == "entreno" -> "Avanza al entrenar"
                            reto.objetivo == "repaso" -> "Avanza al repasar"
                            else -> "Avanza con: " + (estado.habitos.firstOrNull {
                                "habito:" + it.id == reto.objetivo
                            }?.nombre ?: "un hábito borrado")
                        }
                    )
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

            if (estado.recompensas.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Textito("Aún no defines recompensas. Elige algo que te guste y que no sea comida.")
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

/** Mascota original: cuerpo con degradado, brillo y adornos según el rango. */
@Composable
private fun Mascota(color: Color, rango: String) {
    val acento = colorRango(rango)
    Canvas(modifier = Modifier.size(96.dp)) {
        val a = size.width
        val h = size.height
        val centro = Offset(a / 2f, h * 0.54f)

        if (rango in listOf("Diamante", "Ascendente", "Inmortal", "Radiante")) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(acento.copy(alpha = 0.28f), Color.Transparent),
                    center = centro,
                    radius = a * 0.55f
                ),
                radius = a * 0.55f,
                center = centro
            )
        }

        // Sombra en el piso
        drawOval(
            color = Color.Black.copy(alpha = 0.18f),
            topLeft = Offset(a * 0.24f, h * 0.88f),
            size = Size(a * 0.52f, h * 0.07f)
        )

        // Orejas
        listOf(-1f, 1f).forEach { lado ->
            drawCircle(
                color = color.copy(alpha = 0.95f),
                radius = a * 0.11f,
                center = Offset(centro.x + lado * a * 0.25f, h * 0.20f)
            )
        }

        // Cuerpo
        drawOval(
            brush = Brush.verticalGradient(
                listOf(color, color.copy(alpha = 0.72f)),
                startY = h * 0.14f,
                endY = h * 0.90f
            ),
            topLeft = Offset(a * 0.16f, h * 0.14f),
            size = Size(a * 0.68f, h * 0.76f)
        )

        // Brillo superior
        drawOval(
            brush = Brush.verticalGradient(
                listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                startY = h * 0.14f,
                endY = h * 0.50f
            ),
            topLeft = Offset(a * 0.24f, h * 0.17f),
            size = Size(a * 0.52f, h * 0.30f)
        )

        // Cara
        drawOval(
            color = Color(0xFFFDFDFF),
            topLeft = Offset(a * 0.26f, h * 0.40f),
            size = Size(a * 0.48f, h * 0.36f)
        )

        // Ojos
        listOf(-1f, 1f).forEach { lado ->
            drawOval(
                color = Color(0xFF15171C),
                topLeft = Offset(centro.x + lado * a * 0.14f - a * 0.055f, h * 0.48f),
                size = Size(a * 0.11f, h * 0.13f)
            )
            drawCircle(
                color = Color.White,
                radius = a * 0.022f,
                center = Offset(centro.x + lado * a * 0.14f + a * 0.02f, h * 0.51f)
            )
        }

        // Cachetes
        listOf(-1f, 1f).forEach { lado ->
            drawCircle(
                color = acento.copy(alpha = 0.28f),
                radius = a * 0.045f,
                center = Offset(centro.x + lado * a * 0.20f, h * 0.63f)
            )
        }

        // Sonrisa
        drawPath(
            path = Path().apply {
                moveTo(centro.x - a * 0.07f, h * 0.65f)
                quadraticBezierTo(centro.x, h * 0.72f, centro.x + a * 0.07f, h * 0.65f)
            },
            color = Color(0xFF15171C),
            style = Stroke(width = a * 0.022f, cap = StrokeCap.Round)
        )

        // Corona desde Oro
        if (rango != "Plata") {
            drawPath(
                path = Path().apply {
                    moveTo(a * 0.33f, h * 0.17f)
                    lineTo(a * 0.40f, h * 0.05f)
                    lineTo(a * 0.50f, h * 0.14f)
                    lineTo(a * 0.60f, h * 0.05f)
                    lineTo(a * 0.67f, h * 0.17f)
                    close()
                },
                brush = Brush.verticalGradient(
                    listOf(acento, acento.copy(alpha = 0.7f)),
                    startY = h * 0.05f,
                    endY = h * 0.17f
                )
            )
        }

        // Destellos en Radiante
        if (rango == "Radiante") {
            repeat(6) { i ->
                rotate(i * 60f, centro) {
                    drawRoundRect(
                        color = acento.copy(alpha = 0.55f),
                        topLeft = Offset(centro.x - a * 0.012f, h * 0.02f),
                        size = Size(a * 0.024f, h * 0.07f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(a * 0.012f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogoReto(onCerrar: () -> Unit) {
    val habitos = Repo.estado.habitos
    var titulo by remember { mutableStateOf("") }
    var meta by remember { mutableStateOf("4") }
    var objetivo by remember { mutableStateOf(habitos.firstOrNull()?.let { "habito:" + it.id } ?: "entreno") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nuevo reto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(titulo, "Reto (de proceso)") { titulo = it }
                Campo(meta, "Meta (veces)") { texto -> meta = texto.filter { c -> c.isDigit() } }
                Textito("¿Qué lo hace avanzar?")
                habitos.forEach { habito ->
                    Chip(
                        habito.nombre,
                        objetivo == "habito:" + habito.id,
                        Modifier.fillMaxWidth()
                    ) { objetivo = "habito:" + habito.id }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip("Entrenar", objetivo == "entreno", Modifier.weight(1f)) { objetivo = "entreno" }
                    Chip("Repasar", objetivo == "repaso", Modifier.weight(1f)) { objetivo = "repaso" }
                }
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                val objetivoMeta = meta.toIntOrNull() ?: 1
                if (titulo.isNotBlank()) {
                    Repo.agregarReto(
                        Reto(titulo = titulo.trim(), meta = objetivoMeta, objetivo = objetivo)
                    )
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
