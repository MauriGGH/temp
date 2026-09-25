package com.personal.habitos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.personal.habitos.ui.navigation.Destino
import com.personal.habitos.ui.theme.LocalGlassColors

/** Barra flotante: el destino activo es un círculo del color de acento. */
@Composable
fun GlassBottomBar(
    destinos: List<Destino>,
    actual: Destino,
    onSelect: (Destino) -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = LocalGlassColors.current
    val shape = CircleShape

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(72.dp)
            .clip(shape)
            .background(glass.glassStrong)
            .border(1.dp, glass.glassBorder, shape)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        destinos.forEach { destino ->
            val activo = destino == actual
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (activo) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(destino) }
                    .semantics { contentDescription = destino.titulo },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = destino.icono,
                    contentDescription = null,
                    tint = if (activo) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
