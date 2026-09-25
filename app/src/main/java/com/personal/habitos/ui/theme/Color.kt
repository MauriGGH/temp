package com.personal.habitos.ui.theme

import androidx.compose.ui.graphics.Color

/** Colores de acento que el usuario puede elegir en Ajustes. */
enum class AccentOption(val label: String, val color: Color) {
    NaranjaQuemado("Naranja quemado", Color(0xFFC84B14)),
    Violeta("Violeta", Color(0xFF5B4BDB)),
    VerdeAzulado("Verde azulado", Color(0xFF0B7A6B)),
    AzulProfundo("Azul profundo", Color(0xFF2456B8)),
    RosaOscuro("Rosa oscuro", Color(0xFFB03060))
}

/** Paleta base del estilo "vidrio de niebla". */
object Palette {
    val LightBackground = Color(0xFFE9EBF0)
    val LightText = Color(0xFF1C1F2A)
    val LightTextMuted = Color(0xFF525868)
    val LightGlass = Color(0x8CFFFFFF)
    val LightGlassStrong = Color(0xB3FFFFFF)
    val LightGlassBorder = Color(0xD9FFFFFF)
    val LightDivider = Color(0x121C1F2A)

    val BlobLavender = Color(0xFFB9C0E8)
    val BlobPeach = Color(0xFFF4C9B0)
    val BlobBlue = Color(0xFFC7D6F2)

    val DarkBackground = Color(0xFF12141B)
    val DarkText = Color(0xFFF2F3F7)
    val DarkTextMuted = Color(0xFFA9AEBD)
    val DarkGlass = Color(0x12FFFFFF)
    val DarkGlassStrong = Color(0x1FFFFFFF)
    val DarkGlassBorder = Color(0x24FFFFFF)
    val DarkDivider = Color(0x14FFFFFF)

    val DarkBlobIndigo = Color(0xFF3B3F85)
    val DarkBlobRust = Color(0xFF7A3A22)
    val DarkBlobBlue = Color(0xFF1F3F6E)
}

/** Colores de los rangos de la sección de retos (fase posterior). */
object RankColors {
    val Plata = Color(0xFF7C8698)
    val Oro = Color(0xFFD9A118)
    val Platino = Color(0xFF2F7FD1)
    val Diamante = Color(0xFF7B3FD4)
    val Ascendente = Color(0xFF14875A)
    val Inmortal = Color(0xFFC0392B)
    val Radiante = Color(0xFFA67C08)
}
