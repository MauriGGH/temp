package com.personal.habitos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.personal.habitos.data.Repo
import com.personal.habitos.data.Revision
import com.personal.habitos.data.inicioDeSemana
import com.personal.habitos.data.sesionesEnSemana
import com.personal.habitos.data.vecesEnSemana
import com.personal.habitos.ui.components.BarrasSemana
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.components.Widget
import java.time.LocalDate

@Composable
fun RevisionScreen(contentPadding: PaddingValues, onVolver: () -> Unit) {
    val estado = Repo.estado
    val semana = inicioDeSemana().toEpochDay()
    val guardada = Repo.revisionDe(semana)

    var automatismo by remember { mutableStateOf(guardada?.automatismo ?: emptyMap()) }
    var reflexion by remember { mutableStateOf(guardada?.reflexion ?: "") }
    var aviso by remember { mutableStateOf(false) }

    val listos = estado.habitos.isNotEmpty() &&
        estado.habitos.all { (automatismo[it.id] ?: 0) >= 4 }

    Pantalla(
        titulo = "Revisión semanal",
        contentPadding = contentPadding,
        subtitulo = "Semana del " + LocalDate.ofEpochDay(semana),
        onVolver = onVolver
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassCard(modifier = Modifier.weight(1f), contentPadding = 14.dp, spacing = 2.dp) {
                    Textito("ENTRENOS")
                    Text(
                        "${sesionesEnSemana(estado.sesiones)}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Textito("esta semana")
                }
                GlassCard(modifier = Modifier.weight(1f), contentPadding = 14.dp, spacing = 2.dp) {
                    Textito("MARCAS")
                    Text(
                        "${estado.habitos.sumOf { vecesEnSemana(it.id, estado.marcas) }}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Textito("de hábitos")
                }
                GlassCard(modifier = Modifier.weight(1f), contentPadding = 14.dp, spacing = 2.dp) {
                    Textito("PUNTOS")
                    Text("${estado.puntos}", style = MaterialTheme.typography.headlineSmall)
                    Textito("en total")
                }
            }

            Textito("Se mide cuántas veces, no si fue perfecto.")

            if (estado.habitos.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Textito("Sin hábitos activos todavía. Créalos para poder revisarlos aquí.")
                }
            }

            estado.habitos.forEach { habito ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(habito.nombre, fontWeight = FontWeight.Bold)
                        Text(
                            "${vecesEnSemana(habito.id, estado.marcas)} veces",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Textito("¿Qué tan automático se siente?")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..5).forEach { valor ->
                            Chip(
                                valor.toString(),
                                automatismo[habito.id] == valor,
                                Modifier.weight(1f)
                            ) {
                                automatismo = automatismo + (habito.id to valor)
                            }
                        }
                    }
                }
            }

            val semanas = (5 downTo 0).map { atras ->
                val inicio = LocalDate.ofEpochDay(semana).minusWeeks(atras.toLong()).toEpochDay()
                val fin = inicio + 7
                estado.marcas.count { it.fecha in inicio until fin }.toFloat() +
                    estado.sesiones.count { it.fecha in inicio until fin }.toFloat()
            }

            Widget(titulo = "Últimas semanas", modifier = Modifier.fillMaxWidth()) {
                BarrasSemana(
                    valores = semanas,
                    etiquetas = listOf("-5", "-4", "-3", "-2", "-1", "Hoy")
                )
                Textito("Marcas de hábitos y entrenamientos por semana.")
            }

            Widget(titulo = "Tu reflexión", modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = reflexion,
                    onValueChange = { reflexion = it },
                    label = { Text("¿Qué funcionó y qué costó?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            BotonPrincipal(
                if (aviso) "Revisión guardada" else "Guardar revisión",
                Modifier.fillMaxWidth()
            ) {
                Repo.guardarRevision(Revision(semana, automatismo, reflexion))
                aviso = true
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                if (listos && estado.enEspera.isNotEmpty()) {
                    Text(
                        "Ya puedes agregar un hábito nuevo",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Textito("Tus hábitos actuales se sienten automáticos. Elige uno de la lista:")
                    estado.enEspera.forEach { habito ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(habito.nombre)
                            BotonTexto("Activar") { Repo.activarDesdeEspera(habito) }
                        }
                    }
                } else if (listos) {
                    Text("Vas bien", style = MaterialTheme.typography.titleMedium)
                    Textito("Tus hábitos se sienten automáticos. Añade uno nuevo cuando quieras.")
                } else {
                    Textito("Todavía no toca un hábito nuevo. Sigue con estos.")
                }
            }

            if (estado.revisiones.size > 1) {
                Text("Revisiones anteriores", style = MaterialTheme.typography.titleMedium)
                GlassList(modifier = Modifier.fillMaxWidth()) {
                    estado.revisiones.filter { it.semana != semana }
                        .sortedByDescending { it.semana }
                        .take(6)
                        .forEachIndexed { indice, revision ->
                            if (indice > 0) Separador()
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(
                                    "Semana del " + LocalDate.ofEpochDay(revision.semana),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (revision.reflexion.isNotBlank()) Textito(revision.reflexion)
                            }
                        }
                }
            }
        }
    }
}
