package com.alejandro.appgym.util

import com.alejandro.appgym.data.BodyWeight
import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.data.SetEntry
import com.alejandro.appgym.data.WorkoutSession
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Genera un informe de rendimiento en texto plano, pensado para que un humano lo lea
 * y para que se pueda analizar la evolución (mejora / empeora) pegándolo en un chat.
 *
 * No juzga: solo presenta los datos y los cambios calculados. La interpretación se
 * deja para quien lo lea.
 */
object ReportBuilder {

    fun build(
        name: String,
        heightCm: Int,
        age: Int = 0,
        planName: String = "",
        sessions: List<WorkoutSession>,
        sets: List<SetEntry>,
        weights: List<BodyWeight>,
        today: LocalDate = LocalDate.now()
    ): String {
        val sb = StringBuilder()
        val done = sets.filter { it.done }
        val dateOf = sessions.associate { it.id to it.date }
        val descansos = sessions.count { it.restDay }
        val entrenos = sessions.filter { !it.restDay }

        fun line(s: String = "") = sb.append(s).append('\n')
        fun rule() = line("──────────────────────────────")

        line("INFORME DE ENTRENAMIENTO · AppGym")
        line("Generado el ${today.numeric()}")
        line()
        line("Este informe recoge todo el historial registrado en la app.")
        line("Sirve para valorar si el rendimiento mejora o empeora con el tiempo.")
        rule()

        /* ---------------- Perfil ---------------- */
        line("PERFIL")
        line("Nombre: ${name.ifBlank { "—" }}")
        line("Altura: $heightCm cm")
        if (age > 0) line("Edad: $age años")
        val curW = weights.lastOrNull()?.weight
        val firstW = weights.firstOrNull()?.weight
        if (curW != null) {
            val extra = if (firstW != null && weights.size > 1) {
                val d = curW - firstW
                "  (inicial ${firstW.weightText()} · ${signed(d)} kg)"
            } else ""
            line("Peso actual: ${curW.weightText()} kg$extra")
            if (heightCm > 0) {
                val imc = curW / ((heightCm / 100.0) * (heightCm / 100.0))
                line("IMC: ${String.format(ES, "%.1f", imc)}")
            }
        }
        rule()

        if (sessions.isEmpty()) {
            line("Todavía no hay entrenamientos registrados.")
            return sb.toString()
        }

        /* ---------------- Resumen ---------------- */
        val dates = sessions.map { LocalDate.ofEpochDay(it.date) }.sorted()
        val firstDay = dates.first()
        val lastDay = dates.last()
        val span = (lastDay.toEpochDay() - firstDay.toEpochDay()).toInt() + 1
        val weeks = (span / 7.0).coerceAtLeast(1.0 / 7)
        val totalVolume = done.sumOf { it.volume }
        val cardioMin = done.sumOf { it.minutes }

        line("RESUMEN")
        line("Periodo: ${firstDay.numeric()} — ${lastDay.numeric()}  ($span días)")
        if (planName.isNotBlank()) line("Plan que sigue: $planName")
        line("Sesiones registradas: ${entrenos.size}")
        if (descansos > 0) line("Días marcados como descanso: $descansos")
        // Extrapolar una media semanal desde tres días da números absurdos (4,7/semana
        // con dos sesiones). Con menos de dos semanas se dan los datos en crudo.
        if (span >= 14) {
            line("Frecuencia media: ${String.format(ES, "%.1f", entrenos.size / weeks)} por semana")
        } else {
            line("Aún son pocos días para hablar de frecuencia media.")
        }
        line("Series completadas: ${done.size}")
        line("Peso total movido: ${totalVolume.volumeText()}")
        val conHoras = sessions.filter { it.hasTimes }
        if (conHoras.isNotEmpty()) {
            val media = conHoras.sumOf { it.durationMinutes } / conHoras.size
            line("Duración media de sesión: ${media.durationText()}")
        }
        if (cardioMin > 0) line("Cardio acumulado: $cardioMin min")
        rule()

        /* ---------------- Volumen por semana ---------------- */
        line("VOLUMEN POR SEMANA (kg movidos)")
        val byWeek = done
            .groupBy { weekStartOf(LocalDate.ofEpochDay(dateOf[it.sessionId] ?: 0L)) }
            .toSortedMap()
        byWeek.forEach { (monday, list) ->
            val v = list.sumOf { it.volume }
            val ses = list.mapNotNull { dateOf[it.sessionId] }.distinct().size
            line("  ${monday.shortText()}: ${v.volumeText()}  ($ses ${plural(ses, "sesión", "sesiones")})")
        }
        if (byWeek.size >= 2) {
            val vals = byWeek.values.map { it.sumOf { s -> s.volume } }
            line("Tendencia: ${trend(vals)}")
        }
        rule()

        /* ---------------- Evolución por ejercicio ---------------- */
        val fuerzaPorEj = done
            .filter { it.weight > 0 }
            .groupBy { it.exerciseId }
            .toList()
            .sortedByDescending { (_, l) -> l.sumOf { it.volume } }

        val conVariasSesiones = fuerzaPorEj.filter { (_, l) ->
            l.mapNotNull { dateOf[it.sessionId] }.distinct().size >= 2
        }

        if (fuerzaPorEj.isEmpty()) {
            line("EVOLUCIÓN POR EJERCICIO")
            line("  Aún no hay series de fuerza con peso registrado.")
            rule()
        } else if (conVariasSesiones.isEmpty()) {
            // Con una sola sesión por ejercicio no hay evolución que enseñar:
            // repetir "±0 kg" doce veces es ruido, no información.
            line("PUNTO DE PARTIDA")
            line("(cada ejercicio se ha hecho una sola vez; todavía no hay evolución)")
            line()
            fuerzaPorEj.forEach { (id, list) ->
                val ex = ExerciseCatalog.require(id)
                val mejor = list.maxByOrNull { it.weight }!!
                line("  ${ex.name}: ${mejor.weight.weightText()} kg × ${mejor.reps}  ·  " +
                    "${list.size} ${plural(list.size, "serie", "series")}  ·  ${ex.group}")
            }
            rule()
        } else {
            line("EVOLUCIÓN POR EJERCICIO")
            line("(primer registro → último; solo los que llevan dos sesiones o más)")
            line()
            conVariasSesiones.forEach { (id, list) ->
                val ex = ExerciseCatalog.require(id)
                val ordered = list.sortedBy { dateOf[it.sessionId] ?: 0L }
                val firstBest = bestOn(ordered, ordered.first().sessionId, dateOf)
                val lastBest = bestOn(ordered, ordered.last().sessionId, dateOf)
                val best = ordered.maxByOrNull { it.weight }!!
                val daysTrained = ordered.mapNotNull { dateOf[it.sessionId] }.distinct().size
                val d = lastBest.weight - firstBest.weight
                val pct = if (firstBest.weight > 0) " (${signed(d / firstBest.weight * 100, "%")})" else ""

                line("${ex.name}  ·  ${ex.group}")
                line("  Sesiones: $daysTrained")
                line("  Primer día: ${firstBest.weight.weightText()} kg × ${firstBest.reps}")
                line("  Último día: ${lastBest.weight.weightText()} kg × ${lastBest.reps}")
                line("  Cambio de peso: ${signed(d)} kg$pct")
                line("  Mejor marca: ${best.weight.weightText()} kg × ${best.reps}")
                line()
            }

            val soloUna = fuerzaPorEj - conVariasSesiones.toSet()
            if (soloUna.isNotEmpty()) {
                line("Con una sola sesión, sin evolución todavía:")
                soloUna.forEach { (id, list) ->
                    val ex = ExerciseCatalog.require(id)
                    val mejor = list.maxByOrNull { it.weight }!!
                    line("  ${ex.name}: ${mejor.weight.weightText()} kg × ${mejor.reps}")
                }
                line()
            }
            rule()
        }

        /* ---------------- Reparto del trabajo ---------------- */
        if (done.any { it.weight > 0 }) {
            line("REPARTO DEL TRABAJO")
            line("(series y kg movidos por grupo muscular; sirve para ver desequilibrios)")
            line()
            val porGrupo = done
                .filter { it.weight > 0 }
                .groupBy { ExerciseCatalog.require(it.exerciseId).group }
                .toList()
                .sortedByDescending { (_, l) -> l.sumOf { it.volume } }
            val totalSeries = done.count { it.weight > 0 }
            porGrupo.forEach { (grupo, list) ->
                val pct = (list.size * 100.0 / totalSeries).roundToInt()
                line("  $grupo: ${list.size} ${plural(list.size, "serie", "series")} ($pct%) · " +
                    list.sumOf { it.volume }.volumeText())
            }

            // Los dos equilibrios que más se descuidan entrenando solo.
            val seriesDe = { g: String -> porGrupo.firstOrNull { it.first == g }?.second?.size ?: 0 }
            val empuje = seriesDe("Pecho") + seriesDe("Hombro") + seriesDe("Tríceps")
            val tiron = seriesDe("Espalda") + seriesDe("Bíceps") + seriesDe("Trapecio")
            if (empuje > 0 || tiron > 0) {
                line()
                line("  Empuje $empuje series / tirón $tiron series" + equilibrio(empuje, tiron))
            }
            rule()
        }

        /* ---------------- Cardio ---------------- */
        val cardio = done.filter { it.minutes > 0 }.groupBy { it.exerciseId }
        if (cardio.isNotEmpty()) {
            line("CARDIO")
            cardio.toList().sortedByDescending { (_, l) -> l.sumOf { it.minutes } }.forEach { (id, list) ->
                val ex = ExerciseCatalog.require(id)
                val min = list.sumOf { it.minutes }
                val ses = list.mapNotNull { dateOf[it.sessionId] }.distinct().size
                val km = list.sumOf { it.distanceKm }
                val kcal = list.sumOf { it.calories }
                line(buildString {
                    append("  ${ex.name}: $min min en $ses ${plural(ses, "día", "días")}")
                    if (km > 0) append(" · ${km.distanceText()} km")
                    if (kcal > 0) append(" · $kcal kcal")
                })
            }

            val conPulso = done.filter { it.avgHeartRate > 0 }
            if (conPulso.isNotEmpty()) {
                val medio = conPulso.sumOf { it.avgHeartRate } / conPulso.size
                val maximo = conPulso.maxOf { it.avgHeartRate }
                line()
                line("Pulso medio en cardio: $medio ppm (máximo registrado $maximo)")
                if (age > 0) {
                    val fcMax = (208 - 0.7 * age).toInt()
                    val z2 = (fcMax * 0.60).toInt()..(fcMax * 0.72).toInt()
                    line("Frecuencia máxima estimada: $fcMax ppm")
                    line("Zona 2 (ritmo cómodo): ${z2.first}-${z2.last} ppm")
                }
            }
            rule()
        }

        /* ---------------- Récords ---------------- */
        line("RÉCORDS PERSONALES (peso máximo)")
        val records = done.filter { it.weight > 0 }
            .groupBy { it.exerciseId }
            .map { (id, l) -> id to l.maxByOrNull { it.weight }!! }
            .sortedByDescending { it.second.weight }
            .take(10)
        if (records.isEmpty()) line("  —")
        records.forEach { (id, s) ->
            line("  ${ExerciseCatalog.require(id).name}: ${s.weight.weightText()} kg × ${s.reps}")
        }
        rule()

        /* ---------------- Peso corporal ---------------- */
        if (weights.isNotEmpty()) {
            line("PESO CORPORAL")
            weights.forEach { line("  ${LocalDate.ofEpochDay(it.date).numeric()}: ${it.weight.weightText()} kg") }
            if (weights.size >= 2) {
                line("Cambio total: ${signed(weights.last().weight - weights.first().weight)} kg")
            }
            rule()
        }

        line("Fin del informe.")
        return sb.toString()
    }

