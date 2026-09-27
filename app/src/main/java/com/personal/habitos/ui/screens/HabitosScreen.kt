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
import androidx.compose.material.icons.filled.Edit
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
import com.personal.habitos.data.Habito
import com.personal.habitos.data.Repo
import com.personal.habitos.data.etapaFuerza
import com.personal.habitos.data.fuerzaHabito
import com.personal.habitos.data.hechoEn
import com.personal.habitos.data.marcasDe
import com.personal.habitos.data.vecesEnSemana
import com.personal.habitos.ui.components.AnilloProgreso
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.components.PuntoDia
import com.personal.habitos.ui.theme.LocalGlassColors

private val diasCortos = listOf("L", "M", "M", "J", "V", "S", "D")

@Composable
fun HabitosScreen(contentPadding: PaddingValues) {
    val estado = Repo.estado
    var creando by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Habito?>(null) }
    var borrando by remember { mutableStateOf<Habito?>(null) }

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

            if (estado.habitos.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Aún no tienes hábitos activos", style = MaterialTheme.typography.titleMedium)
                    Textito("Empieza con uno pequeño. Puedes cambiarlo cuando quieras.")
                    BotonPrincipal("Crear mi primer hábito", Modifier.fillMaxWidth()) { creando = true }
                }
            }

            estado.habitos.forEach { habito ->
                TarjetaHabito(
                    habito = habito,
                    onEditar = { editando = habito },
                    onBorrar = { borrando = habito },
                    onAEspera = { Repo.aEspera(habito) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lista de espera", style = MaterialTheme.typography.titleMedium)
                BotonTexto("Añadir") { creando = true }
            }

            if (estado.enEspera.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Textito("Aquí esperan los hábitos que quieres agregar más adelante.")
                }
            } else {
                GlassList(modifier = Modifier.fillMaxWidth()) {
                    estado.enEspera.forEachIndexed { indice, habito ->
                        if (indice > 0) Separador()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(habito.nombre, fontWeight = FontWeight.Bold)
                                if (habito.ancla.isNotBlank()) Textito(habito.ancla)
                            }
                            BotonTexto("Activar") { Repo.activarDesdeEspera(habito) }
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar ${habito.nombre}",
                                tint = LocalGlassColors.current.textMuted,
                                modifier = Modifier.clickable { editando = habito }
                            )
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Borrar ${habito.nombre}",
                                tint = LocalGlassColors.current.textMuted,
                                modifier = Modifier.clickable { borrando = habito }
                            )
                        }
                    }
                }
                Textito("Se abren cuando tus hábitos actuales se sientan automáticos.")
            }
        }
    }

    if (creando) {
        DialogoHabito(
            inicial = null,
            puedeActivar = estado.habitos.size < 3,
            onCerrar = { creando = false }
        )
    }

    val aEditar = editando
    if (aEditar != null) {
        DialogoHabito(
            inicial = aEditar,
            puedeActivar = true,
            onCerrar = { editando = null }
        )
    }

    val aBorrar = borrando
    if (aBorrar != null) {
        AlertDialog(
            onDismissRequest = { borrando = null },
            title = { Text("¿Borrar ${aBorrar.nombre}?") },
            text = { Textito("También se borra su historial de marcas. Esto no se puede deshacer.") },
            confirmButton = {
                BotonTexto("Borrar") {
                    Repo.borrarHabito(aBorrar)
                    borrando = null
                }
            },
            dismissButton = { BotonTexto("Cancelar") { borrando = null } }
        )
    }
}

@Composable
private fun TarjetaHabito(
    habito: Habito,
    onEditar: () -> Unit,
    onBorrar: () -> Unit,
    onAEspera: () -> Unit
) {
    val fuerza = fuerzaHabito(habito, Repo.marcasDe(habito))
    val glass = LocalGlassColors.current
    val veces = vecesEnSemana(habito.id, Repo.estado.marcas)

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AnilloProgreso(progreso = fuerza, tamano = 56.dp, grosor = 7.dp) {
                Text(
                    "${(fuerza * 100).toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(habito.nombre, fontWeight = FontWeight.Bold, maxLines = 2)
                Textito(etapaFuerza(fuerza))
                if (habito.ancla.isNotBlank()) Textito(habito.ancla)
            }
            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Editar ${habito.nombre}",
                    tint = glass.textMuted,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onEditar() }
                )
                Box(Modifier.size(14.dp))
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Borrar ${habito.nombre}",
                    tint = glass.textMuted,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onBorrar() }
                )
            }
        }

        Separador()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (1..7).forEach { dia ->
                PuntoDia(
                    hecho = Repo.hechoEn(habito, dia),
                    aplica = habito.dias.contains(dia),
                    etiqueta = diasCortos[dia - 1]
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Etiqueta("$veces esta semana")
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                BotonTexto("↑") { Repo.moverHabito(habito, true) }
                BotonTexto("↓") { Repo.moverHabito(habito, false) }
                BotonTexto("Pausar") { onAEspera() }
            }
        }
    }
}

@Composable
private fun DialogoHabito(
    inicial: Habito?,
    puedeActivar: Boolean,
    onCerrar: () -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var ancla by remember { mutableStateOf(inicial?.ancla ?: "") }
    var dias by remember { mutableStateOf(inicial?.dias ?: setOf(1, 2, 3, 4, 5, 6, 7)) }
    var activar by remember { mutableStateOf(puedeActivar) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (inicial == null) "Nuevo hábito" else "Editar hábito") },
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
                if (inicial == null) {
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
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                if (nombre.isNotBlank()) {
                    val diasFinales = dias.ifEmpty { setOf(1, 2, 3, 4, 5, 6, 7) }
                    if (inicial == null) {
                        Repo.agregarHabito(
                            Habito(nombre = nombre.trim(), ancla = ancla.trim(), dias = diasFinales),
                            activar && puedeActivar
                        )
                    } else {
                        Repo.editarHabito(
                            inicial.copy(
                                nombre = nombre.trim(),
                                ancla = ancla.trim(),
                                dias = diasFinales
                            )
                        )
                    }
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}
