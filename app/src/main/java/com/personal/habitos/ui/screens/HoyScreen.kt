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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.personal.habitos.data.siguienteSesion
import com.personal.habitos.data.temasDeHoy
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate
import java.time.format.TextStyle as EstiloTexto
import java.util.Locale

@Composable
fun HoyScreen(
    contentPadding: PaddingValues,
    irAEntreno: () -> Unit,
    irAEstudio: () -> Unit
) {
    val estado = Repo.estado
    val hoy = LocalDate.now()
    val diaSemana = hoy.dayOfWeek.value
    val sesion = siguienteSesion(estado.sesiones)
    val pendientes = temasDeHoy(estado.temas, hoy)
    val fecha = hoy.dayOfWeek.getDisplayName(EstiloTexto.FULL, Locale("es")) +
        ", " + hoy.dayOfMonth + " de " +
        hoy.month.getDisplayName(EstiloTexto.FULL, Locale("es"))

    Pantalla(titulo = "Hoy", contentPadding = contentPadding, subtitulo = fecha) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Textito("Hoy toca entrenar")
                Text("Sesión $sesion", style = MaterialTheme.typography.headlineSmall)
                Textito(
                    estado.plantillas.firstOrNull { it.nombre == sesion }
                        ?.ejercicios?.take(3)?.joinToString(", ") { it.nombre } ?: ""
                )
                BotonPrincipal("Empezar sesión", Modifier.fillMaxWidth()) { irAEntreno() }
            }

            Text("Mis hábitos", style = MaterialTheme.typography.titleMedium)

            if (estado.habitos.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Textito("Todavía no tienes hábitos activos. Créalos en la sección Hábitos.")
                }
            } else {
                GlassList(modifier = Modifier.fillMaxWidth()) {
                    estado.habitos.forEachIndexed { indice, habito ->
                        if (indice > 0) Separador()
                        FilaHabito(habito, aplicaHoy = habito.dias.contains(diaSemana))
                    }
                }
            }

            if (pendientes.isNotEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Textito("Repaso de hoy")
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
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(habito.nombre, fontWeight = FontWeight.Bold)
            Textito(if (aplicaHoy) habito.ancla else "Hoy no toca")
        }
        Box(
            modifier = Modifier
                .size(48.dp)
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
