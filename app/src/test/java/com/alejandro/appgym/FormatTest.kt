package com.alejandro.appgym

import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.util.clockText
import com.alejandro.appgym.util.dayInitial
import com.alejandro.appgym.util.distanceText
import com.alejandro.appgym.util.durationText
import com.alejandro.appgym.util.percentChange
import com.alejandro.appgym.util.relativeDay
import com.alejandro.appgym.util.volumeShort
import com.alejandro.appgym.util.volumeUnit
import com.alejandro.appgym.util.weightText
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests rápidos que corren en el JVM, sin emulador. */
class FormatTest {

    @Test
    fun `los pesos enteros no arrastran decimales`() {
        assertEquals("72", 72.0.weightText())
        assertEquals("72,5", 72.5.weightText())
        assertEquals("0", 0.0.weightText())
    }

    @Test
    fun `el volumen se muestra en kg con separador de miles`() {
        // Como Strong y Hevy: siempre kg, nunca toneladas.
        assertEquals("850", 850.0.volumeShort())
        assertEquals("999", 999.0.volumeShort())
        assertEquals("1.000", 1_000.0.volumeShort())
        assertEquals("12.450", 12_450.0.volumeShort())
        assertEquals("123.400", 123_400.0.volumeShort())
        assertEquals("kg", 12_450.0.volumeUnit())
    }

    @Test
    fun `el cambio porcentual compara con la sesion anterior`() {
        assertEquals("+10%", percentChange(1100.0, 1000.0))
        assertEquals("−10%", percentChange(900.0, 1000.0))
        assertNull("Sin cambio no se muestra nada", percentChange(1000.0, 1000.0))
        assertNull("Sin sesión previa no hay comparación", percentChange(1000.0, 0.0))
    }

    @Test
    fun `la distancia mantiene los decimales de la maquina`() {
        assertEquals("2,81", 2.81.distanceText())
        assertEquals("2,8", 2.80.distanceText())
        assertEquals("0,5", 0.5.distanceText())
        assertEquals("0,01", 0.01.distanceText())
    }

    @Test
    fun `la distancia no arrastra ceros de relleno`() {
        assertEquals("3", 3.0.distanceText())
        assertEquals("0", 0.0.distanceText())
    }

    @Test
    fun `quitar los ceros no se come digitos enteros`() {
        // Recortar "10,00" por la derecha podría dejar "1" si no parase en la coma.
        assertEquals("10", 10.0.distanceText())
        assertEquals("100", 100.0.distanceText())
        assertEquals("20,05", 20.05.distanceText())
    }

    @Test
    fun `la duracion se lee en horas y minutos`() {
        assertEquals("45 min", 45.durationText())
        assertEquals("1 h", 60.durationText())
        assertEquals("1 h 35 min", 95.durationText())
        assertEquals("2 h", 120.durationText())
        assertEquals("—", 0.durationText())
    }

    @Test
    fun `la hora se formatea con dos digitos`() {
        assertEquals("21:15", (21 * 60 + 15).clockText())
        assertEquals("09:05", (9 * 60 + 5).clockText())
        assertEquals("--:--", (-1).clockText())
    }

    @Test
    fun `miercoles es X y no M`() {
        val miercoles = LocalDate.of(2026, 7, 22)
        assertEquals("X", miercoles.dayInitial())
        assertEquals("M", LocalDate.of(2026, 7, 21).dayInitial())
    }

    @Test
    fun `los dias cercanos se nombran en lenguaje natural`() {
        val hoy = LocalDate.of(2026, 7, 20)
        assertEquals("Hoy", relativeDay(hoy, hoy))
        assertEquals("Ayer", relativeDay(hoy.minusDays(1), hoy))
        assertTrue(relativeDay(hoy.minusDays(9), hoy).contains("de julio"))
    }

    @Test
    fun `el catalogo no tiene ids repetidos y todos apuntan a una imagen`() {
        val ids = ExerciseCatalog.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        assertTrue(ExerciseCatalog.all.all { it.imageAsset == "exercises/${it.id}.webp" })
        assertTrue(ExerciseCatalog.all.all { it.name.isNotBlank() })
    }

    @Test
    fun `las alternativas son del mismo grupo y no incluyen el original`() {
        ExerciseCatalog.all.forEach { ex ->
            val alts = ExerciseCatalog.alternativesFor(ex.id)
            assertTrue("«${ex.name}» se propone a sí mismo", alts.none { it.id == ex.id })
            assertTrue(
                "«${ex.name}» propone otro grupo",
                alts.all { it.group == ex.group }
            )
            assertTrue(
                "«${ex.name}» mezcla cardio con fuerza",
                alts.all { it.isCardio == ex.isCardio }
            )
        }
    }

    @Test
    fun `casi todos los ejercicios tienen alguna alternativa`() {
        val sinAlternativa = ExerciseCatalog.all.filter { ExerciseCatalog.alternativesFor(it.id).isEmpty() }
        assertTrue("Se quedan sin sustituto: ${sinAlternativa.map { it.name }}", sinAlternativa.size <= 2)
    }

    @Test
    fun `nunca se propone el movimiento opuesto`() {
        val abd = ExerciseCatalog.alternativesFor("abductores_en_maquina").map { it.id }
        assertTrue("Los aductores no sustituyen a los abductores", !abd.contains("aductores_en_maquina"))

        val cuad = ExerciseCatalog.alternativesFor("extension_de_cuadriceps").map { it.id }
        assertTrue("El femoral no sustituye al cuádriceps", !cuad.contains("curl_femoral_tumbado"))
    }

    @Test
    fun `el face pull propone trabajo de deltoides posterior`() {
        val alts = ExerciseCatalog.alternativesFor("face_pull").map { it.id }
        assertEquals("aperturas_invertidas_en_maquina", alts.first())
    }

    @Test
    fun `las maquinas de cardio se sustituyen entre ellas`() {
        val alts = ExerciseCatalog.alternativesFor("bicicleta_estatica")
        assertTrue(alts.isNotEmpty())
        assertTrue(alts.all { it.isCardio })
    }

    @Test
    fun `todos los ejercicios pertenecen a un grupo conocido`() {
        val grupos = ExerciseCatalog.groups.toSet()
        assertTrue(ExerciseCatalog.all.all { it.group in grupos })
    }
}
