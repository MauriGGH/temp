package com.personal.habitos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Habito
import com.personal.habitos.data.Repo
import com.personal.habitos.ui.components.GlassBackground
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.theme.LocalGlassColors

private val diasCortosBienvenida = listOf("L", "M", "M", "J", "V", "S", "D")

@Composable
fun BienvenidaScreen() {
    var paso by remember { mutableStateOf(0) }
    var nombre1 by remember { mutableStateOf("Comer sin pantalla") }
    var ancla1 by remember { mutableStateOf("En cada comida en casa") }
    var nombre2 by remember { mutableStateOf("Entrenamiento") }
    var ancla2 by remember { mutableStateOf("Después de lavarme los dientes") }
    var dias2 by remember { mutableStateOf(setOf(1, 3, 5)) }

    GlassBackground {
        Pantalla(
            titulo = if (paso == 0) "Bienvenido" else "Empecemos con poco",
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (0..1).forEach { indice ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (paso == indice) 36.dp else 22.dp)
                                .clip(CircleShape)
                                .background(
                                    if (paso == indice) MaterialTheme.colorScheme.primary
                                    else LocalGlassColors.current.divider
                                )
                        )
                    }
                }

                if (paso == 0) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Cómo funciona", style = MaterialTheme.typography.titleMedium)
                        Textito("Aquí no hay rachas que se reinicien ni mensajes de culpa.")
                        Textito("Se mide cuántas veces lo logras, no si fue perfecto.")
                        Textito("Trabajas pocos hábitos a la vez; los demás esperan su turno.")
                        Textito("Todo se guarda solo en este teléfono.")
                    }
                    BotonPrincipal("Continuar", Modifier.fillMaxWidth()) { paso = 1 }
                } else {
                    Textito("Elige tus primeros hábitos. Puedes cambiarlos cuando quieras.")

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Campo(nombre1, "Hábito") { nombre1 = it }
                        Campo(ancla1, "Cuándo o después de qué") { ancla1 = it }
                    }

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Campo(nombre2, "Hábito") { nombre2 = it }
                        Campo(ancla2, "Cuándo o después de qué") { ancla2 = it }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..7).forEach { dia ->
                                Chip(
                                    diasCortosBienvenida[dia - 1],
                                    dias2.contains(dia),
                                    Modifier.weight(1f)
                                ) {
                                    dias2 = if (dias2.contains(dia)) dias2 - dia else dias2 + dia
                                }
                            }
                        }
                    }

                    BotonPrincipal("Empezar", Modifier.fillMaxWidth()) {
                        val lista = mutableListOf<Habito>()
                        if (nombre1.isNotBlank()) {
                            lista.add(Habito(nombre = nombre1.trim(), ancla = ancla1.trim()))
                        }
                        if (nombre2.isNotBlank()) {
                            lista.add(
                                Habito(
                                    nombre = nombre2.trim(),
                                    ancla = ancla2.trim(),
                                    dias = dias2.ifEmpty { setOf(1, 3, 5) }
                                )
                            )
                        }
                        Repo.terminarBienvenida(lista)
                    }
                    BotonTexto("Empezar sin hábitos") { Repo.terminarBienvenida(emptyList()) }
                    Box(Modifier.size(8.dp))
                }
            }
        }
    }
}
