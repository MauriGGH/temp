package com.personal.habitos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import com.personal.habitos.data.Habito
import com.personal.habitos.data.Repo
import com.personal.habitos.data.etapaFuerza
import com.personal.habitos.data.fuerzaHabito
import com.personal.habitos.data.hechoEn
import com.personal.habitos.data.marcasDe
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.theme.LocalGlassColors

private val diasCortos = listOf("L", "M", "M", "J", "V", "S", "D")

@Composable
fun HabitosScreen(contentPadding: PaddingValues) {
    val estado = Repo.estado
    var creando by remember { mutableStateOf(false) }

    Pantalla(
        titulo = "Hábitos",
        contentPadding = contentPadding,
        subtitulo = "Uno a la vez. Lo nuevo entra cuando lo actual ya cuesta poco.",
        accion = {
            BotonRedondo(onClick = { creando = true }, descripcion = "Nuevo hábito") {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            estado.habitos.forEach { habito ->
                TarjetaHabito(habito)
            }

            if (estado.enEspera.isNotEmpty()) {
                Text("Lista de espera", style = MaterialTheme.typography.titleMedium)
                GlassList(modifier = Modifier.fillMaxWidth()) {
                    estado.enEspera.forEachIndexed { indice, habito ->
                        if (indice > 0) Separador()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(habito.nombre, modifier = Modifier.weight(1f))
                            BotonTexto("Activar") { Repo.activarDesdeEspera(habito) }
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Borrar ${habito.nombre}",
                                tint = LocalGlassColors.current.textMuted,
                                modifier = Modifier.clickable { Repo.borrarHabito(habito) }
                            )
                        }
                    }
                }
                Textito("Se abren cuando tus hábitos actuales se sientan automáticos.")
            }
        }
    }

    if (creando) {
        DialogoNuevoHabito(
            puedeActivar = estado.habitos.size < 3,
            onCerrar = { creando = false }
        )
    }
}

@Composable
private fun TarjetaHabito(habito: Habito) {
    val fuerza = fuerzaHabito(habito, Repo.marcasDe(habito))
    val glass = LocalGlassColors.current

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { fuerza },
                    modifier = Modifier.size(52.dp),
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = glass.divider
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(habito.nombre, fontWeight = FontWeight.Bold)
                Textito(etapaFuerza(fuerza) + if (habito.ancla.isNotBlank()) " · ${habito.ancla}" else "")
            }
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Borrar ${habito.nombre}",
                tint = glass.textMuted,
                modifier = Modifier.clickable { Repo.borrarHabito(habito) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (1..7).forEach { dia ->
                val aplica = habito.dias.contains(dia)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val hecho = Repo.hechoEn(habito, dia)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .then(Modifier)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { if (hecho) 1f else 0f },
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 5.dp,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = if (aplica) glass.divider else glass.glass
                        )
                    }
                    Textito(diasCortos[dia - 1])
                }
            }
        }
    }
}

@Composable
private fun DialogoNuevoHabito(puedeActivar: Boolean, onCerrar: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var ancla by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf(setOf(1, 2, 3, 4, 5, 6, 7)) }
    var activar by remember { mutableStateOf(puedeActivar) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nuevo hábito") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(nombre, "Nombre") { nombre = it }
                Campo(ancla, "Después de…") { ancla = it }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..7).forEach { dia ->
                        Chip(
                            texto = diasCortos[dia - 1],
                            seleccionado = dias.contains(dia),
                            modifier = Modifier.weight(1f)
                        ) {
                            dias = if (dias.contains(dia)) dias - dia else dias + dia
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip("Activo ahora", activar && puedeActivar, Modifier.weight(1f)) {
                        activar = puedeActivar
                    }
                    Chip("A la lista", !activar || !puedeActivar, Modifier.weight(1f)) {
                        activar = false
                    }
                }
                if (!puedeActivar) {
                    Textito("Ya tienes tres hábitos activos. Este entra a la lista de espera.")
                }
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                if (nombre.isNotBlank()) {
                    Repo.agregarHabito(
                        Habito(nombre = nombre.trim(), ancla = ancla.trim(), dias = dias),
                        activar && puedeActivar
                    )
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}
