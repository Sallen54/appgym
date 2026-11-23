package com.alejandro.appgym.util

import com.alejandro.appgym.data.Exercise
import com.alejandro.appgym.data.SetEntry

/**
 * Peso y repeticiones sugeridos para la próxima sesión de un ejercicio.
 *
 * Usa la doble progresión, que es lo que hacen las apps serias y los entrenadores
 * con gente que entrena sola: se trabaja en un rango (10-12, por ejemplo). Mientras
 * no completes todas las series en el número alto, repites peso e intentas sumar
 * repeticiones. Cuando lo consigues, subes peso y vuelves al número bajo.
 *
 * Es código puro, sin Room ni Android, para poder probarlo en la JVM.
 */
object Progresion {

    /** Lo que se le enseña al usuario: qué hacer y por qué. */
    data class Sugerencia(
        val kg: Double,
        val repsMin: Int,
        val repsMax: Int,
        val motivo: String,
        /** True si sube el peso respecto a la última vez. */
        val sube: Boolean = false,
        /** True si no hay historial y el peso es solo orientativo. */
        val primeraVez: Boolean = false,
        /** True si hace semanas que no se hace y se rebaja el peso para volver. */
        val vuelta: Boolean = false
    ) {
        val rango: String get() = if (repsMin == repsMax) "$repsMax" else "$repsMin-$repsMax"
    }

    /** A partir de cuántos días sin hacer un ejercicio se considera un parón. */
    const val DIAS_PARON = 21L
    const val DIAS_PARON_LARGO = 60L

    /** El rango va del objetivo menos dos al objetivo: 12 → 10-12. */
    fun repsMin(objetivo: Int): Int = (objetivo - 2).coerceAtLeast(1)

    /** Cuánto subir de una vez según el material. */
    fun incremento(ejercicio: Exercise?): Double = when {
        ejercicio == null -> 2.5
        ejercicio.equipment == "Mancuernas" -> 2.0
        ejercicio.group == "Pierna" && ejercicio.equipment == "Máquina" -> 5.0
        else -> 2.5
    }

    /**
     * @param ultimaSesion series completadas de ese ejercicio en la última sesión
     *   anterior, en orden. Vacía si nunca se ha hecho.
     * @param objetivo repeticiones objetivo de la rutina (el alto del rango).
     * @param series cuántas series pide la rutina.
     * @param pesoInicial peso de partida si no hay historial (0 = sin sugerencia de kg).
     */
    fun sugerir(
        ejercicio: Exercise?,
        ultimaSesion: List<SetEntry>,
        objetivo: Int,
        series: Int,
        pesoInicial: Double,
        /** Días desde esa última sesión. Null si no se sabe. */
        diasSinHacer: Long? = null
    ): Sugerencia {
        val min = repsMin(objetivo)
        val hechas = ultimaSesion.filter { it.done && it.reps > 0 }

        if (hechas.isEmpty()) {
            return Sugerencia(
                kg = pesoInicial,
                repsMin = min,
                repsMax = objetivo,
                primeraVez = true,
                motivo = if (pesoInicial > 0)
                    "Peso de partida orientativo. Ajústalo para llegar a $objetivo dejando 2 en reserva."
                else
                    "Primera vez: llega a $objetivo con buena técnica."
            )
        }

        // El peso de trabajo es el más alto que usaste; las series con menos peso
        // (calentamiento o bajadas a propósito) no cuentan para decidir.
        val peso = hechas.maxOf { it.weight }
        val deTrabajo = hechas.filter { it.weight == peso }
        val repsHechas = deTrabajo.joinToString(", ") { it.reps.toString() }
        val completo = deTrabajo.size >= series && deTrabajo.all { it.reps >= objetivo }

        // Ejercicios sin carga (flexiones, elevación de piernas): se progresa en reps.
        if (peso <= 0.0) {
            return Sugerencia(
                kg = 0.0,
                repsMin = min,
                repsMax = objetivo,
                motivo = if (completo) "La última vez: $repsHechas. Ya dominas el rango: hazlo más lento."
                else "La última vez: $repsHechas. Intenta una repetición más."
            )
        }

        // Tras un parón, volver al último peso es pedir una lesión o unas agujetas de
        // una semana. Se rebaja un 10 % a partir de tres semanas y un 20 % a partir
        // de dos meses, redondeando al salto de placa más cercano (hacia abajo se
        // rebajaba dos veces: 40 kg al 80 % daba 30 en vez de 32,5).
        if (diasSinHacer != null && diasSinHacer >= DIAS_PARON) {
            val paso = incremento(ejercicio)
            val factor = if (diasSinHacer >= DIAS_PARON_LARGO) 0.8 else 0.9
            val rebajado = (kotlin.math.round(peso * factor / paso) * paso)
                .coerceAtLeast(paso)
                .coerceAtMost(peso)
            val semanas = diasSinHacer / 7
            return Sugerencia(
                kg = rebajado,
                repsMin = min,
                repsMax = objetivo,
                vuelta = true,
                motivo = "Llevas $semanas semanas sin hacerlo (la última vez " +
                    "${peso.weightText()} kg). Vuelve con ${rebajado.weightText()} kg; " +
                    "en un par de sesiones la app te devuelve donde estabas."
            )
        }

        if (completo) {
            val nuevo = peso + incremento(ejercicio)
            return Sugerencia(
                kg = nuevo,
                repsMin = min,
                repsMax = objetivo,
                sube = true,
                motivo = "La última vez: ${peso.weightText()} kg × $repsHechas. " +
                    "Completaste todo: sube a ${nuevo.weightText()} kg y empieza en $min."
            )
        }

        return Sugerencia(
            kg = peso,
            repsMin = min,
            repsMax = objetivo,
            motivo = "La última vez: ${peso.weightText()} kg × $repsHechas. " +
                "Mismo peso, intenta una repetición más en cada serie."
        )
    }

    /** Repeticiones con las que arrancar las series: una más que la última vez, dentro del rango. */
    fun repsDeArranque(s: Sugerencia, ultimaSesion: List<SetEntry>): Int {
        if (s.primeraVez || s.vuelta) return s.repsMax
        if (s.sube) return s.repsMin
        val ultima = ultimaSesion.filter { it.done && it.weight == s.kg }.minOfOrNull { it.reps }
            ?: return s.repsMin
        return (ultima + 1).coerceIn(s.repsMin, s.repsMax)
    }
}
