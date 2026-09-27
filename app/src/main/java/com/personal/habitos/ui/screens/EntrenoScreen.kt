package com.personal.habitos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Ejercicio
import com.personal.habitos.data.RegistroSesion
import com.personal.habitos.data.Repo
import com.personal.habitos.data.Serie
import com.personal.habitos.data.siguienteSesion
import com.personal.habitos.ui.components.AnilloProgreso
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.components.Widget
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate
import kotlinx.coroutines.delay

private fun reloj(segundos: Int): String =
    "%d:%02d".format(segundos / 60, segundos % 60)

@Composable
fun EntrenoScreen(contentPadding: PaddingValues) {
    val estado = Repo.estado
    val toca = siguienteSesion(estado.sesiones)
    var sesion by remember { mutableStateOf(toca) }
    var series by remember { mutableStateOf(listOf<Serie>()) }
    var esfuerzo by remember { mutableStateOf(mapOf<String, String>()) }
    var nota by remember { mutableStateOf("") }
    var guardada by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf(false) }
    var abierta by remember { mutableStateOf<String?>(null) }

    // Cronómetros
    var segundosSesion by remember { mutableStateOf(0) }
    var corriendo by remember { mutableStateOf(false) }
    var descanso by remember { mutableStateOf(0) }
    var descansoTotal by remember { mutableStateOf(90) }

    LaunchedEffect(corriendo) {
        while (corriendo) {
            delay(1000)
            segundosSesion += 1
        }
    }
    LaunchedEffect(descanso > 0) {
        while (descanso > 0) {
            delay(1000)
            descanso -= 1
        }
    }

    val plantilla = estado.plantillas.firstOrNull { it.nombre == sesion }
    val ultimaDeEsta = estado.sesiones.filter { it.sesion == sesion }.maxByOrNull { it.fecha }
    val totalSeries = series.size

    Pantalla(
        titulo = "Entreno",
        contentPadding = contentPadding,
        subtitulo = "Te toca $toca. Primero lo pesado; los extras al final.",
        accion = {
            BotonRedondo(onClick = { editando = true }, descripcion = "Añadir ejercicio") {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                estado.plantillas.forEach { p ->
                    Chip(
                        texto = "Sesión ${p.nombre}",
                        seleccionado = sesion == p.nombre,
                        modifier = Modifier.weight(1f)
                    ) {
                        sesion = p.nombre
                        series = emptyList()
                        esfuerzo = emptyMap()
                        guardada = false
                    }
                }
            }

            // Temporizador de descanso
            if (descanso > 0) {
                Widget(titulo = "Descanso", modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AnilloProgreso(
                            progreso = descanso.toFloat() / descansoTotal.coerceAtLeast(1),
                            tamano = 150.dp,
                            grosor = 12.dp
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(reloj(descanso), style = MaterialTheme.typography.displaySmall)
                                Textito("para la siguiente serie")
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip("−15 s", false, Modifier.weight(1f)) {
                            descanso = (descanso - 15).coerceAtLeast(0)
                        }
                        Chip("+15 s", false, Modifier.weight(1f)) {
                            descanso += 15
                            descansoTotal = maxOf(descansoTotal, descanso)
                        }
                        Chip("Saltar", true, Modifier.weight(1f)) { descanso = 0 }
                    }
                }
            }

            // Cifras de la sesión
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassCard(modifier = Modifier.weight(1f), contentPadding = 14.dp, spacing = 2.dp) {
                    Textito("TIEMPO")
                    Text(reloj(segundosSesion), style = MaterialTheme.typography.headlineSmall)
                    Textito(if (corriendo) "en curso" else "detenido")
                }
                GlassCard(modifier = Modifier.weight(1f), contentPadding = 14.dp, spacing = 2.dp) {
                    Textito("SERIES")
                    Text("$totalSeries", style = MaterialTheme.typography.headlineSmall)
                    Textito("registradas")
                }
                GlassCard(modifier = Modifier.weight(1f), contentPadding = 14.dp, spacing = 2.dp) {
                    Textito("ANTERIOR")
                    Text(
                        "${ultimaDeEsta?.series?.size ?: 0}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Textito("series")
                }
            }

            Chip(
                if (corriendo) "Pausar cronómetro" else "Iniciar cronómetro",
                corriendo,
                Modifier.fillMaxWidth()
            ) { corriendo = !corriendo }

            plantilla?.ejercicios?.sortedBy { it.extra }?.forEach { ejercicio ->
                TarjetaEjercicio(
                    ejercicio = ejercicio,
                    seriesHechas = series.filter { it.ejercicio == ejercicio.nombre },
                    seriesPrevias = ultimaDeEsta?.series?.filter { it.ejercicio == ejercicio.nombre }
                        ?: emptyList(),
                    esfuerzoActual = esfuerzo[ejercicio.nombre],
                    onSerie = { peso, reps ->
                        series = series + Serie(ejercicio.nombre, peso, reps)
                        descansoTotal = ejercicio.descanso
                        descanso = ejercicio.descanso
                        if (!corriendo) corriendo = true
                    },
                    onQuitarSerie = {
                        val ultimo = series.lastOrNull { it.ejercicio == ejercicio.nombre }
                        if (ultimo != null) series = series - ultimo
                    },
                    onEsfuerzo = { valor -> esfuerzo = esfuerzo + (ejercicio.nombre to valor) },
                    onMover = { arriba -> Repo.moverEjercicio(sesion, ejercicio.nombre, arriba) },
                    onBorrar = { Repo.borrarEjercicio(sesion, ejercicio.nombre) }
                )
            }

            Campo(nota, "Nota de la sesión") { nota = it }

            BotonPrincipal(
                if (guardada) "Sesión guardada" else "Terminar sesión",
                Modifier.fillMaxWidth()
            ) {
                if (!guardada && series.isNotEmpty()) {
                    Repo.guardarSesion(
                        RegistroSesion(
                            fecha = LocalDate.now().toEpochDay(),
                            sesion = sesion,
                            ejerciciosHechos = series.map { it.ejercicio }.distinct(),
                            esfuerzo = esfuerzo,
                            series = series,
                            duracion = segundosSesion,
                            nota = nota
                        )
                    )
                    guardada = true
                    corriendo = false
                    descanso = 0
                    series = emptyList()
                    esfuerzo = emptyMap()
                    nota = ""
                }
            }
            if (!guardada && series.isEmpty()) {
                Textito("Registra al menos una serie para guardar la sesión.")
            }

            // Progresión
            val historial = estado.sesiones.filter { it.sesion == sesion }
                .sortedBy { it.fecha }
                .takeLast(6)
            if (historial.isNotEmpty()) {
                Widget(titulo = "Progresión", modifier = Modifier.fillMaxWidth()) {
                    val maximo = (historial.maxOfOrNull { it.series.size } ?: 1).coerceAtLeast(1)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        historial.forEach { registro ->
                            val fraccion = registro.series.size.toFloat() / maximo
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height((80 * fraccion.coerceAtLeast(0.12f)).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                    Textito("Series registradas en tus últimas sesiones $sesion.")
                }
            }

            Text("Historial", style = MaterialTheme.typography.titleMedium)

            if (estado.sesiones.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Textito("Aquí aparecerán tus sesiones cuando registres la primera.")
                }
            } else {
                GlassList(modifier = Modifier.fillMaxWidth()) {
                    estado.sesiones.sortedByDescending { it.fecha }.take(10)
                        .forEachIndexed { indice, registro ->
                            if (indice > 0) Separador()
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        abierta = if (abierta == registro.id) null else registro.id
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Sesión ${registro.sesion}", fontWeight = FontWeight.Bold)
                                        Textito(
                                            LocalDate.ofEpochDay(registro.fecha).toString() +
                                                " · ${registro.series.size} series" +
                                                if (registro.duracion > 0) " · ${reloj(registro.duracion)}" else ""
                                        )
                                    }
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Borrar sesión",
                                        tint = LocalGlassColors.current.textMuted,
                                        modifier = Modifier.clickable { Repo.borrarSesion(registro.id) }
                                    )
                                }
                                if (abierta == registro.id) {
                                    registro.series.forEach { serie ->
                                        Textito("${serie.ejercicio}: ${serie.peso} × ${serie.reps}")
                                    }
                                    registro.esfuerzo.forEach { (ejercicio, valor) ->
                                        Textito("$ejercicio · se sintió $valor")
                                    }
                                    Campo(
                                        registro.nota,
                                        "Nota de esta sesión"
                                    ) { texto -> Repo.editarNotaSesion(registro.id, texto) }
                                }
                            }
                        }
                }
                Textito("Toca una sesión para ver el detalle.")
            }
        }
    }

    if (editando) {
        DialogoEjercicio(sesion = sesion) { editando = false }
    }
}

