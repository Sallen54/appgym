package com.alejandro.appgym

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alejandro.appgym.data.AppDatabase
import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.data.GymRepository
import com.alejandro.appgym.data.SeedData
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests de la capa de datos sobre una base en memoria.
 * Ejecutar con: Run 'GymRepositoryTest' (necesita emulador o móvil conectado).
 */
@RunWith(AndroidJUnit4::class)
class GymRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: GymRepository

    private val hoy: LocalDate = LocalDate.of(2026, 7, 20)

    @Before
    fun crear() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        repo = GymRepository(db.sessionDao(), db.setDao(), db.routineDao(), db.bodyWeightDao())
    }

    @After
    fun cerrar() = db.close()

    @Test
    fun un_dia_solo_puede_tener_una_sesion() = runTest {
        val a = repo.sessionForDate(hoy)
        val b = repo.sessionForDate(hoy)
        assertEquals(a.id, b.id)
        assertEquals(1, db.sessionDao().observeAll().first().size)
    }

    @Test
    fun anadir_un_ejercicio_crea_tres_series_y_repetirlo_suma_una() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")
        assertEquals(3, db.setDao().setsFor(s.id, "prensa_de_piernas").size)

        repo.addExerciseToSession(s.id, "prensa_de_piernas")
        val series = db.setDao().setsFor(s.id, "prensa_de_piernas")
        assertEquals(4, series.size)
        assertEquals(listOf(1, 2, 3, 4), series.map { it.setNumber })
    }

    @Test
    fun quitar_serie_elimina_la_ultima_y_deja_las_demas() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")
        repo.addSet(s.id, "prensa_de_piernas")
        assertEquals(4, db.setDao().setsFor(s.id, "prensa_de_piernas").size)

        val series = db.setDao().setsFor(s.id, "prensa_de_piernas")
        repo.deleteSet(series.last())

        val quedan = db.setDao().setsFor(s.id, "prensa_de_piernas")
        assertEquals(3, quedan.size)
        assertEquals(listOf(1, 2, 3), quedan.map { it.setNumber })
    }

    @Test
    fun anadir_y_quitar_series_deja_la_numeracion_correcta() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")

        repo.deleteSet(db.setDao().setsFor(s.id, "prensa_de_piernas").last())
        repo.addSet(s.id, "prensa_de_piernas")

        val series = db.setDao().setsFor(s.id, "prensa_de_piernas")
        assertEquals(3, series.size)
        assertEquals("No debe haber números repetidos", 3, series.map { it.setNumber }.toSet().size)
    }

    @Test
    fun el_peso_se_prerellena_con_el_de_la_ultima_vez() = runTest {
        val lunes = repo.sessionForDate(hoy)
        repo.addExerciseToSession(lunes.id, "prensa_de_piernas")
        val primera = db.setDao().setsFor(lunes.id, "prensa_de_piernas").first()
        repo.updateSet(primera.copy(weight = 120.0, reps = 12, done = true))

        val jueves = repo.sessionForDate(hoy.plusDays(3))
        repo.addExerciseToSession(jueves.id, "prensa_de_piernas")
        val nueva = db.setDao().setsFor(jueves.id, "prensa_de_piernas").first()
        assertEquals(120.0, nueva.weight, 0.001)
        assertEquals(12, nueva.reps)
    }

    @Test
    fun al_marcar_una_serie_las_siguientes_heredan_peso_y_repeticiones() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")

        val series = db.setDao().setsFor(s.id, "prensa_de_piernas")
        val primera = series.first().copy(weight = 28.0, reps = 9, done = true)
        repo.updateSet(primera)
        repo.propagateToPendingSets(primera)

        val despues = db.setDao().setsFor(s.id, "prensa_de_piernas").drop(1)
        assertTrue(despues.isNotEmpty())
        assertTrue(despues.all { it.weight == 28.0 && it.reps == 9 && !it.done })
    }

    @Test
    fun heredar_no_toca_las_series_ya_completadas() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")
        val series = db.setDao().setsFor(s.id, "prensa_de_piernas")

        // La tercera se dejó hecha antes, con otro peso: no debe cambiar.
        repo.updateSet(series[2].copy(weight = 50.0, reps = 5, done = true))

        val primera = series[0].copy(weight = 28.0, reps = 9, done = true)
        repo.updateSet(primera)
        repo.propagateToPendingSets(primera)

        val tercera = db.setDao().setsFor(s.id, "prensa_de_piernas")[2]
        assertEquals(50.0, tercera.weight, 0.001)
        assertEquals(5, tercera.reps)
    }

    @Test
    fun cargar_una_rutina_arranca_con_el_peso_de_la_ultima_vez() = runTest {
        repo.seedPlan()
        val torso = repo.observeRoutines().first().first { it.name == "Torso A · empuje" }

        val antes = repo.sessionForDate(hoy.minusDays(7))
        repo.addExerciseToSession(antes.id, "press_de_pecho_en_maquina")
        val hecha = db.setDao().setsFor(antes.id, "press_de_pecho_en_maquina").first()
        repo.updateSet(hecha.copy(weight = 34.0, reps = 9, done = true))

        val s = repo.sessionForDate(hoy)
        repo.applyRoutine(s.id, torso.id)

        val press = db.setDao().setsFor(s.id, "press_de_pecho_en_maquina")
        assertTrue(press.isNotEmpty())
        assertTrue(press.all { it.weight == 34.0 && it.reps == 9 })
    }

    @Test
    fun cargar_una_rutina_dos_veces_no_duplica_ejercicios() = runTest {
        repo.seedPlan()
        val rutina = repo.observeRoutines().first().first()
        val s = repo.sessionForDate(hoy)

        repo.applyRoutine(s.id, rutina.id)
        val despuesDeLaPrimera = db.setDao().observeForSession(s.id).first().size

        repo.applyRoutine(s.id, rutina.id)
        assertEquals(despuesDeLaPrimera, db.setDao().observeForSession(s.id).first().size)
    }

    @Test
    fun la_rutina_traslada_series_repeticiones_y_minutos_de_cardio() = runTest {
        repo.seedPlan()
        val torso = repo.observeRoutines().first().first { it.name == "Torso A · empuje" }
        val s = repo.sessionForDate(hoy)
        repo.applyRoutine(s.id, torso.id)

        val series = db.setDao().observeForSession(s.id).first()
        val remo = series.filter { it.exerciseId == "remo_ergometro" }
        assertEquals(1, remo.size)
        assertEquals(12, remo.first().minutes)

        val press = series.filter { it.exerciseId == "press_de_pecho_en_maquina" }
        assertEquals(4, press.size)
        assertTrue(press.all { it.reps == 8 })
    }

    @Test
    fun resembrar_retira_las_rutinas_que_ya_no_existen() = runTest {
        repo.createRoutine("Inicio · Día A", "", 4)
        repo.reseedPlan()
        val nombres = repo.observeRoutines().first().map { it.name }
        assertTrue("La rutina retirada sigue ahí", !nombres.contains("Inicio · Día A"))
        assertTrue(nombres.contains("Torso A · empuje"))
    }

    @Test
    fun resembrar_conserva_una_rutina_retirada_si_ya_se_uso() = runTest {
        val vieja = repo.createRoutine("Inicio · Día A", "", 4)
        val s = repo.sessionForDate(hoy)
        repo.updateSession(s.copy(routineId = vieja))

        repo.reseedPlan()

        val nombres = repo.observeRoutines().first().map { it.name }
        assertTrue(
            "Una rutina usada en un entrenamiento no debe borrarse",
            nombres.contains("Inicio · Día A")
        )
    }

    @Test
    fun las_rutinas_sembradas_llevan_su_plan() = runTest {
        repo.seedPlan()
        val rutinas = repo.observeRoutines().first()
        assertTrue(rutinas.all { it.planKey == "recomp" })
        assertEquals("recomp", rutinas.first { it.name == "Torso A · empuje" }.planKey)
    }

    @Test
    fun se_guardan_las_horas_y_se_calcula_la_duracion() = runTest {
        repo.setTimes(hoy, 19 * 60 + 30, 21 * 60)
        val s = repo.sessionForDate(hoy)
        assertTrue(s.hasTimes)
        assertEquals(90, s.durationMinutes)
    }

    @Test
    fun una_sesion_que_cruza_medianoche_no_da_duracion_negativa() = runTest {
        repo.setTimes(hoy, 23 * 60 + 30, 45)
        val s = repo.sessionForDate(hoy)
        assertEquals(75, s.durationMinutes)
    }

    @Test
    fun vaciar_el_dia_borra_las_series_y_suelta_la_rutina() = runTest {
        repo.seedPlan()
        val rutina = repo.observeRoutines().first().first()
        val s = repo.sessionForDate(hoy)
        repo.applyRoutine(s.id, rutina.id)
        assertTrue(db.setDao().observeForSession(s.id).first().isNotEmpty())

        repo.clearDay(s.id)
        assertTrue(db.setDao().observeForSession(s.id).first().isEmpty())
        assertEquals(null, repo.sessionForDate(hoy).routineId)
    }

    @Test
    fun tras_vaciar_se_puede_cargar_otra_rutina() = runTest {
        repo.seedPlan()
        val rutinas = repo.observeRoutines().first()
        val s = repo.sessionForDate(hoy)

        repo.applyRoutine(s.id, rutinas[0].id)
        repo.clearDay(s.id)
        repo.applyRoutine(s.id, rutinas[1].id)

        val esperados = repo.observeRoutineItems(rutinas[1].id).first().map { it.exerciseId }.toSet()
        val reales = db.setDao().observeForSession(s.id).first().map { it.exerciseId }.toSet()
        assertEquals(esperados, reales)
    }

    @Test
    fun resembrar_no_duplica_rutinas() = runTest {
        repo.reseedPlan()
        val primera = repo.observeRoutines().first().size
        repo.reseedPlan()
        assertEquals(primera, repo.observeRoutines().first().size)
    }

    @Test
    fun resembrar_respeta_las_rutinas_creadas_a_mano() = runTest {
        val mia = repo.createRoutine("Mi rutina de casa", "", 3)
        repo.reseedPlan()
        assertTrue(repo.observeRoutines().first().any { it.id == mia })
    }

    @Test
    fun marcar_descanso_vacia_el_dia() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")
        assertTrue(db.setDao().observeForSession(s.id).first().isNotEmpty())

        repo.setRestDay(hoy, true)

        val despues = repo.sessionForDate(hoy)
        assertTrue(despues.restDay)
        assertTrue(db.setDao().observeForSession(despues.id).first().isEmpty())
    }

    @Test
    fun quitar_el_descanso_deja_el_dia_normal() = runTest {
        repo.setRestDay(hoy, true)
        repo.setRestDay(hoy, false)
        val s = repo.sessionForDate(hoy)
        assertTrue(!s.restDay)
        assertEquals("Entrenamiento", s.title)
    }

    @Test
    fun cambiar_de_ejercicio_conserva_series_reps_y_peso() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "press_de_pecho_en_maquina")
        db.setDao().setsFor(s.id, "press_de_pecho_en_maquina").forEach {
            repo.updateSet(it.copy(weight = 55.0, reps = 10, done = true))
        }

        repo.swapExercise(s.id, "press_de_pecho_en_maquina", "press_banca_en_multipower")

        assertTrue(db.setDao().setsFor(s.id, "press_de_pecho_en_maquina").isEmpty())
        val nuevas = db.setDao().setsFor(s.id, "press_banca_en_multipower")
        assertEquals(3, nuevas.size)
        assertEquals(listOf(1, 2, 3), nuevas.map { it.setNumber })
        assertTrue(nuevas.all { it.weight == 55.0 && it.reps == 10 && it.done })
    }

    @Test
    fun cambiar_a_un_ejercicio_que_ya_estaba_no_repite_numeros() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "press_de_pecho_en_maquina")
        repo.addExerciseToSession(s.id, "press_inclinado_en_maquina")

        repo.swapExercise(s.id, "press_de_pecho_en_maquina", "press_inclinado_en_maquina")

        val series = db.setDao().setsFor(s.id, "press_inclinado_en_maquina")
        assertEquals(6, series.size)
        assertEquals(series.size, series.map { it.setNumber }.toSet().size)
    }

    @Test
    fun borrar_una_sesion_se_lleva_sus_series() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")
        assertTrue(db.setDao().observeAll().first().isNotEmpty())

        repo.deleteSession(s.id)
        assertTrue(db.setDao().observeAll().first().isEmpty())
    }

    @Test
    fun borrar_una_rutina_se_lleva_sus_ejercicios() = runTest {
        repo.seedPlan()
        val rutina = repo.observeRoutines().first().first()
        assertTrue(repo.observeRoutineItems(rutina.id).first().isNotEmpty())

        repo.deleteRoutine(rutina.id)
        assertTrue(repo.observeRoutineItems(rutina.id).first().isEmpty())
    }

    @Test
    fun los_datos_de_la_maquina_de_cardio_se_guardan() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "bicicleta_estatica", setsCount = 1)
        val serie = db.setDao().setsFor(s.id, "bicicleta_estatica").first()

        repo.updateSet(
            serie.copy(minutes = 20, distanceKm = 2.81, calories = 214, avgHeartRate = 166, done = true)
        )

        val guardada = db.setDao().setsFor(s.id, "bicicleta_estatica").first()
        assertEquals(20, guardada.minutes)
        assertEquals(2.81, guardada.distanceKm, 0.001)
        assertEquals(214, guardada.calories)
        assertEquals(166, guardada.avgHeartRate)
        assertTrue(guardada.hasCardioData)
    }

    @Test
    fun una_serie_de_fuerza_no_tiene_datos_de_cardio() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")
        val serie = db.setDao().setsFor(s.id, "prensa_de_piernas").first()
        assertTrue(!serie.hasCardioData)
    }

    @Test
    fun dos_pesos_el_mismo_dia_se_sobrescriben() = runTest {
        repo.logWeight(hoy, 75.0)
        repo.logWeight(hoy, 74.2)
        val registros = repo.observeBodyWeight().first()
        assertEquals(1, registros.size)
        assertEquals(74.2, registros.first().weight, 0.001)
    }

    @Test
    fun los_pesos_salen_ordenados_por_fecha() = runTest {
        repo.logWeight(hoy, 75.0)
        repo.logWeight(hoy.minusDays(14), 76.4)
        repo.logWeight(hoy.minusDays(7), 75.8)
        val pesos = repo.observeBodyWeight().first()
        assertEquals(listOf(76.4, 75.8, 75.0), pesos.map { it.weight })
    }

    @Test
    fun el_volumen_solo_cuenta_las_series_marcadas() = runTest {
        val s = repo.sessionForDate(hoy)
        repo.addExerciseToSession(s.id, "prensa_de_piernas")
        val series = db.setDao().setsFor(s.id, "prensa_de_piernas")
        repo.updateSet(series[0].copy(weight = 100.0, reps = 10, done = true))
        repo.updateSet(series[1].copy(weight = 100.0, reps = 10, done = false))

        val volumen = db.setDao().observeForSession(s.id).first()
            .filter { it.done }
            .sumOf { it.volume }
        assertEquals(1000.0, volumen, 0.001)
    }

    @Test
    fun el_plan_inicial_apunta_a_ejercicios_que_existen_en_el_catalogo() = runTest {
        val desconocidos = SeedData.plans
            .flatMap { it.items }
            .map { it.exerciseId }
            .filter { ExerciseCatalog.find(it) == null }
        assertTrue("Ejercicios inexistentes: $desconocidos", desconocidos.isEmpty())

        repo.seedPlan()
        assertEquals(SeedData.plans.size, repo.observeRoutines().first().size)
    }
}
