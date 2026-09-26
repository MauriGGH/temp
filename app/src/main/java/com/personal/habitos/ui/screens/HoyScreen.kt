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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Habito
import com.personal.habitos.data.Repo
import com.personal.habitos.data.nivelDe
import com.personal.habitos.data.rangoDe
import com.personal.habitos.data.inicioDeSemana
import com.personal.habitos.data.siguienteSesion
import com.personal.habitos.data.temasDeHoy
import com.personal.habitos.ui.components.AnilloProgreso
import com.personal.habitos.ui.components.BarrasSemana
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.InsigniaRango
import com.personal.habitos.ui.components.Widget
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate
import java.time.format.TextStyle as EstiloTexto
import java.util.Locale

private val etiquetasDias = listOf("L", "M", "M", "J", "V", "S", "D")

@Composable
fun HoyScreen(
    contentPadding: PaddingValues,
    irAEntreno: () -> Unit,
    irAEstudio: () -> Unit,
    irANotas: () -> Unit,
    irAAjustes: () -> Unit
) {
    val estado = Repo.estado
    val hoy = LocalDate.now()
    val diaSemana = hoy.dayOfWeek.value
    val lunes = inicioDeSemana(hoy)
    val sesion = siguienteSesion(estado.sesiones)
    val pendientes = temasDeHoy(estado.temas, hoy)
    val rango = rangoDe(estado.puntos)

    val deHoy = estado.habitos.filter { it.dias.contains(diaSemana) }
    val hechosHoy = deHoy.count { Repo.hechoHoy(it) }
    val progresoHoy = if (deHoy.isEmpty()) 0f else hechosHoy.toFloat() / deHoy.size

    val barras = (0..6).map { indice ->
        val dia = lunes.plusDays(indice.toLong())
        val marcas = estado.marcas.count { it.fecha == dia.toEpochDay() }.toFloat()
        val entrenos = estado.sesiones.count { it.fecha == dia.toEpochDay() }.toFloat()
        marcas + entrenos
    }

    val fecha = hoy.dayOfWeek.getDisplayName(EstiloTexto.FULL, Locale("es"))
        .replaceFirstChar { it.uppercase() } + " " + hoy.dayOfMonth + " de " +
        hoy.month.getDisplayName(EstiloTexto.FULL, Locale("es"))

    Pantalla(
        titulo = "Hoy",
        contentPadding = contentPadding,
        subtitulo = fecha,
        accion = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonRedondo(onClick = irANotas, descripcion = "Notas") {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                }
                BotonRedondo(onClick = irAAjustes, descripcion = "Ajustes") {
                    Icon(Icons.Filled.Settings, contentDescription = null)
                }
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            // Fila de dos widgets: progreso del día y rango
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Widget(titulo = "Hoy", modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnilloProgreso(progreso = progresoHoy, tamano = 92.dp) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "$hechosHoy/${deHoy.size}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    "hábitos",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LocalGlassColors.current.textMuted
                                )
                            }
                        }
                    }
                }

                Widget(titulo = "Rango", modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InsigniaRango(rango.nombre, tamano = 62.dp)
                        Text(rango.nombre, fontWeight = FontWeight.Bold)
                        Text(
                            "Nivel ${nivelDe(estado.puntos)} · ${estado.puntos} pts",
                            style = MaterialTheme.typography.labelSmall,
                            color = LocalGlassColors.current.textMuted
                        )
                    }
                }
            }

            // Sesión de entrenamiento
            Widget(
                titulo = "Entrenamiento",
                modifier = Modifier.fillMaxWidth(),
                accion = {
                    if (estado.sesiones.isNotEmpty()) {
                        Etiqueta("La última fue ${if (sesion == "A") "B" else "A"}")
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sesión $sesion", style = MaterialTheme.typography.headlineSmall)
                        Textito(
                            estado.plantillas.firstOrNull { it.nombre == sesion }
                                ?.ejercicios?.take(3)?.joinToString(", ") { it.nombre } ?: ""
                        )
                    }
                }
                BotonPrincipal("Empezar sesión", Modifier.fillMaxWidth()) { irAEntreno() }
            }

            // Actividad de la semana
            Widget(titulo = "Tu semana", modifier = Modifier.fillMaxWidth()) {
                BarrasSemana(valores = barras, etiquetas = etiquetasDias)
                Textito("Hábitos marcados y entrenamientos de cada día.")
            }

            // Hábitos del día
            Widget(titulo = "Mis hábitos", modifier = Modifier.fillMaxWidth()) {
                if (estado.habitos.isEmpty()) {
                    Textito("Todavía no tienes hábitos activos. Créalos en la sección Hábitos.")
                } else {
                    estado.habitos.forEachIndexed { indice, habito ->
                        if (indice > 0) Separador()
                        FilaHabito(habito, aplicaHoy = habito.dias.contains(diaSemana))
                    }
                }
            }

            if (pendientes.isNotEmpty()) {
                Widget(titulo = "Repaso de hoy", modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "${pendientes.size} ${if (pendientes.size == 1) "tema" else "temas"}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Textito(pendientes.joinToString(" · ") { it.nombre })
                    BotonPrincipal("Repasar", Modifier.fillMaxWidth()) { irAEstudio() }
                }
            }
        }
    }
}

@Composable
private fun FilaHabito(habito: Habito, aplicaHoy: Boolean) {
    val hecho = Repo.hechoHoy(habito)
    val glass = LocalGlassColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(habito.nombre, fontWeight = FontWeight.Bold, maxLines = 1)
            Textito(
                when {
                    !aplicaHoy -> "Hoy no toca"
                    habito.ancla.isNotBlank() -> habito.ancla
                    else -> "Cuando puedas"
                }
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (hecho) MaterialTheme.colorScheme.primary else Color.Transparent)
                .border(
                    if (hecho) 0.dp else 1.5.dp,
                    if (hecho) Color.Transparent else glass.glassBorder,
                    CircleShape
                )
                .clickable { Repo.marcarHabito(habito) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Marcar ${habito.nombre}",
                tint = if (hecho) MaterialTheme.colorScheme.onPrimary else glass.textMuted
            )
        }
    }
}
