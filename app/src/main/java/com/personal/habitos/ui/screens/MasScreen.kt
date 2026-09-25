package com.personal.habitos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Repo
import com.personal.habitos.data.nivelDe
import com.personal.habitos.data.rangoDe
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.theme.LocalGlassColors

@Composable
fun MasScreen(
    contentPadding: PaddingValues,
    irA: (String) -> Unit
) {
    val estado = Repo.estado
    val rango = rangoDe(estado.puntos)

    Pantalla(titulo = "Más", contentPadding = contentPadding) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            GlassCard(modifier = Modifier.fillMaxWidth(), spacing = 4.dp) {
                Text("Nivel ${nivelDe(estado.puntos)} · ${rango.nombre}", fontWeight = FontWeight.Bold)
                Textito("${estado.puntos} puntos acumulados")
            }

            GlassList(modifier = Modifier.fillMaxWidth()) {
                val opciones = listOf(
                    "retos" to "Retos y recompensas",
                    "estudio" to "Estudio",
                    "agenda" to "Agenda",
                    "revision" to "Revisión semanal",
                    "notas" to "Notas",
                    "ajustes" to "Ajustes"
                )
                opciones.forEachIndexed { indice, (ruta, titulo) ->
                    if (indice > 0) Separador()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { irA(ruta) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(titulo, fontWeight = FontWeight.Bold)
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = LocalGlassColors.current.textMuted
                        )
                    }
                }
            }
        }
    }
}
