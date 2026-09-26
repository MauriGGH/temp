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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
    val toca = siguienteSesion(estado.sesiones)
    var sesion by remember { mutableStateOf(toca) }
    var hechos by remember { mutableStateOf(setOf<String>()) }
    var esfuerzo by remember { mutableStateOf(mapOf<String, String>()) }
    var nota by remember { mutableStateOf("") }
    var guardada by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf(false) }
    var abierta by remember { mutableStateOf<String?>(null) }

    val plantilla = estado.plantillas.firstOrNull { it.nombre == sesion }
    val ultimaDeEsta = estado.sesiones.filter { it.sesion == sesion }.maxByOrNull { it.fecha }

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
                        hechos = emptySet()
                        esfuerzo = emptyMap()
                        guardada = false
                    }
                }
            }

            plantilla?.ejercicios?.sortedBy { it.extra }?.forEach { ejercicio ->
                TarjetaEjercicio(
                    ejercicio = ejercicio,
                    hecho = hechos.contains(ejercicio.nombre),
                    esfuerzoActual = esfuerzo[ejercicio.nombre],
                    ultimoEsfuerzo = ultimaDeEsta?.esfuerzo?.get(ejercicio.nombre),
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
                    },
                    onBorrar = { Repo.borrarEjercicio(sesion, ejercicio.nombre) }
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
                    hechos = emptySet()
                    esfuerzo = emptyMap()
                    nota = ""
                }
            }
            if (!guardada && hechos.isEmpty()) {
                Textito("Marca al menos un ejercicio para guardar la sesión.")
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
                                                " · ${registro.ejerciciosHechos.size} ejercicios"
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
                                    registro.ejerciciosHechos.forEach { nombre ->
                                        Textito(
                                            nombre + (registro.esfuerzo[nombre]?.let { " · $it" } ?: "")
                                        )
                                    }
                                    if (registro.nota.isNotBlank()) Textito("Nota: ${registro.nota}")
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
    hecho: Boolean,
    esfuerzoActual: String?,
    ultimoEsfuerzo: String?,
    onMarcar: () -> Unit,
    onEsfuerzo: (String) -> Unit,
    onBorrar: () -> Unit
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
                    buildString {
                        append(ejercicio.categoria)
                        if (ejercicio.extra) append(" · extra")
                        if (ultimoEsfuerzo != null) append(" · la última vez: $ultimoEsfuerzo")
                    }
                )
            }
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Quitar ${ejercicio.nombre}",
                tint = LocalGlassColors.current.textMuted,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBorrar() }
            )
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
                        Ejercicio(nombre.trim(), categoria, extra)
                    )
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}
