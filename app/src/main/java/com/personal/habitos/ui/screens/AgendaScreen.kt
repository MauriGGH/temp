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
import androidx.compose.material3.MaterialTheme
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
import com.personal.habitos.data.Bloque
import com.personal.habitos.data.Repo
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate

private val dias = listOf("L", "M", "M", "J", "V", "S", "D")
private val periodos = listOf("Mañana", "Tarde", "Noche")

@Composable
fun AgendaScreen(contentPadding: PaddingValues, onVolver: () -> Unit) {
    val estado = Repo.estado
    var diaSel by remember { mutableStateOf(LocalDate.now().dayOfWeek.value) }
    var creando by remember { mutableStateOf(false) }

    Pantalla(
        titulo = "Agenda",
        contentPadding = contentPadding,
        subtitulo = "Mover o saltar un bloque no cuenta como falla.",
        onVolver = onVolver,
        accion = {
            BotonRedondo(onClick = { creando = true }, descripcion = "Nuevo bloque") {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                (1..7).forEach { dia ->
                    Chip(dias[dia - 1], diaSel == dia, Modifier.weight(1f)) { diaSel = dia }
                }
            }

            periodos.forEach { periodo ->
                val bloques = estado.bloques.filter { it.dia == diaSel && it.periodo == periodo }
                Text(periodo, style = MaterialTheme.typography.titleMedium)
                if (bloques.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) { Textito("Sin bloques.") }
                } else {
                    GlassList(modifier = Modifier.fillMaxWidth()) {
                        bloques.forEachIndexed { indice, bloque ->
                            if (indice > 0) Separador()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bloque.titulo, fontWeight = FontWeight.Bold)
                                    Textito(
                                        when (bloque.tipo) {
                                            "libre" -> "Tiempo libre planeado"
                                            "entreno" -> "Entrenamiento"
                                            "habito" -> "Hábito"
                                            "escuela" -> "Escuela"
                                            else -> "Bloque"
                                        }
                                    )
                                }
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Borrar ${bloque.titulo}",
                                    tint = LocalGlassColors.current.textMuted,
                                    modifier = Modifier.clickable { Repo.borrarBloque(bloque.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (creando) {
        DialogoBloque(dia = diaSel) { creando = false }
    }
}

@Composable
private fun DialogoBloque(dia: Int, onCerrar: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var periodo by remember { mutableStateOf(periodos.first()) }
    var tipo by remember { mutableStateOf("otro") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nuevo bloque") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(titulo, "Título") { titulo = it }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    periodos.forEach { p ->
                        Chip(p, periodo == p, Modifier.weight(1f)) { periodo = p }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("otro" to "Otro", "libre" to "Libre", "escuela" to "Escuela").forEach { (clave, texto) ->
                        Chip(texto, tipo == clave, Modifier.weight(1f)) { tipo = clave }
                    }
                }
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                if (titulo.isNotBlank()) {
                    Repo.agregarBloque(
                        Bloque(dia = dia, periodo = periodo, titulo = titulo.trim(), tipo = tipo)
                    )
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}
