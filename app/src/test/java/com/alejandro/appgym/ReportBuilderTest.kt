package com.alejandro.appgym

import com.alejandro.appgym.data.BodyWeight
import com.alejandro.appgym.data.SetEntry
import com.alejandro.appgym.data.WorkoutSession
import com.alejandro.appgym.util.ReportBuilder
import java.time.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportBuilderTest {

    private val hoy = LocalDate.of(2026, 7, 22)

    private fun session(id: Long, date: LocalDate) =
        WorkoutSession(id = id, date = date.toEpochDay(), title = "S")

    @Test
    fun `sin datos el informe lo dice sin reventar`() {
        val out = ReportBuilder.build("Alejandro", 181, 30, "Inicio", emptyList(), emptyList(), emptyList(), hoy)
        assertTrue(out.contains("PERFIL"))
        assertTrue(out.contains("no hay entrenamientos"))
    }

    @Test
    fun `detecta la mejora de peso en un ejercicio entre dos dias`() {
        val s1 = session(1, hoy.minusDays(14))
        val s2 = session(2, hoy)
        val sets = listOf(
            SetEntry(sessionId = 1, exerciseId = "prensa_de_piernas", setNumber = 1, reps = 10, weight = 80.0, done = true),
            SetEntry(sessionId = 2, exerciseId = "prensa_de_piernas", setNumber = 1, reps = 10, weight = 100.0, done = true)
        )
        val out = ReportBuilder.build("Alejandro", 181, 30, "Inicio", listOf(s1, s2), sets, emptyList(), hoy)
        assertTrue(out.contains("Prensa de piernas"))
        assertTrue("Debe mostrar el aumento de 20 kg", out.contains("+20"))
        assertTrue(out.contains("Mejor marca: 100 kg"))
    }

    @Test
    fun `las series no marcadas no cuentan en el informe`() {
        val s = session(1, hoy)
        val sets = listOf(
            SetEntry(sessionId = 1, exerciseId = "prensa_de_piernas", setNumber = 1, reps = 10, weight = 100.0, done = true),
            SetEntry(sessionId = 1, exerciseId = "prensa_de_piernas", setNumber = 2, reps = 10, weight = 999.0, done = false)
        )
        val out = ReportBuilder.build("Alejandro", 181, 30, "Inicio", listOf(s), sets, emptyList(), hoy)
        assertFalse("El peso de una serie sin marcar no debe aparecer", out.contains("999"))
    }

    @Test
    fun `incluye el peso corporal y su cambio`() {
        val s = session(1, hoy)
        val weights = listOf(
            BodyWeight(date = hoy.minusDays(21).toEpochDay(), weight = 75.0),
            BodyWeight(date = hoy.toEpochDay(), weight = 73.5)
        )
        val out = ReportBuilder.build("Alejandro", 181, 30, "Inicio", listOf(s), emptyList(), weights, hoy)
        assertTrue(out.contains("PESO CORPORAL"))
        assertTrue(out.contains("Cambio total: −1,5 kg"))
        assertTrue(out.contains("IMC"))
    }

    @Test
    fun `el cardio se reporta en minutos y no como fuerza`() {
        val s = session(1, hoy)
        val sets = listOf(
            SetEntry(sessionId = 1, exerciseId = "cinta_de_correr", setNumber = 1, reps = 0, weight = 0.0, minutes = 20, done = true)
        )
        val out = ReportBuilder.build("Alejandro", 181, 30, "Inicio", listOf(s), sets, emptyList(), hoy)
        assertTrue(out.contains("CARDIO"))
        assertTrue(out.contains("20 min"))
    }

    @Test
    fun `el informe recoge distancia calorias y pulso de la maquina`() {
        val s = session(1, hoy)
        val sets = listOf(
            SetEntry(
                sessionId = 1, exerciseId = "bicicleta_estatica", setNumber = 1,
                reps = 0, weight = 0.0, minutes = 20,
                distanceKm = 2.81, calories = 214, avgHeartRate = 166, done = true
            )
        )
        val out = ReportBuilder.build("Alejandro", 181, 30, "Inicio", listOf(s), sets, emptyList(), hoy)
        assertTrue("Falta la distancia", out.contains("2,81 km"))
        assertTrue("Faltan las calorías", out.contains("214 kcal"))
        assertTrue("Falta el pulso", out.contains("166 ppm"))
        assertTrue("Falta la zona 2", out.contains("Zona 2"))
    }

    @Test
    fun `con pocos dias no inventa una frecuencia semanal`() {
        // Dos sesiones en tres días daban "4,7 por semana", que no significa nada.
        val sets = listOf(
            SetEntry(sessionId = 1, exerciseId = "prensa_de_piernas", setNumber = 1, reps = 12, weight = 40.0, done = true)
        )
        val out = ReportBuilder.build(
            "Alejandro", 181, 26, "Inicio",
            listOf(session(1, hoy.minusDays(2)), session(2, hoy)), sets, emptyList(), hoy
        )
        assertFalse("No debe extrapolar", out.contains("Frecuencia media"))
        assertTrue(out.contains("pocos días"))
    }

    @Test
    fun `con un mes si da la frecuencia media`() {
        val sesiones = (0..7).map { session(it + 1L, hoy.minusDays((it * 4).toLong())) }
        val sets = listOf(
            SetEntry(sessionId = 1, exerciseId = "prensa_de_piernas", setNumber = 1, reps = 12, weight = 40.0, done = true)
        )
        val out = ReportBuilder.build("Alejandro", 181, 26, "Inicio", sesiones, sets, emptyList(), hoy)
        assertTrue(out.contains("Frecuencia media"))
    }

    @Test
    fun `con una sola sesion no repite doce veces cero`() {
        val s = session(1, hoy)
        val sets = listOf("prensa_de_piernas", "press_de_pecho_en_maquina", "jalon_al_pecho_agarre_ancho")
            .mapIndexed { i, ex ->
                SetEntry(sessionId = 1, exerciseId = ex, setNumber = 1, reps = 12, weight = 40.0 + i, done = true)
            }
        val out = ReportBuilder.build("Alejandro", 181, 26, "Inicio", listOf(s), sets, emptyList(), hoy)
        assertTrue("Debe reconocerlo como punto de partida", out.contains("PUNTO DE PARTIDA"))
        assertFalse("No debe hablar de evolución", out.contains("Cambio de peso"))
    }

    @Test
    fun `reparte el trabajo por grupo muscular y detecta desequilibrio`() {
        val s = session(1, hoy)
        // Cuatro series de pecho contra una de espalda: desequilibrio claro.
        val sets = (1..4).map {
            SetEntry(sessionId = 1, exerciseId = "press_de_pecho_en_maquina", setNumber = it, reps = 10, weight = 60.0, done = true)
        } + SetEntry(sessionId = 1, exerciseId = "jalon_al_pecho_agarre_ancho", setNumber = 1, reps = 10, weight = 40.0, done = true)

        val out = ReportBuilder.build("Alejandro", 181, 26, "Inicio", listOf(s), sets, emptyList(), hoy)
        assertTrue(out.contains("REPARTO DEL TRABAJO"))
        assertTrue(out.contains("Pecho"))
        assertTrue("Debe avisar del desequilibrio", out.contains("más empuje que tirón"))
    }

    @Test
    fun `el informe dice que plan se esta siguiendo`() {
        val out = ReportBuilder.build(
            "Alejandro", 181, 26, "Grasa y músculo",
            listOf(session(1, hoy)), emptyList(), emptyList(), hoy
        )
        assertTrue(out.contains("Grasa y músculo"))
    }

    @Test
    fun `sin edad no se calculan zonas de pulso`() {
        val s = session(1, hoy)
        val sets = listOf(
            SetEntry(sessionId = 1, exerciseId = "bicicleta_estatica", setNumber = 1,
                reps = 0, weight = 0.0, minutes = 20, avgHeartRate = 150, done = true)
        )
        val out = ReportBuilder.build("Alejandro", 181, 0, "Inicio", listOf(s), sets, emptyList(), hoy)
        assertTrue(out.contains("150 ppm"))
        assertFalse(out.contains("Zona 2"))
    }
}
