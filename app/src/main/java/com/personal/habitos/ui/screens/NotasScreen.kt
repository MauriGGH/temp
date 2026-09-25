package com.personal.habitos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Nota
import com.personal.habitos.data.Repo
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate

@Composable
fun NotasScreen(contentPadding: PaddingValues, onVolver: () -> Unit) {
    val estado = Repo.estado
    var texto by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("Casa") }

    Pantalla(
        titulo = "Notas",
        contentPadding = contentPadding,
        subtitulo = "Para ti. Sin calificaciones, solo lo que notaste.",
        onVolver = onVolver
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("¿Qué notaste?") },
                    placeholder = { Text("Qué pasó antes, qué sentías, qué te dijiste…") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Casa", "Escuela", "Otro").forEach { opcion ->
                        Chip(opcion, lugar == opcion, Modifier.weight(1f)) { lugar = opcion }
                    }
                }
                BotonPrincipal("Guardar nota", Modifier.fillMaxWidth()) {
                    if (texto.isNotBlank()) {
                        Repo.agregarNota(
                            Nota(
                                fecha = LocalDate.now().toEpochDay(),
                                texto = texto.trim(),
                                lugar = lugar
                            )
                        )
                        texto = ""
                    }
                }
            }

            Text("Anteriores", style = MaterialTheme.typography.titleMedium)

            estado.notas.sortedByDescending { it.fecha }.forEach { nota ->
                GlassCard(modifier = Modifier.fillMaxWidth(), spacing = 6.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            LocalDate.ofEpochDay(nota.fecha).toString() + " · " + nota.lugar,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Borrar nota",
                            tint = LocalGlassColors.current.textMuted,
                            modifier = Modifier.clickable { Repo.borrarNota(nota.id) }
                        )
                    }
                    Text(nota.texto)
                }
            }
        }
    }
}
