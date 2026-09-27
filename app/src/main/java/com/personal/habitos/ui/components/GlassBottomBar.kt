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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.personal.habitos.ui.navigation.Destino
import com.personal.habitos.ui.theme.LocalGlassColors

/** Barra fija. El destino activo se llena de color; el central va elevado. */
@Composable
fun GlassBottomBar(
    destinos: List<Destino>,
    actual: Destino,
    onSelect: (Destino) -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = LocalGlassColors.current
    val acento = MaterialTheme.colorScheme.primary
    val shape = CircleShape

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .height(70.dp)
            .shadow(20.dp, shape, spotColor = Color.Black, ambientColor = Color.Black)
            .clip(shape)
            .background(if (glass.isDark) Color(0xF01A1E27) else Color(0xF2FFFFFF))
            .background(Brush.verticalGradient(listOf(glass.glassGlow, Color.Transparent)))
            .border(1.dp, glass.glassBorder, shape)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        destinos.forEach { destino ->
            val activo = destino == actual
            val resaltado = destino.central
            val tamano = if (resaltado) 58.dp else 50.dp

            Box(
                modifier = Modifier
                    .size(tamano)
                    .then(
                        if (resaltado) Modifier.shadow(14.dp, CircleShape, spotColor = acento)
                        else Modifier
                    )
                    .clip(CircleShape)
                    .background(
                        when {
                            activo || resaltado -> Brush.verticalGradient(
                                listOf(acento, acento.copy(alpha = 0.78f))
                            )
                            else -> Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                        }
                    )
                    .then(
                        if (resaltado && !activo) Modifier.border(2.dp, glass.glassBorder, CircleShape)
                        else Modifier
                    )
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
                    tint = if (activo || resaltado) MaterialTheme.colorScheme.onPrimary else glass.textMuted,
                    modifier = Modifier.size(if (resaltado) 28.dp else 24.dp)
                )
            }
        }
    }
}
