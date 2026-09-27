package com.personal.habitos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Barra inferior. Retos va al centro y se dibuja elevado,
 * porque es la sección que resume tu avance.
 */
enum class Destino(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector,
    val central: Boolean = false
) {
    Hoy("hoy", "Hoy", Icons.Outlined.Home),
    Habitos("habitos", "Hábitos", Icons.Outlined.CheckCircle),
    Retos("retos", "Retos", Icons.Outlined.EmojiEvents, central = true),
    Entreno("entreno", "Entreno", Icons.Outlined.FitnessCenter),
    Mas("mas", "Más", Icons.Filled.MoreHoriz);

    companion object {
        fun porRuta(ruta: String?): Destino = entries.firstOrNull { it.ruta == ruta } ?: Hoy
    }
}
