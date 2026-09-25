package com.personal.habitos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.personal.habitos.data.Ejercicio
import com.personal.habitos.data.RegistroSesion
import com.personal.habitos.data.Repo
import com.personal.habitos.data.siguienteSesion
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate

@Composable
fun EntrenoScreen(contentPadding: PaddingValues) {
    val estado = Repo.estado
    var sesion by remember { mutableStateOf(siguienteSesion(estado.sesiones)) }
    var hechos by remember { mutableStateOf(setOf<String>()) }
    var esfuerzo by remember { mutableStateOf(mapOf<String, String>()) }
    var nota by remember { mutableStateOf("") }
    var guardada by remember { mutableStateOf(false) }

    val plantilla = estado.plantillas.firstOrNull { it.nombre == sesion }
    val ultima = estado.sesiones.maxByOrNull { it.fecha }

    Pantalla(
        titulo = "Entreno",
        contentPadding = contentPadding,
        subtitulo = "Te toca ${siguienteSesion(estado.sesiones)}. Primero lo pesado."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("A", "B").forEach { nombre ->
                    Chip(
                        texto = "Sesión $nombre",
                        seleccionado = sesion == nombre,
                        modifier = Modifier.weight(1f)
                    ) {
                        sesion = nombre
                        hechos = emptySet()
                        esfuerzo = emptyMap()
                    }
                }
            }

            plantilla?.ejercicios?.forEach { ejercicio ->
                TarjetaEjercicio(
                    ejercicio = ejercicio,
                    hecho = hechos.contains(ejercicio.nombre),
                    esfuerzoActual = esfuerzo[ejercicio.nombre],
                    ultimoEsfuerzo = ultima?.esfuerzo?.get(ejercicio.nombre),
                    onMarcar = {
                        hechos = if (hechos.contains(ejercicio.nombre)) {
                            hechos - ejercicio.nombre
                        } else {
                            hechos + ejercicio.nombre
                        }
                    },
                    onEsfuerzo = { valor ->
                        esfuerzo = esfuerzo + (ejercicio.nombre to valor)
                        hechos = hechos + ejercicio.nombre
                    }
                )
            }

            Campo(nota, "Nota de la sesión") { nota = it }

            BotonPrincipal(
                if (guardada) "Sesión guardada" else "Terminar sesión",
                Modifier.fillMaxWidth()
            ) {
                if (!guardada && hechos.isNotEmpty()) {
                    Repo.guardarSesion(
                        RegistroSesion(
                            fecha = LocalDate.now().toEpochDay(),
                            sesion = sesion,
                            ejerciciosHechos = hechos.toList(),
                            esfuerzo = esfuerzo,
                            nota = nota
                        )
                    )
                    guardada = true
                }
            }

            if (estado.sesiones.isNotEmpty()) {
                Text("Historial", style = MaterialTheme.typography.titleMedium)
                GlassList(modifier = Modifier.fillMaxWidth()) {
                    estado.sesiones.sortedByDescending { it.fecha }.take(6)
                        .forEachIndexed { indice, registro ->
                            if (indice > 0) Separador()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Sesión ${registro.sesion}", fontWeight = FontWeight.Bold)
                                    Textito(
                                        LocalDate.ofEpochDay(registro.fecha).toString() +
                                            " · ${registro.ejerciciosHechos.size} ejercicios"
                                    )
                                }
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun TarjetaEjercicio(
    ejercicio: Ejercicio,
    hecho: Boolean,
    esfuerzoActual: String?,
    ultimoEsfuerzo: String?,
    onMarcar: () -> Unit,
    onEsfuerzo: (String) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), spacing = 10.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Marcar ${ejercicio.nombre}",
                tint = if (hecho) MaterialTheme.colorScheme.primary else LocalGlassColors.current.textMuted,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onMarcar() }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(ejercicio.nombre, fontWeight = FontWeight.Bold)
                Textito(
                    if (ultimoEsfuerzo != null) "Última vez: $ultimoEsfuerzo"
                    else ejercicio.categoria + if (ejercicio.extra) " · extra" else ""
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Fácil", "Justo", "Difícil").forEach { valor ->
                Chip(valor, esfuerzoActual == valor, Modifier.weight(1f)) { onEsfuerzo(valor) }
            }
        }
    }
}
