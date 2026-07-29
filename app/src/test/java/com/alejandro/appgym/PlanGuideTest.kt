package com.alejandro.appgym

import com.alejandro.appgym.data.PlanGuide
import com.alejandro.appgym.data.SeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlanGuideTest {

    @Test
    fun `solo queda el plan principal`() {
        assertEquals(1, SeedData.groups.size)
        assertEquals("recomp", SeedData.groups.first().key)
        assertEquals("recomp", SeedData.DEFAULT_KEY)
    }

    @Test
    fun `el plan por defecto existe de verdad`() {
        // Si se retira un plan sin cambiar DEFAULT_KEY, la app arrancaría sin plan.
        assertTrue(SeedData.groupOf(SeedData.DEFAULT_KEY) != null)
    }

    @Test
    fun `cada plan tiene dias y cada dia ejercicios`() {
        SeedData.groups.forEach { grupo ->
            assertTrue("«${grupo.name}» sin días", grupo.days.isNotEmpty())
            grupo.days.forEach { dia ->
                assertTrue("«${dia.name}» sin ejercicios", dia.items.isNotEmpty())
                assertEquals("«${dia.name}» con la clave de plan mal", grupo.key, dia.key)
            }
        }
    }

    @Test
    fun `cada dia del plan cae en un dia de la semana distinto`() {
        val dias = SeedData.groupOf("recomp")!!.days.map { it.weekday }
        assertEquals("No puede haber dos rutinas el mismo día", dias.size, dias.toSet().size)
        assertTrue("Días fuera de rango", dias.all { it in 1..7 })
    }

    @Test
    fun `la app sabe que toca cada dia`() {
        assertEquals("Torso A · empuje", SeedData.dayFor("recomp", 1)?.name)   // lunes
        assertEquals("Pierna A · salto", SeedData.dayFor("recomp", 2)?.name)   // martes
        assertEquals("Torso B · tirón", SeedData.dayFor("recomp", 4)?.name)    // jueves
        assertEquals("Pierna B · core", SeedData.dayFor("recomp", 5)?.name)    // viernes
    }

    @Test
    fun `los dias sin rutina son de descanso`() {
        listOf(3, 6, 7).forEach {   // miércoles, sábado, domingo
            assertTrue("El día $it no debería tener rutina", SeedData.dayFor("recomp", it) == null)
        }
    }

    @Test
    fun `no hay dos dias de pierna seguidos`() {
        val porDia = SeedData.groupOf("recomp")!!.days.associateBy { it.weekday }
        (1..6).forEach { d ->
            val hoy = porDia[d]?.name.orEmpty()
            val manana = porDia[d + 1]?.name.orEmpty()
            assertTrue(
                "Dos días de pierna seguidos ($d y ${d + 1})",
                !(hoy.startsWith("Pierna") && manana.startsWith("Pierna"))
            )
        }
    }

    @Test
    fun `el plan tiene cuatro dias`() {
        assertEquals(4, SeedData.groupOf("recomp")!!.days.size)
    }

    @Test
    fun `el plan retirado ya no existe pero sus rutinas estan en obsoletos`() {
        assertTrue(SeedData.groupOf("inicio") == null)
        listOf("Inicio · Día A", "Inicio · Día B").forEach {
            assertTrue("«$it» debe estar en obsoleteNames", SeedData.obsoleteNames.contains(it))
        }
    }

    @Test
    fun `el trabajo de voleibol esta dentro del plan principal`() {
        val ejercicios = SeedData.groupOf("recomp")!!.days.flatMap { it.items }.map { it.exerciseId }
        listOf(
            "elevacion_de_gemelos_de_pie",   // salto
            "face_pull",                     // hombro sano
            "abductores_en_maquina",          // estabilidad lateral
            "pallof_press",                   // core antirrotación
            "lenador_en_polea"                // rotación
        ).forEach {
            assertTrue("Falta «$it», que venía del plan de voley", ejercicios.contains(it))
        }
    }

    @Test
    fun `las secciones de la guia coinciden con las claves de plan`() {
        val ids = PlanGuide.secciones.map { it.id }
        SeedData.groups.forEach {
            assertTrue("No hay sección para el plan «${it.key}»", ids.contains(it.key))
        }
    }

    @Test
    fun `todas las secciones tienen contenido`() {
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
    fun `los nombres de rutina no se repiten entre planes`() {
        val nombres = SeedData.plans.map { it.name }
        assertEquals(nombres.size, nombres.toSet().size)
    }

    @Test
    fun `los planes retirados estan en la lista de obsoletos`() {
        listOf("Voley · Salto y pierna", "Adaptación A", "Pierna A", "Inicio · Día A")
            .forEach {
                assertTrue("«$it» debe estar en obsoleteNames", SeedData.obsoleteNames.contains(it))
            }
    }
}
