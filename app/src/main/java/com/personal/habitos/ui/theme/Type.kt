package com.personal.habitos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tipografía del sistema por ahora. Para usar Plus Jakarta Sans:
 * copia los .ttf a app/src/main/res/font y cambia appFontFamily.
 */
private val appFontFamily = FontFamily.SansSerif

val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    ),
    labelSmall = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    )
)
