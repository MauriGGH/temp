package com.personal.habitos.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Repo
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.theme.AccentOption

@Composable
fun AjustesScreen(contentPadding: PaddingValues, onVolver: () -> Unit) {
    val estado = Repo.estado
    val context = LocalContext.current
    var importando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    Pantalla(
        titulo = "Ajustes",
        contentPadding = contentPadding,
        subtitulo = "Todo se guarda solo en este teléfono.",
        onVolver = onVolver
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Color de la app", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccentOption.entries.forEachIndexed { indice, opcion ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(opcion.color)
                                .border(
                                    if (estado.acento == indice) 3.dp else 0.dp,
                                    MaterialTheme.colorScheme.onBackground,
                                    CircleShape
                                )
                                .clickable { Repo.cambiarAcento(indice) }
                        )
                    }
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Tema", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Sistema", "Claro", "Oscuro").forEachIndexed { indice, texto ->
                        Chip(texto, estado.modoTema == indice, Modifier.weight(1f)) {
                            Repo.cambiarModoTema(indice)
                        }
                    }
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Tus datos", style = MaterialTheme.typography.titleMedium)
                BotonPrincipal("Copiar respaldo (JSON)", Modifier.fillMaxWidth()) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("habitos", Repo.exportar()))
                    mensaje = "Respaldo copiado al portapapeles."
                }
                BotonPrincipal("Compartir respaldo", Modifier.fillMaxWidth()) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, Repo.exportar())
                    }
                    context.startActivity(Intent.createChooser(intent, "Compartir respaldo"))
                }
                BotonPrincipal("Importar respaldo", Modifier.fillMaxWidth()) { importando = true }
                if (mensaje.isNotBlank()) Textito(mensaje)
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Reiniciar", style = MaterialTheme.typography.titleMedium)
                Textito("Borra todo y vuelve a los datos de ejemplo.")
                BotonTexto("Reiniciar la app") { Repo.reiniciar() }
            }
        }
    }

    if (importando) {
        var texto by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { importando = false },
            title = { Text("Importar respaldo") },
            text = {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Pega aquí el JSON") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            },
            confirmButton = {
                BotonTexto("Importar") {
                    mensaje = if (Repo.importar(texto)) "Respaldo importado."
                    else "No se pudo leer ese respaldo."
                    importando = false
                }
            },
            dismissButton = { BotonTexto("Cancelar") { importando = false } }
        )
    }
}
