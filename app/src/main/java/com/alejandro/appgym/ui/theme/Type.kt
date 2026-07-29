package com.alejandro.appgym.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alejandro.appgym.R
import com.alejandro.appgym.ui.components.Badge

/** Outfit (SIL Open Font License). La licencia está en assets/OFL-Outfit.txt */
val Outfit = FontFamily(
    Font(R.font.outfit_regular, FontWeight.Normal),
    Font(R.font.outfit_medium, FontWeight.Medium),
    Font(R.font.outfit_semibold, FontWeight.SemiBold),
    Font(R.font.outfit_bold, FontWeight.Bold)
)

/*
 * Escala pensada para leerse de un vistazo con el móvil en la mano a media serie.
 * Se acabaron los overline en mayúsculas: los títulos de sección son texto normal
 * a 17sp. Las mayúsculas quedan solo para los badges (labelSmall).
 */
val AppTypography = Typography(

    /** Cifra grande: peso corporal. */
    displayMedium = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.6).sp
    ),

    /** Título de pantalla. */
    headlineLarge = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.4).sp
    ),

    /** Título de cabecera secundaria (Tu plan). */
    headlineMedium = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.3).sp
    ),

    /** Título de tarjeta destacada. */
    headlineSmall = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.2).sp
    ),

    /** Título de sección. Sustituye a los overline en mayúsculas. */
    titleLarge = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp, lineHeight = 22.sp
    ),

    /** Título de fila o tarjeta. */
    titleMedium = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 15.5.sp, lineHeight = 20.sp
    ),

    /** Cifra de stat. */
    titleSmall = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 24.sp, letterSpacing = (-0.3).sp
    ),

    bodyLarge = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.Normal,
        fontSize = 14.5.sp, lineHeight = 22.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp, lineHeight = 19.sp
    ),

    /** Texto de botón. */
    labelLarge = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 14.5.sp, lineHeight = 19.sp
    ),

    labelMedium = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 17.sp
    ),

    /** Badge. Único sitio con mayúsculas: PLAN ACTIVO, FASE ACTUAL, SERIE 3. */
    labelSmall = TextStyle(
        fontFamily = Outfit, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 15.sp, letterSpacing = 0.4.sp
    )
)
