package com.personal.habitos.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.personal.habitos.ui.components.GlassCard

@Composable
fun FinanzasScreen(contentPadding: PaddingValues) {
    Pantalla(titulo = "Finanzas", contentPadding = contentPadding) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "En construcción",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
