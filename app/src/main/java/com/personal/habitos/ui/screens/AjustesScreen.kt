package com.personal.habitos.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personal.habitos.data.Repo
import com.personal.habitos.sistema.Recordatorios
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.components.Widget
import com.personal.habitos.ui.theme.AccentOption

private val claves = listOf(
    Triple("entreno", "Entrenamiento", "Te avisa el día que toca sesión"),
    Triple("revision", "Revisión semanal", "Un aviso para cerrar la semana"),
    Triple("bloques", "Bloques de agenda", "Lo que sigue en tu día")
)

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

            Widget(titulo = "Recordatorios", modifier = Modifier.fillMaxWidth()) {
                claves.forEachIndexed { indice, (clave, titulo, detalle) ->
                    if (indice > 0) Separador()
                    val activo = estado.recordatorios[clave] == true
                    val hora = estado.horas[clave] ?: 8

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(titulo, fontWeight = FontWeight.Bold)
                            Textito(detalle)
                        }
                        Switch(
                            checked = activo,
                            onCheckedChange = { valor ->
                                Repo.cambiarRecordatorio(clave, valor)
                                Recordatorios.programar(context, clave, valor, hora)
                                mensaje = if (valor) "Recordatorio activado." else "Recordatorio apagado."
                            }
                        )
                    }

                    if (activo) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(6, 7, 8, 9, 12, 15, 18, 20, 21).forEach { h ->
                                Chip("$h:00", hora == h) {
                                    Repo.cambiarHora(clave, h)
                                    Recordatorios.programar(context, clave, true, h)
                                    mensaje = "Recordatorio a las $h:00."
                                }
                            }
                        }
                    }
                }
                Textito("Los avisos son aproximados: Android los agrupa para ahorrar batería.")
            }

            Widget(titulo = "Color de la app", modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccentOption.entries.forEachIndexed { indice, opcion ->
                        Box(
                            modifier = Modifier
                                .size(46.dp)
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

            Widget(titulo = "Tema", modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Sistema", "Claro", "Oscuro").forEachIndexed { indice, texto ->
                        Chip(texto, estado.modoTema == indice, Modifier.weight(1f)) {
                            Repo.cambiarModoTema(indice)
                        }
                    }
                }
            }

            Widget(titulo = "Tus datos", modifier = Modifier.fillMaxWidth()) {
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

            Widget(titulo = "Reiniciar", modifier = Modifier.fillMaxWidth()) {
                Textito("Borra todo y vuelve a empezar desde la bienvenida.")
                BotonTexto("Reiniciar la app") { Repo.reiniciar() }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Textito("Los eventos que mandes al calendario aparecen en el calendario del teléfono, y de ahí se sincronizan con tu cuenta de Google.")
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
