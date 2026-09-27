package com.personal.habitos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Bloque
import com.personal.habitos.data.Repo
import com.personal.habitos.data.inicioDeSemana
import com.personal.habitos.sistema.Calendario
import com.personal.habitos.ui.components.Widget
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate

private val dias = listOf("L", "M", "M", "J", "V", "S", "D")
private val periodos = listOf("Mañana", "Tarde", "Noche")

private fun iconoDe(tipo: String): ImageVector = when (tipo) {
    "entreno" -> Icons.Outlined.FitnessCenter
    "escuela" -> Icons.Outlined.MenuBook
    "habito" -> Icons.Outlined.Restaurant
    "libre" -> Icons.Outlined.PlayCircle
    else -> Icons.Outlined.Schedule
}

private fun textoDe(tipo: String): String = when (tipo) {
    "entreno" -> "Entrenamiento"
    "escuela" -> "Escuela"
    "habito" -> "Hábito"
    "libre" -> "Tiempo libre planeado"
    else -> "Bloque"
}

@Composable
fun AgendaScreen(contentPadding: PaddingValues, onVolver: () -> Unit) {
    val estado = Repo.estado
    val hoy = LocalDate.now()
    val lunes = inicioDeSemana(hoy)
    var diaSel by remember { mutableStateOf(hoy.dayOfWeek.value) }
    var creando by remember { mutableStateOf(false) }
    var duplicando by remember { mutableStateOf(false) }
    var aviso by remember { mutableStateOf("") }
    val contexto = androidx.compose.ui.platform.LocalContext.current
    val glass = LocalGlassColors.current

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                (1..7).forEach { dia ->
                    val fecha = lunes.plusDays((dia - 1).toLong())
                    val seleccionado = diaSel == dia
                    val esHoy = fecha == hoy
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent
                            )
                            .border(
                                if (esHoy && !seleccionado) 1.5.dp else 0.dp,
                                if (esHoy && !seleccionado) MaterialTheme.colorScheme.primary
                                else Color.Transparent,
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { diaSel = dia },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            dias[dia - 1],
                            style = MaterialTheme.typography.labelSmall,
                            color = if (seleccionado) MaterialTheme.colorScheme.onPrimary
                            else glass.textMuted
                        )
                        Text(
                            fecha.dayOfMonth.toString(),
                            fontWeight = FontWeight.Bold,
                            color = if (seleccionado) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            periodos.forEach { periodo ->
                val bloques = estado.bloques.filter { it.dia == diaSel && it.periodo == periodo }
                Widget(titulo = periodo, modifier = Modifier.fillMaxWidth()) {
                    if (bloques.isEmpty()) {
                        Textito("Sin bloques.")
                    } else {
                        bloques.forEachIndexed { indice, bloque ->
                            if (indice > 0) Separador()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(glass.glassStrong),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        iconoDe(bloque.tipo),
                                        contentDescription = null,
                                        tint = if (bloque.tipo == "libre") glass.textMuted
                                        else MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bloque.titulo, fontWeight = FontWeight.Bold)
                                    Textito(textoDe(bloque.tipo))
                                }
                                if (bloque.tipo == "libre") Etiqueta("Planeado")
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Borrar ${bloque.titulo}",
                                    tint = glass.textMuted,
                                    modifier = Modifier.clickable { Repo.borrarBloque(bloque.id) }
                                )
                            }
                        }
                    }
                }
            }

            Widget(titulo = "Herramientas del día", modifier = Modifier.fillMaxWidth()) {
                Chip("Duplicar este día a otros", false, Modifier.fillMaxWidth()) {
                    duplicando = true
                }
                Chip("Mandar el día al calendario", false, Modifier.fillMaxWidth()) {
                    val fecha = lunes.plusDays((diaSel - 1).toLong())
                    val delDia = estado.bloques.filter { it.dia == diaSel }
                    var puestos = 0
                    delDia.forEach { bloque ->
                        val hora = when (bloque.periodo) {
                            "Mañana" -> 8
                            "Tarde" -> 15
                            else -> 20
                        }
                        if (Calendario.insertarEvento(contexto, bloque.titulo, fecha, hora, 60)) {
                            puestos += 1
                        }
                    }
                    aviso = if (puestos > 0) "$puestos eventos en tu calendario."
                    else "No se pudo escribir. Abriendo el calendario…"
                    if (puestos == 0 && delDia.isNotEmpty()) {
                        val primero = delDia.first()
                        Calendario.proponerEvento(contexto, primero.titulo, fecha, 8, 60)
                    }
                }
                if (aviso.isNotBlank()) Textito(aviso)
            }

            Textito("El tiempo libre también es un bloque planeado: descansar no es fallar.")
        }
    }

    if (creando) {
        DialogoBloque(dia = diaSel) { creando = false }
    }

    if (duplicando) {
        var destinos by remember { mutableStateOf(setOf<Int>()) }
        AlertDialog(
            onDismissRequest = { duplicando = false },
            title = { Text("Duplicar ${dias[diaSel - 1]} a…") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Textito("Los bloques de esos días se reemplazan por los de hoy.")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..7).forEach { dia ->
                            if (dia != diaSel) {
                                Chip(dias[dia - 1], destinos.contains(dia), Modifier.weight(1f)) {
                                    destinos = if (destinos.contains(dia)) destinos - dia
                                    else destinos + dia
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                BotonTexto("Duplicar") {
                    if (destinos.isNotEmpty()) Repo.duplicarDia(diaSel, destinos.toList())
                    duplicando = false
                }
            },
            dismissButton = { BotonTexto("Cancelar") { duplicando = false } }
        )
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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("entreno" to "Entreno", "escuela" to "Escuela", "habito" to "Hábito")
                        .forEach { (clave, texto) ->
                            Chip(texto, tipo == clave, Modifier.weight(1f)) { tipo = clave }
                        }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("libre" to "Libre", "otro" to "Otro").forEach { (clave, texto) ->
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