@Composable
private fun TarjetaEjercicio(
    ejercicio: Ejercicio,
    seriesHechas: List<Serie>,
    seriesPrevias: List<Serie>,
    esfuerzoActual: String?,
    onSerie: (String, String) -> Unit,
    onQuitarSerie: () -> Unit,
    onEsfuerzo: (String) -> Unit,
    onMover: (Boolean) -> Unit,
    onBorrar: () -> Unit
) {
    var peso by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    val glass = LocalGlassColors.current

    GlassCard(modifier = Modifier.fillMaxWidth(), spacing = 10.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ejercicio.nombre, fontWeight = FontWeight.Bold)
                Textito(
                    buildString {
                        append(ejercicio.categoria)
                        append(" · descanso ${ejercicio.descanso} s")
                        if (ejercicio.extra) append(" · extra")
                    }
                )
            }
            if (seriesHechas.isNotEmpty()) Etiqueta("${seriesHechas.size} series")
            BotonTexto("↑") { onMover(true) }
            BotonTexto("↓") { onMover(false) }
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Quitar ${ejercicio.nombre}",
                tint = glass.textMuted,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBorrar() }
            )
        }

        seriesHechas.forEachIndexed { indice, serie ->
            val previa = seriesPrevias.getOrNull(indice)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Textito("Serie ${indice + 1}", Modifier.width(62.dp))
                Text(
                    "${serie.peso} × ${serie.reps}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Textito(
                    previa?.let { "antes ${it.peso} × ${it.reps}" } ?: "nueva",
                    Modifier.width(96.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = reps,
                onValueChange = { texto -> reps = texto.filter { it.isDigit() } },
                label = { Text("Reps") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip("Añadir serie", true, Modifier.weight(1f)) {
                if (reps.isNotBlank()) {
                    onSerie(peso.ifBlank { "—" }, reps)
                    reps = ""
                }
            }
            if (seriesHechas.isNotEmpty()) {
                Chip("Quitar última", false, Modifier.weight(1f)) { onQuitarSerie() }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Fácil", "Justo", "Difícil").forEach { valor ->
                Chip(valor, esfuerzoActual == valor, Modifier.weight(1f)) { onEsfuerzo(valor) }
            }
        }
    }
}

@Composable
private fun DialogoEjercicio(sesion: String, onCerrar: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Pierna") }
    var extra by remember { mutableStateOf(false) }
    var descanso by remember { mutableStateOf("90") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Añadir a la sesión $sesion") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(nombre, "Ejercicio") { nombre = it }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Pierna", "Empuje", "Jalón").forEach { opcion ->
                        Chip(opcion, categoria == opcion, Modifier.weight(1f)) { categoria = opcion }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Brazo", "Hombro", "Cierre").forEach { opcion ->
                        Chip(opcion, categoria == opcion, Modifier.weight(1f)) { categoria = opcion }
                    }
                }
                Campo(descanso, "Descanso (segundos)") { texto ->
                    descanso = texto.filter { it.isDigit() }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip("Principal", !extra, Modifier.weight(1f)) { extra = false }
                    Chip("Extra", extra, Modifier.weight(1f)) { extra = true }
                }
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                if (nombre.isNotBlank()) {
                    Repo.agregarEjercicio(
                        sesion,
                        Ejercicio(
                            nombre.trim(),
                            categoria,
                            extra,
                            descanso.toIntOrNull()?.coerceIn(15, 600) ?: 90
                        )
                    )
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}
