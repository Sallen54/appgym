package com.alejandro.appgym.util

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

val ES: Locale = Locale.forLanguageTag("es-ES")

/** 72.5 -> "72,5"   72.0 -> "72" */
fun Double.weightText(): String =
    if (abs(this - this.roundToInt()) < 0.001) this.roundToInt().toString()
    else String.format(ES, "%.1f", this)

/**
 * Volumen en kg con separador de miles: 12450 -> "12.450".
 * Las apps de gimnasio (Strong, Hevy) muestran siempre kg, nunca toneladas.
 */
fun Double.volumeShort(): String =
    String.format(ES, "%,d", this.roundToInt()).replace(',', '.')

fun Double.volumeUnit(): String = "kg"

/** "12.450 kg" */
fun Double.volumeText(): String = "${volumeShort()} kg"

/** "+12%" / "−8%" / null si no hay con qué comparar. */
fun percentChange(actual: Double, anterior: Double): String? {
    if (anterior <= 0.0) return null
    val pct = ((actual - anterior) / anterior * 100).roundToInt()
    if (pct == 0) return null
    return if (pct > 0) "+$pct%" else "−${abs(pct)}%"
}

/** 2.81 -> "2,81" ; 3.0 -> "3" */
fun Double.distanceText(): String =
    if (abs(this - this.roundToInt()) < 0.005) this.roundToInt().toString()
    else String.format(ES, "%.2f", this)

/** 95 -> "1 h 35 min" ; 45 -> "45 min" */
fun Int.durationText(): String = when {
    this <= 0 -> "—"
    this < 60 -> "$this min"
    this % 60 == 0 -> "${this / 60} h"
    else -> "${this / 60} h ${this % 60} min"
}

/** 1275 -> "21:15" */
fun Int.clockText(): String =
    if (this < 0) "--:--" else String.format(ES, "%02d:%02d", this / 60, this % 60)

private fun cap(s: String) = s.replaceFirstChar { it.uppercase(ES) }

/** "lunes, 20 de julio" */
fun LocalDate.longText(): String {
    val dow = cap(dayOfWeek.getDisplayName(TextStyle.FULL, ES))
    val month = month.getDisplayName(TextStyle.FULL, ES)
    return "$dow, $dayOfMonth de $month"
}

/** "20 jul" */
fun LocalDate.shortText(): String {
    val month = month.getDisplayName(TextStyle.SHORT, ES).replace(".", "")
    return "$dayOfMonth $month"
}

/** "L", "M", "X"... */
fun LocalDate.dayInitial(): String = when (dayOfWeek.value) {
    1 -> "L"; 2 -> "M"; 3 -> "X"; 4 -> "J"; 5 -> "V"; 6 -> "S"; else -> "D"
}

fun relativeDay(date: LocalDate, today: LocalDate = LocalDate.now()): String = when (date) {
    today -> "Hoy"
    today.minusDays(1) -> "Ayer"
    today.plusDays(1) -> "Mañana"
    else -> date.longText()
}

/** "22/07/2026" */
fun LocalDate.numeric(): String =
    String.format(ES, "%02d/%02d/%d", dayOfMonth, monthValue, year)

fun epochDayToDate(day: Long): LocalDate = LocalDate.ofEpochDay(day)
