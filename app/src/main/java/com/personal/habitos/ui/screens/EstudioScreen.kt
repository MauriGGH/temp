package com.personal.habitos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Repo
import com.personal.habitos.data.Tema
import com.personal.habitos.data.intercalar
import com.personal.habitos.data.intervaloPrevisto
import com.personal.habitos.data.temasDeHoy
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate

@Composable
fun EstudioScreen(contentPadding: PaddingValues, onVolver: () -> Unit) {
    val estado = Repo.estado
    val hoy = LocalDate.now()
    val pendientes = intercalar(temasDeHoy(estado.temas, hoy))
    var repasando by remember { mutableStateOf(false) }
    var creando by remember { mutableStateOf(false) }

    if (repasando && pendientes.isNotEmpty()) {
        RepasoScreen(
            temas = pendientes,
            contentPadding = contentPadding,
            onSalir = { repasando = false }
        )
        return
    }

    Pantalla(
        titulo = "Estudio",
        contentPadding = contentPadding,
        subtitulo = "Repetición espaciada, recuperación activa e intercalado.",
        onVolver = onVolver,
        accion = {
            BotonRedondo(onClick = { creando = true }, descripcion = "Nuevo tema") {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Textito("Hoy toca repasar")
                Text(
                    "${pendientes.size} ${if (pendientes.size == 1) "tema" else "temas"}",
                    style = MaterialTheme.typography.headlineSmall
                )
                if (pendientes.isNotEmpty()) {
                    Textito(pendientes.joinToString(" · ") { it.nombre })
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Etiqueta("Intercalado: ${pendientes.map { it.materia }.distinct().size} materias")
                        Etiqueta("Sin apuntes primero")
                    }
                    BotonPrincipal("Empezar repaso", Modifier.fillMaxWidth()) { repasando = true }
                } else {
                    Textito("Nada pendiente. Los temas vuelven solos cuando toca.")
                }
            }

            Text("Mis temas", style = MaterialTheme.typography.titleMedium)

            GlassList(modifier = Modifier.fillMaxWidth()) {
                estado.temas.sortedBy { it.proximo }.forEachIndexed { indice, tema ->
                    if (indice > 0) Separador()
                    val dias = tema.proximo - hoy.toEpochDay()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tema.nombre, fontWeight = FontWeight.Bold)
                            Textito("${tema.materia} · repasado ${tema.repasos} veces")
                        }
                        Textito(
                            when {
                                dias <= 0 -> "Hoy"
                                dias == 1L -> "Mañana"
                                else -> "En $dias días"
                            }
                        )
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Borrar ${tema.nombre}",
                            tint = LocalGlassColors.current.textMuted,
                            modifier = Modifier.clickable { Repo.borrarTema(tema.id) }
                        )
                    }
                }
            }
        }
    }

    if (creando) {
        DialogoTema { creando = false }
    }
}

@Composable
private fun RepasoScreen(
    temas: List<Tema>,
    contentPadding: PaddingValues,
    onSalir: () -> Unit
) {
    var indice by remember { mutableStateOf(0) }
    var respuesta by remember { mutableStateOf("") }
    val tema = temas.getOrNull(indice)

    if (tema == null) {
        onSalir()
        return
    }

    Pantalla(
        titulo = "Repaso",
        contentPadding = contentPadding,
        subtitulo = "Tema ${indice + 1} de ${temas.size}",
        onVolver = onSalir
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            LinearProgressIndicator(
                progress = { (indice.toFloat() / temas.size).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = LocalGlassColors.current.divider
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Textito(tema.materia)
                Text(tema.nombre, style = MaterialTheme.typography.headlineSmall)
                Textito("Explícalo como si se lo enseñaras a alguien, sin ver tus apuntes. Pregúntate por qué es así.")
                OutlinedTextField(
                    value = respuesta,
                    onValueChange = { respuesta = it },
                    label = { Text("Lo que recuerdas") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            }

            Text("¿Qué tanto lo recordaste?", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Poco" to 0, "Más o menos" to 1, "Bien" to 2).forEach { (texto, calidad) ->
                    Column(modifier = Modifier.weight(1f)) {
                        Chip(texto, false, Modifier.fillMaxWidth()) {
                            Repo.calificarTema(tema, calidad)
                            respuesta = ""
                            indice += 1
                            if (indice >= temas.size) onSalir()
                        }
                        Textito("En ${intervaloPrevisto(tema, calidad)} días")
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoTema(onCerrar: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var materia by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nuevo tema") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(nombre, "Tema") { nombre = it }
                Campo(materia, "Materia") { materia = it }
                Textito("El primer repaso se programa para hoy.")
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                if (nombre.isNotBlank()) {
                    Repo.agregarTema(
                        Tema(
                            nombre = nombre.trim(),
                            materia = materia.ifBlank { "General" },
                            proximo = LocalDate.now().toEpochDay()
                        )
                    )
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}