    /* ---------------- Auxiliares ---------------- */

    private fun bestOn(list: List<SetEntry>, sessionId: Long, dateOf: Map<Long, Long>): SetEntry {
        val day = dateOf[sessionId]
        return list.filter { dateOf[it.sessionId] == day }.maxByOrNull { it.weight } ?: list.first()
    }

    private fun weekStartOf(date: LocalDate): LocalDate =
        date.minusDays(((date.dayOfWeek.value + 6) % 7).toLong())

    private fun signed(v: Double, unit: String = ""): String {
        val r = (v * 10).roundToInt() / 10.0
        val sign = if (r > 0) "+" else if (r < 0) "−" else "±"
        return "$sign${abs(r).weightText()}$unit"
    }

    /** Comenta el desequilibrio solo cuando es grande de verdad. */
    private fun equilibrio(a: Int, b: Int): String {
        if (a == 0 || b == 0) return ""
        val ratio = maxOf(a, b).toDouble() / minOf(a, b)
        return when {
            ratio >= 1.5 && a > b -> "  → bastante más empuje que tirón"
            ratio >= 1.5 -> "  → bastante más tirón que empuje"
            else -> "  → equilibrado"
        }
    }

    private fun plural(n: Int, one: String, many: String) = if (n == 1) one else many

    private fun trend(values: List<Double>): String {
        if (values.size < 2) return "sin datos suficientes"
        val firstHalf = values.take(values.size / 2).ifEmpty { listOf(values.first()) }
        val lastHalf = values.takeLast(values.size / 2)
        val a = firstHalf.average()
        val b = lastHalf.average()
        val d = b - a
        val pct = if (a > 0) " (${signed(d / a * 100, "%")})" else ""
        return when {
            abs(d) < a * 0.05 -> "estable$pct"
            d > 0 -> "al alza$pct"
            else -> "a la baja$pct"
        }
    }
}
