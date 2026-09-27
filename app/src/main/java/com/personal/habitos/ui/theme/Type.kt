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
/*
 * Para usar Plus Jakarta Sans:
 * 1. Baja los .ttf de Google Fonts.
 * 2. Cópialos a app/src/main/res/font con nombres en minúsculas y guiones bajos.
 * 3. Descomenta el bloque de abajo y borra la línea de FontFamily.SansSerif.
 *
 * private val appFontFamily = FontFamily(
 *     Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
 *     Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
 *     Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
 *     Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
 *     Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
 * )
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
        fontSize = 22.sp,
        letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontFamily = appFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp
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
        fontSize = 12.sp,
        letterSpacing = 0.3.sp
    )
)
