package com.alejandro.appgym.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Tema oscuro cálido. Los tonos llevan algo de amarillo para que la app no se sienta
 * clínica: la jerarquía se hace con estos cuatro escalones de superficie, nunca con sombras.
 */

/* ---- Superficies ---- */
val Ink = Color(0xFF171511)          // fondo de la app
val Surface1 = Color(0xFF211E18)     // tarjeta
val Surface2 = Color(0xFF2A2620)     // tarjeta elevada, input dentro de tarjeta
val Control = Color(0xFF23211A)      // chips inactivos, botones secundarios, iconos circulares
val Outline = Color(0xFF322D25)      // borde sutil, solo donde es imprescindible
val OutlineSoft = Color(0xFF23211A)  // separador de la barra inferior

/* ---- Texto ---- */
val TextPrimary = Color(0xFFF4F0E6)
val TextSecondary = Color(0xFFB3AC9D)
val TextTertiary = Color(0xFF7E7869)
val TextOnSoftAccent = Color(0xFFD8D2C4)

/* ---- Acento ---- */
val Volt = Color(0xFFCBF24C)
val OnVolt = Color(0xFF191D07)
val Danger = Color(0xFFFF6B78)

/** Fondo de badge lima y borde de tarjeta destacada. */
val VoltSoft = Volt.copy(alpha = 0.14f)
val VoltBorder = Volt.copy(alpha = 0.30f)
val VoltBorderStrong = Volt.copy(alpha = 0.40f)

/** Degradado de la tarjeta de plan activo. */
val PlanGradientStart = Color(0xFF2A2E17)
val PlanGradientEnd = Surface1

/* ---- Grupos musculares (suavizados: identifican, no decoran) ---- */
val GroupChest = Color(0xFFEFA588)
val GroupBack = Color(0xFF92BFE0)
val GroupShoulder = Color(0xFFB7A5E6)
val GroupBiceps = Color(0xFFE9C083)
val GroupTriceps = Color(0xFF8FD9BE)
val GroupLegs = Color(0xFFE795B1)
val GroupTraps = Color(0xFF9FB0DE)
val GroupCore = Color(0xFFE9C083)
val GroupCardio = Color(0xFFEF8F9E)

fun groupColor(group: String): Color = when (group) {
    "Pecho" -> GroupChest
    "Espalda" -> GroupBack
    "Hombro" -> GroupShoulder
    "Bíceps" -> GroupBiceps
    "Tríceps" -> GroupTriceps
    "Pierna" -> GroupLegs
    "Trapecio" -> GroupTraps
    "Core" -> GroupCore
    "Cardio" -> GroupCardio
    else -> TextSecondary
}

/** Rampa de las barras del gráfico semanal: de apagado a lima en la semana actual. */
val WeekRamp = listOf(
    Color(0xFF38391F),
    Color(0xFF43451F),
    Color(0xFF5A611F),
    Color(0xFF7E8B23),
    Color(0xFFA3BC33),
    Volt
)

val AccentPalette = listOf(Volt, GroupChest, GroupBack, GroupShoulder, GroupBiceps, GroupTriceps, GroupLegs, GroupCardio)

fun accentColor(index: Int): Color =
    AccentPalette[((index % AccentPalette.size) + AccentPalette.size) % AccentPalette.size]
