package com.personal.habitos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.habitos.ui.theme.LocalGlassColors

@Composable
fun Pantalla(
    titulo: String,
    contentPadding: PaddingValues = PaddingValues(bottom = 112.dp),
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null,
    accion: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onVolver != null) {
                    BotonRedondo(onClick = onVolver, descripcion = "Volver") {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                    Box(Modifier.size(12.dp))
                }
                Text(text = titulo, style = MaterialTheme.typography.displaySmall)
            }
            accion?.invoke()
        }
        if (subtitulo != null) Textito(subtitulo)
        content()
    }
}

@Composable
fun Textito(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = LocalGlassColors.current.textMuted,
        modifier = modifier
    )
}

@Composable
fun BotonRedondo(
    onClick: () -> Unit,
    descripcion: String,
    contenido: @Composable () -> Unit
) {
    val glass = LocalGlassColors.current
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(glass.glass)
            .border(1.dp, glass.glassBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        contenido()
    }
}

@Composable
fun BotonPrincipal(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = CircleShape
    ) {
        Text(texto, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Chip(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val glass = LocalGlassColors.current
    val fondo = if (seleccionado) MaterialTheme.colorScheme.primary else glass.glassStrong
    val color = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(fondo)
            .border(1.dp, if (seleccionado) Color.Transparent else glass.glassBorder, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = color, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun Etiqueta(texto: String) {
    val glass = LocalGlassColors.current
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(glass.glassStrong)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(texto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun Campo(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun Separador() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(LocalGlassColors.current.divider)
    )
}

@Composable
fun BotonTexto(texto: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, colors = ButtonDefaults.textButtonColors()) {
        Text(texto, fontWeight = FontWeight.SemiBold)
    }
}
