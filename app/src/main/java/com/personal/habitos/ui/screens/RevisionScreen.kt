package com.personal.habitos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Revision
import com.personal.habitos.data.Repo
import com.personal.habitos.data.inicioDeSemana
import com.personal.habitos.data.sesionesEnSemana
import com.personal.habitos.data.vecesEnSemana
import com.personal.habitos.ui.components.GlassCard
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
                estado.habitos.take(2).forEach { habito ->
                    GlassCard(modifier = Modifier.weight(1f), spacing = 4.dp) {
                        Text(
                            "${vecesEnSemana(habito.id, estado.marcas)} veces",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Textito(habito.nombre)
                    }
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth(), spacing = 4.dp) {
                Text(
                    "${sesionesEnSemana(estado.sesiones)} entrenamientos",
                    style = MaterialTheme.typography.headlineSmall
                )
                Textito("Se mide cuántas veces, no si fue perfecto.")
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("¿Qué tan automático se siente?", style = MaterialTheme.typography.titleMedium)
                estado.habitos.forEach { habito ->
                    Textito(habito.nombre)
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

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = reflexion,
                    onValueChange = { reflexion = it },
                    label = { Text("¿Qué funcionó y qué costó?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            BotonPrincipal(if (aviso) "Revisión guardada" else "Guardar revisión", Modifier.fillMaxWidth()) {
                Repo.guardarRevision(Revision(semana, automatismo, reflexion))
                aviso = true
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                if (listos && estado.enEspera.isNotEmpty()) {
                    Text("Ya puedes agregar un hábito nuevo", style = MaterialTheme.typography.titleMedium)
                    Textito("Tus hábitos actuales se sienten automáticos. Activa uno de la lista de espera.")
                } else {
                    Textito("Todavía no toca un hábito nuevo. Sigue con estos.")
                }
            }
        }
    }
}
