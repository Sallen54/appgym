package com.alejandro.appgym

import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.data.PlanGuide
import com.alejandro.appgym.data.SeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlanGuideTest {

    private val plan get() = SeedData.groupOf(SeedData.DEFAULT_KEY)!!

    @Test
    fun `el plan por defecto existe de verdad`() {
        // Si se retira un plan sin cambiar DEFAULT_KEY, la app arrancaría sin plan.
        assertTrue(SeedData.groupOf(SeedData.DEFAULT_KEY) != null)
    }

    @Test
    fun `estan las rutinas por parejas mas cuerpo entero y cardio`() {
        assertEquals(
            listOf(
                "Pecho y bíceps", "Espalda y tríceps", "Pierna y glúteo",
                "Hombro y core", "Cuerpo entero", "Cardio"
            ),
            plan.days.map { it.name }
        )
    }

    @Test
    fun `cada rutina tiene ejercicios y foco`() {
        SeedData.plans.forEach { dia ->
            assertTrue("«${dia.name}» sin ejercicios", dia.items.isNotEmpty())
            assertTrue("«${dia.name}» sin foco", dia.focus.isNotBlank())
        }
    }

    @Test
    fun `todos los ejercicios del plan existen en el catalogo`() {
        SeedData.plans.flatMap { it.items }.forEach {
            assertTrue("«${it.exerciseId}» no está en el catálogo", ExerciseCatalog.find(it.exerciseId) != null)
        }
    }

    @Test
    fun `las rutinas de fuerza acaban con cardio`() {
        plan.days.filter { it.name != "Cardio" }.forEach { dia ->
            val ultimo = ExerciseCatalog.find(dia.items.last().exerciseId)!!
            assertTrue("«${dia.name}» no acaba con cardio", ultimo.isCardio)
            assertTrue("«${dia.name}» con el cardio sin minutos", dia.items.last().minutes > 0)
        }
    }

    @Test
    fun `la rutina de cardio es solo cardio`() {
        val cardio = plan.days.first { it.name == "Cardio" }
        assertTrue(cardio.items.all { ExerciseCatalog.find(it.exerciseId)!!.isCardio })
    }

    @Test
    fun `la fuerza tiene repeticiones y peso de partida`() {
        SeedData.plans.flatMap { it.items }
            .filter { !ExerciseCatalog.find(it.exerciseId)!!.isCardio }
            .forEach {
                assertTrue("«${it.exerciseId}» sin repeticiones", it.reps in 6..20)
                val ex = ExerciseCatalog.find(it.exerciseId)!!
                if (ex.equipment != "Peso corporal") {
                    assertTrue("«${it.exerciseId}» sin peso de partida", it.kg > 0)
                }
            }
    }

    @Test
    fun `las rutinas por parejas trabajan lo que dicen`() {
        fun grupos(nombre: String) = plan.days.first { it.name == nombre }.items
            .mapNotNull { ExerciseCatalog.find(it.exerciseId) }
            .filter { !it.isCardio }
            .map { it.group }
            .toSet()
        assertEquals(setOf("Pecho", "Bíceps"), grupos("Pecho y bíceps"))
        assertTrue(grupos("Espalda y tríceps").containsAll(setOf("Espalda", "Tríceps")))
        assertEquals(setOf("Pierna"), grupos("Pierna y glúteo"))
        assertTrue(grupos("Hombro y core").containsAll(setOf("Hombro", "Core")))
    }

    @Test
    fun `los nombres de rutina no se repiten`() {
        val nombres = SeedData.plans.map { it.name }
        assertEquals(nombres.size, nombres.toSet().size)
    }

    @Test
    fun `las rutinas antiguas estan en obsoletos para retirarlas`() {
        listOf(
            "Torso A · empuje", "Pierna A · salto", "Torso B · tirón", "Pierna B · core",
            "Voley · Salto y pierna", "Inicio · Día A"
        ).forEach {
            assertTrue("«$it» debe estar en obsoleteNames", SeedData.obsoleteNames.contains(it))
        }
    }

    @Test
    fun `ninguna rutina actual esta marcada como obsoleta`() {
        SeedData.plans.forEach {
            assertTrue("«${it.name}» se borraría al resembrar", it.name !in SeedData.obsoleteNames)
        }
    }

    @Test
    fun `todas las secciones de consejos tienen contenido`() {
        assertTrue(PlanGuide.secciones.isNotEmpty())
        PlanGuide.secciones.forEach { s ->
            assertTrue("Sección sin título", s.titulo.isNotBlank())
            assertTrue("«${s.titulo}» sin bloques", s.bloques.isNotEmpty())
            s.bloques.forEach {
                assertTrue(it.titulo.isNotBlank())
                assertTrue(it.cuerpo.isNotBlank())
            }
        }
    }

    @Test
    fun `los ids de seccion no se repiten`() {
        val ids = PlanGuide.secciones.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `los consejos no llevan datos personales`() {
        // El repositorio es público.
        val todo = PlanGuide.secciones.joinToString { s ->
            s.titulo + s.resumen + s.destacado.orEmpty() + s.bloques.joinToString { it.cuerpo }
        }
        listOf("Alejandro", "75 kg", "tus 75", "181").forEach {
            assertTrue("Los consejos mencionan «$it»", !todo.contains(it))
        }
    }
}
