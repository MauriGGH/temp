package com.personal.habitos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

/** Destinos de la barra inferior. */
enum class Destino(val ruta: String, val titulo: String, val icono: ImageVector) {
    Hoy("hoy", "Hoy", Icons.Outlined.Home),
    Habitos("habitos", "Hábitos", Icons.Outlined.CheckCircle),
    Entreno("entreno", "Entreno", Icons.Outlined.FitnessCenter),
    Finanzas("finanzas", "Finanzas", Icons.Outlined.AccountBalanceWallet),
    Mas("mas", "Más", Icons.Filled.MoreHoriz);

    companion object {
        fun porRuta(ruta: String?): Destino = entries.firstOrNull { it.ruta == ruta } ?: Hoy
    }
}
