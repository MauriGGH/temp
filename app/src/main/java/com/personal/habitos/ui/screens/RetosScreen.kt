package com.personal.habitos.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.personal.habitos.ui.components.InsigniaRango
import com.personal.habitos.ui.components.coloresRango
import com.personal.habitos.ui.theme.LocalGlassColors

private fun colorRango(nombre: String): Color = coloresRango(nombre).claro

@Composable
fun RetosScreen(contentPadding: PaddingValues, onVolver: (() -> Unit)? = null) {
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

/** Guardián: casco angular, visor luminoso y cresta del color del rango. */
@Composable
private fun Mascota(color: Color, rango: String) {
    val cresta = colorRango(rango)
    Canvas(modifier = Modifier.size(112.dp)) {
        val u = size.width / 120f
        fun p(vararg puntos: Pair<Float, Float>) = Path().apply {
            puntos.forEachIndexed { i, (x, y) ->
                if (i == 0) moveTo(x * u, y * u) else lineTo(x * u, y * u)
            }
            close()
        }

        val centro = Offset(60f * u, 62f * u)

        // Aura del rango
        drawCircle(
            brush = Brush.radialGradient(
                listOf(cresta.copy(alpha = 0.30f), Color.Transparent),
                center = centro,
                radius = 56f * u
            ),
            radius = 56f * u,
            center = centro
        )

        // Sombra
        drawOval(
            color = Color.Black.copy(alpha = 0.32f),
            topLeft = Offset(30f * u, 105f * u),
            size = Size(60f * u, 10f * u)
        )

        val cuerpo = Brush.verticalGradient(
            listOf(Color(0xFF2B3040), Color(0xFF14171F)),
            startY = 12f * u,
            endY = 104f * u
        )

        // Orejas
        drawPath(p(26f to 44f, 30f to 12f, 50f to 34f), cuerpo)
        drawPath(p(94f to 44f, 90f to 12f, 70f to 34f), cuerpo)

        // Casco
        drawPath(
            p(60f to 20f, 96f to 40f, 96f to 74f, 60f to 100f, 24f to 74f, 24f to 40f),
            cuerpo
        )
        drawPath(
            p(60f to 20f, 96f to 40f, 96f to 74f, 60f to 100f, 24f to 74f, 24f to 40f),
            color = Color.White.copy(alpha = 0.22f),
            style = Stroke(width = 1.6f * u)
        )

        // Placa interior del color de la app
        drawPath(
            p(60f to 28f, 86f to 44f, 86f to 70f, 60f to 90f, 34f to 70f, 34f to 44f),
            color = color.copy(alpha = 0.85f),
            style = Stroke(width = 2.4f * u)
        )

        // Visores
        drawPath(p(36f to 54f, 54f to 50f, 54f to 62f, 36f to 60f), cresta)
        drawPath(p(84f to 54f, 66f to 50f, 66f to 62f, 84f to 60f), cresta)

        // Núcleo
        drawPath(
            p(60f to 64f, 68f to 72f, 60f to 84f, 52f to 72f),
            brush = Brush.verticalGradient(
                listOf(color, color.copy(alpha = 0.55f)),
                startY = 64f * u,
                endY = 84f * u
            )
        )

        // Mandíbula
        drawPath(p(52f to 86f, 68f to 86f, 60f to 96f), color = Color(0xFF1B1F29))

        // Cresta del rango
        drawPath(p(48f to 22f, 60f to 2f, 72f to 22f, 60f to 16f), color = cresta)

        // Hombreras
        drawPath(p(14f to 66f, 26f to 58f, 30f to 78f, 16f to 80f), cuerpo)
        drawPath(p(106f to 66f, 94f to 58f, 90f to 78f, 104f to 80f), cuerpo)
        listOf(
            p(14f to 66f, 26f to 58f, 30f to 78f, 16f to 80f),
            p(106f to 66f, 94f to 58f, 90f to 78f, 104f to 80f)
        ).forEach {
            drawPath(it, color = cresta.copy(alpha = 0.5f), style = Stroke(width = 1.2f * u))
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
