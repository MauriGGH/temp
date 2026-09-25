package com.personal.habitos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Movimiento
import com.personal.habitos.data.Repo
import com.personal.habitos.data.balanceMes
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.GlassList
import com.personal.habitos.ui.theme.LocalGlassColors
import java.time.LocalDate
import kotlin.math.roundToLong

private val verde = Color(0xFF0B6B4F)
private val rojo = Color(0xFFA3341A)

fun pesos(monto: Double): String = "$" + String.format("%,d", monto.roundToLong())

@Composable
fun FinanzasScreen(contentPadding: PaddingValues) {
    val estado = Repo.estado
    val (ingresos, gastos, balance) = balanceMes(estado.movimientos)
    var creando by remember { mutableStateOf<Boolean?>(null) }

    Pantalla(
        titulo = "Finanzas",
        contentPadding = contentPadding,
        subtitulo = "Ingresos y gastos de este mes."
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Textito("Balance del mes")
                Text(pesos(balance), style = MaterialTheme.typography.displaySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ingresos", color = verde, fontWeight = FontWeight.Bold)
                        Text(pesos(ingresos), style = MaterialTheme.typography.headlineSmall)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Gastos", color = rojo, fontWeight = FontWeight.Bold)
                        Text(pesos(gastos), style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotonPrincipal("Gasto", Modifier.weight(1f)) { creando = false }
                BotonPrincipal("Ingreso", Modifier.weight(1f)) { creando = true }
            }

            Text("Movimientos", style = MaterialTheme.typography.titleMedium)

            if (estado.movimientos.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Textito("Aún no registras movimientos.")
                }
            } else {
                GlassList(modifier = Modifier.fillMaxWidth()) {
                    estado.movimientos.sortedByDescending { it.fecha }
                        .forEachIndexed { indice, movimiento ->
                            if (indice > 0) Separador()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(movimiento.concepto, fontWeight = FontWeight.Bold)
                                    Textito(
                                        LocalDate.ofEpochDay(movimiento.fecha).toString() +
                                            " · " + movimiento.categoria
                                    )
                                }
                                Text(
                                    (if (movimiento.ingreso) "+" else "−") + pesos(movimiento.monto),
                                    color = if (movimiento.ingreso) verde else rojo,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Borrar movimiento",
                                    tint = LocalGlassColors.current.textMuted,
                                    modifier = Modifier.clickable { Repo.borrarMovimiento(movimiento.id) }
                                )
                            }
                        }
                }
            }
        }
    }

    val tipo = creando
    if (tipo != null) {
        DialogoMovimiento(ingreso = tipo, onCerrar = { creando = null })
    }
}

@Composable
private fun DialogoMovimiento(ingreso: Boolean, onCerrar: () -> Unit) {
    var concepto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (ingreso) "Nuevo ingreso" else "Nuevo gasto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Campo(concepto, "Concepto") { concepto = it }
                Campo(categoria, "Categoría") { categoria = it }
                Campo(monto, "Monto") { texto ->
                    monto = texto.filter { it.isDigit() || it == '.' }
                }
            }
        },
        confirmButton = {
            BotonTexto("Guardar") {
                val valor = monto.toDoubleOrNull()
                if (concepto.isNotBlank() && valor != null && valor > 0) {
                    Repo.agregarMovimiento(
                        Movimiento(
                            fecha = LocalDate.now().toEpochDay(),
                            concepto = concepto.trim(),
                            categoria = categoria.ifBlank { "General" },
                            monto = valor,
                            ingreso = ingreso
                        )
                    )
                }
                onCerrar()
            }
        },
        dismissButton = { BotonTexto("Cancelar", onCerrar) }
    )
}
