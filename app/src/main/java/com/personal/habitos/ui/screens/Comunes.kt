package com.personal.habitos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personal.habitos.ui.theme.LocalGlassColors

/** Estructura común de las pantallas: título grande y contenido con scroll. */
@Composable
fun Pantalla(
    titulo: String,
    contentPadding: PaddingValues,
    subtitulo: String? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = titulo, style = MaterialTheme.typography.displaySmall)
        if (subtitulo != null) {
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalGlassColors.current.textMuted
            )
        }
        content()
    }
}
