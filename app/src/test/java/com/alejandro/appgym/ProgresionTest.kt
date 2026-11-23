package com.alejandro.appgym

import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.data.SetEntry
import com.alejandro.appgym.util.Progresion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgresionTest {

    private val prensaPecho = ExerciseCatalog.find("press_de_pecho_en_maquina")
    private val prensaPierna = ExerciseCatalog.find("prensa_de_piernas")
    private val curlMancuerna = ExerciseCatalog.find("curl_martillo")

    private fun serie(n: Int, kg: Double, reps: Int, hecha: Boolean = true) = SetEntry(
        sessionId = 1, exerciseId = "x", position = 0, setNumber = n,
        reps = reps, weight = kg, done = hecha
    )

    @Test
    fun `sin historial usa el peso de partida y lo marca como primera vez`() {
        val s = Progresion.sugerir(prensaPecho, emptyList(), objetivo = 10, series = 4, pesoInicial = 30.0)
        assertEquals(30.0, s.kg, 0.001)
        assertTrue(s.primeraVez)
        assertFalse(s.sube)
        assertEquals("8-10", s.rango)
    }

    @Test
    fun `si completa todas las series en el alto sube peso`() {
        val ultima = (1..4).map { serie(it, 30.0, 10) }
        val s = Progresion.sugerir(prensaPecho, ultima, objetivo = 10, series = 4, pesoInicial = 25.0)
        assertTrue(s.sube)
        assertEquals(32.5, s.kg, 0.001)
        assertTrue(s.motivo.contains("32,5"))
    }

    @Test
    fun `si no completa repite peso`() {
        val ultima = listOf(serie(1, 30.0, 10), serie(2, 30.0, 9), serie(3, 30.0, 8), serie(4, 30.0, 8))
        val s = Progresion.sugerir(prensaPecho, ultima, objetivo = 10, series = 4, pesoInicial = 25.0)
        assertFalse(s.sube)
        assertEquals(30.0, s.kg, 0.001)
        assertTrue(s.motivo.contains("10, 9, 8, 8"))
    }

    @Test
    fun `menos series de las que pide la rutina no cuenta como completo`() {
        val ultima = (1..2).map { serie(it, 30.0, 10) }
        val s = Progresion.sugerir(prensaPecho, ultima, objetivo = 10, series = 4, pesoInicial = 0.0)
        assertFalse(s.sube)
    }

    @Test
    fun `las series con menos peso no deciden`() {
        // Calentamiento a 20 kg con pocas repeticiones: no debe frenar la subida.
        val ultima = listOf(serie(1, 20.0, 5)) + (2..4).map { serie(it, 30.0, 12) }
        val s = Progresion.sugerir(prensaPecho, ultima, objetivo = 12, series = 3, pesoInicial = 0.0)
        assertTrue(s.sube)
    }

    @Test
    fun `las series sin marcar se ignoran`() {
        val ultima = (1..3).map { serie(it, 30.0, 12) } + serie(4, 30.0, 12, hecha = false)
        val s = Progresion.sugerir(prensaPecho, ultima, objetivo = 12, series = 4, pesoInicial = 0.0)
        assertFalse("Solo hizo 3 de 4", s.sube)
    }

    @Test
    fun `la pierna en maquina sube 5 kg y las mancuernas 2`() {
        assertEquals(5.0, Progresion.incremento(prensaPierna), 0.001)
        assertEquals(2.0, Progresion.incremento(curlMancuerna), 0.001)
        assertEquals(2.5, Progresion.incremento(prensaPecho), 0.001)
    }

    @Test
    fun `sin carga progresa en repeticiones`() {
        val ultima = (1..3).map { serie(it, 0.0, 12) }
        val s = Progresion.sugerir(ExerciseCatalog.find("elevacion_de_piernas_en_banco"), ultima, 15, 3, 0.0)
        assertEquals(0.0, s.kg, 0.001)
        assertFalse(s.sube)
    }

    @Test
    fun `al subir se arranca en el bajo del rango`() {
        val ultima = (1..3).map { serie(it, 30.0, 12) }
        val s = Progresion.sugerir(prensaPecho, ultima, 12, 3, 0.0)
        assertEquals(10, Progresion.repsDeArranque(s, ultima))
    }

    @Test
    fun `al repetir peso se pide una mas que la peor serie`() {
        val ultima = listOf(serie(1, 30.0, 11), serie(2, 30.0, 10), serie(3, 30.0, 10))
        val s = Progresion.sugerir(prensaPecho, ultima, 12, 3, 0.0)
        assertEquals(11, Progresion.repsDeArranque(s, ultima))
    }

    @Test
    fun `tras un paron de un mes se rebaja un 10 por ciento`() {
        val ultima = (1..3).map { serie(it, 40.0, 12) }
        val s = Progresion.sugerir(prensaPecho, ultima, 12, 3, 0.0, diasSinHacer = 30)
        assertTrue(s.vuelta)
        assertFalse("No sube aunque completara", s.sube)
        assertEquals(35.0, s.kg, 0.001)   // 36 redondeado hacia abajo a saltos de 2,5
        assertEquals(12, Progresion.repsDeArranque(s, ultima))
    }

    @Test
    fun `tras meses sin hacerlo se rebaja un 20 por ciento`() {
        val ultima = (1..3).map { serie(it, 100.0, 10) }
        val s = Progresion.sugerir(prensaPierna, ultima, 10, 3, 0.0, diasSinHacer = 120)
        assertEquals(80.0, s.kg, 0.001)
        assertTrue(s.motivo.contains("17 semanas"))
    }

    @Test
    fun `dos semanas sin hacerlo no cuenta como paron`() {
        val ultima = (1..3).map { serie(it, 40.0, 12) }
        val s = Progresion.sugerir(prensaPecho, ultima, 12, 3, 0.0, diasSinHacer = 14)
        assertFalse(s.vuelta)
        assertTrue(s.sube)
    }

    @Test
    fun `la primera vez se apunta al alto del rango`() {
        val s = Progresion.sugerir(prensaPecho, emptyList(), 12, 3, 25.0)
        assertEquals(12, Progresion.repsDeArranque(s, emptyList()))
    }
}
