package com.personal.habitos.ui.theme

import androidx.compose.ui.graphics.Color

/** Colores de acento que el usuario puede elegir en Ajustes. */
enum class AccentOption(val label: String, val color: Color) {
    NaranjaQuemado("Naranja quemado", Color(0xFFE2622A)),
    Violeta("Violeta", Color(0xFF7A6BF0)),
    VerdeAzulado("Verde azulado", Color(0xFF17A08C)),
    AzulProfundo("Azul profundo", Color(0xFF3B7BE8)),
    RosaOscuro("Rosa oscuro", Color(0xFFD1497B))
}

/** Paleta neutra: el color lo pone el acento, no el fondo. */
object Palette {
    // Claro
    val LightBackground = Color(0xFFEFF0F3)
    val LightBackgroundAlt = Color(0xFFE4E6EB)
    val LightText = Color(0xFF15171C)
    val LightTextMuted = Color(0xFF5A6070)
    val LightGlass = Color(0x99FFFFFF)
    val LightGlassStrong = Color(0xCCFFFFFF)
    val LightGlassBorder = Color(0xE6FFFFFF)
    val LightGlassGlow = Color(0x59FFFFFF)
    val LightDivider = Color(0x141A1F2B)

    // Oscuro
    val DarkBackground = Color(0xFF0D0F13)
    val DarkBackgroundAlt = Color(0xFF15181F)
    val DarkText = Color(0xFFF4F5F8)
    val DarkTextMuted = Color(0xFF9AA1B2)
    val DarkGlass = Color(0x14FFFFFF)
    val DarkGlassStrong = Color(0x24FFFFFF)
    val DarkGlassBorder = Color(0x2EFFFFFF)
    val DarkGlassGlow = Color(0x1FFFFFFF)
    val DarkDivider = Color(0x1AFFFFFF)
}

/** Colores de los rangos. */
object RankColors {
    val Plata = Color(0xFFB6BECC)
    val PlataOscuro = Color(0xFF6D7687)
    val Oro = Color(0xFFF0B429)
    val OroOscuro = Color(0xFF9C6F06)
    val Platino = Color(0xFF34C6E8)
    val PlatinoOscuro = Color(0xFF16708C)
    val Diamante = Color(0xFFC07BF5)
    val DiamanteOscuro = Color(0xFF6B2FA8)
    val Ascendente = Color(0xFF2FD68A)
    val AscendenteOscuro = Color(0xFF12704A)
    val Inmortal = Color(0xFFF2456B)
    val InmortalOscuro = Color(0xFF8E1130)
    val Radiante = Color(0xFFFFE08A)
    val RadianteOscuro = Color(0xFFC79A17)
}
