package com.personal.habitos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personal.habitos.ui.components.GlassCard
import com.personal.habitos.ui.theme.LocalGlassColors

@Composable
fun HoyScreen(contentPadding: PaddingValues) {
    Pantalla(titulo = "Hoy", contentPadding = contentPadding) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Hoy toca entrenar",
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalGlassColors.current.textMuted
                )
                Text(text = "Sesión A", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                    Text("Empezar sesión")
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Mis hábitos", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Comer sin pantalla", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Pendiente",
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalGlassColors.current.textMuted
                    )
                }
            }
        }
    }
}
