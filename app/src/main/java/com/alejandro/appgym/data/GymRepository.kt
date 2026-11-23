package com.alejandro.appgym.data

import com.alejandro.appgym.util.Progresion
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class GymRepository(
    private val sessions: SessionDao,
    private val sets: SetDao,
    private val routines: RoutineDao,
    private val bodyWeight: BodyWeightDao
) {

    /* ---------------- Peso corporal ---------------- */

    fun observeBodyWeight(): Flow<List<BodyWeight>> = bodyWeight.observeAll()

    suspend fun logWeight(date: LocalDate, kg: Double, note: String = "") {
        bodyWeight.insert(BodyWeight(date = date.toEpochDay(), weight = kg, note = note))
    }

    suspend fun deleteWeight(id: Long) = bodyWeight.deleteById(id)

    suspend fun hasWeightEntries(): Boolean = bodyWeight.count() > 0

    /* ---------------- Sesiones ---------------- */

    fun observeSessions(): Flow<List<WorkoutSession>> = sessions.observeAll()

    fun observeSession(id: Long): Flow<WorkoutSession?> = sessions.observeById(id)

    fun observeSets(sessionId: Long): Flow<List<SetEntry>> = sets.observeForSession(sessionId)

    fun observeAllSets(): Flow<List<SetEntry>> = sets.observeAll()

    fun observeSetsForExercise(exerciseId: String): Flow<List<SetEntry>> =
        sets.observeForExercise(exerciseId)

    /** Devuelve la sesión del día, creándola si aún no existe. */
    suspend fun sessionForDate(date: LocalDate, title: String = "Entrenamiento"): WorkoutSession {
        val day = date.toEpochDay()
        sessions.findByDate(day)?.let { return it }
        val id = sessions.insert(WorkoutSession(date = day, title = title))
        return sessions.findByDate(day) ?: WorkoutSession(id = id, date = day, title = title)
    }

    suspend fun updateSession(session: WorkoutSession) = sessions.update(session)

    suspend fun deleteSession(id: Long) = sessions.deleteById(id)

    /* ---------------- Series ---------------- */

    /** Añade un ejercicio a la sesión con [setsCount] series vacías. */
    suspend fun addExerciseToSession(
        sessionId: Long,
        exerciseId: String,
        setsCount: Int = 3,
        /** El alto del rango; 12 es el genérico que también usa la tarjeta. */
        reps: Int = 12,
        weight: Double = 0.0
    ) {
        val existing = sets.countFor(sessionId, exerciseId)
        if (existing > 0) {
            addSet(sessionId, exerciseId)
            return
        }
        val pos = sets.maxPosition(sessionId) + 1
        // Mismo cálculo que la tarjeta (GymViewModel.objetivos), con el 3 × 10-12
        // genérico de los ejercicios sueltos: si no, la tarjeta sugería un peso y
        // las series venían rellenas con otro.
        val dia = sessions.findById(sessionId)?.date?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now()
        val ultima = lastSession(exerciseId, dia)
        val fechaUltima = ultima.firstOrNull()?.let { sessions.findById(it.sessionId)?.date }
        val sugerencia = Progresion.sugerir(
            ejercicio = ExerciseCatalog.find(exerciseId),
            ultimaSesion = ultima,
            objetivo = reps.takeIf { it > 0 } ?: 12,
            series = setsCount,
            pesoInicial = weight,
            diasSinHacer = fechaUltima?.let { dia.toEpochDay() - it }
        )
        val w = sugerencia.kg
        val r = if (ultima.isEmpty() && weight == 0.0) reps
        else Progresion.repsDeArranque(sugerencia, ultima)
        sets.insertAll(
            (1..setsCount).map {
                SetEntry(
                    sessionId = sessionId,
                    exerciseId = exerciseId,
                    position = pos,
                    setNumber = it,
                    reps = r,
                    weight = w
                )
            }
        )
    }

    suspend fun addSet(sessionId: Long, exerciseId: String) {
        val current = sets.setsFor(sessionId, exerciseId)
        val template = current.lastOrNull()
        sets.insert(
            SetEntry(
                sessionId = sessionId,
                exerciseId = exerciseId,
                position = template?.position ?: (sets.maxPosition(sessionId) + 1),
                setNumber = (template?.setNumber ?: 0) + 1,
                reps = template?.reps ?: 10,
                weight = template?.weight ?: 0.0
            )
        )
    }

    suspend fun updateSet(entry: SetEntry) = sets.update(entry)

    /**
     * Al completar una serie, las que quedan pendientes de ese ejercicio copian su
     * peso y sus repeticiones. Casi siempre repites la misma carga, así que no hay
     * que volver a teclearla en cada serie.
     */
    suspend fun propagateToPendingSets(entry: SetEntry) {
        val pendientes = sets.setsFor(entry.sessionId, entry.exerciseId)
            .filter { !it.done && it.setNumber > entry.setNumber }
        for (serie in pendientes) {
            if (serie.weight == entry.weight && serie.reps == entry.reps) continue
            sets.update(serie.copy(weight = entry.weight, reps = entry.reps))
        }
    }

    suspend fun deleteSet(entry: SetEntry) = sets.delete(entry)

    /** Vuelve a insertar una serie borrada, conservando su posición y número. */
    suspend fun restoreSet(entry: SetEntry) {
        sets.insert(entry)
    }

    suspend fun removeExercise(sessionId: Long, exerciseId: String) =
        sets.deleteExercise(sessionId, exerciseId)

    /** Última vez que se hizo el ejercicio (para pre-rellenar peso y reps). */
    suspend fun lastPerformance(exerciseId: String): SetEntry? = sets.lastDone(exerciseId)

    /* ---------------- Rutinas ---------------- */

    fun observeRoutines(): Flow<List<Routine>> = routines.observeAll()

    fun observeRoutine(id: Long): Flow<Routine?> = routines.observeById(id)

    fun observeRoutineItems(id: Long): Flow<List<RoutineItem>> = routines.observeItems(id)

    fun observeAllRoutineItems(): Flow<List<RoutineItem>> = routines.observeAllItems()

    suspend fun createRoutine(name: String, dayHint: String, accent: Int): Long =
        routines.insert(Routine(name = name, dayHint = dayHint, accent = accent))

    /* ---------------- Horas de la sesión ---------------- */

    suspend fun setTimes(date: LocalDate, start: Int, end: Int) {
        val session = sessionForDate(date)
        sessions.update(session.copy(startMinute = start, endMinute = end))
    }

    /** Marca o desmarca el día como descanso. Al marcarlo se vacía de ejercicios. */
    suspend fun setRestDay(date: LocalDate, rest: Boolean) {
        val session = sessionForDate(date)
        if (rest) sets.deleteAllFor(session.id)
        sessions.update(
            session.copy(
                restDay = rest,
                routineId = if (rest) null else session.routineId,
                title = if (rest) "Descanso" else "Entrenamiento"
            )
        )
    }

    /**
     * Cambia un ejercicio por otro conservando series, repeticiones y peso.
     * Útil cuando la máquina está ocupada o el movimiento te molesta.
     */
    suspend fun swapExercise(sessionId: Long, fromId: String, toId: String) {
        if (fromId == toId) return
        val actuales = sets.setsFor(sessionId, fromId)
        if (actuales.isEmpty()) return
        // Si el destino ya estaba en el día, se fusionan renumerando.
        val yaHabia = sets.setsFor(sessionId, toId)
        val base = yaHabia.size
        val posicion = yaHabia.firstOrNull()?.position ?: actuales.first().position
        sets.deleteExercise(sessionId, fromId)
        sets.insertAll(
            actuales.mapIndexed { i, serie ->
                serie.copy(
                    id = 0,
                    exerciseId = toId,
                    position = posicion,
                    setNumber = base + i + 1
                )
            }
        )
    }

    /** Vacía el día: borra todas las series y desvincula la rutina. */
    suspend fun clearDay(sessionId: Long) {
        sets.deleteAllFor(sessionId)
        sessions.findById(sessionId)?.let { sessions.update(it.copy(routineId = null, title = "Entrenamiento")) }
    }

    suspend fun updateRoutine(routine: Routine) = routines.update(routine)

    suspend fun deleteRoutine(id: Long) = routines.deleteRoutine(id)

    suspend fun addExerciseToRoutine(routineId: Long, exerciseId: String) {
        val pos = routines.maxItemPosition(routineId) + 1
        routines.insertItem(RoutineItem(routineId = routineId, exerciseId = exerciseId, position = pos))
    }

    suspend fun updateRoutineItem(item: RoutineItem) = routines.updateItem(item)

    suspend fun deleteRoutineItem(item: RoutineItem) = routines.deleteItem(item)

    /**
     * Sustituye el plan automático por la versión actual de [SeedData].
     *
     * Las rutinas retiradas se borran aunque se hayan usado: el historial no depende
     * de ellas (cada sesión guarda su propio título y sus series), así que lo único
     * que se suelta es la referencia. Las rutinas creadas a mano nunca se tocan.
     */
    suspend fun reseedPlan() {
        for (nombre in SeedData.obsoleteNames) {
            val vieja = routines.findByName(nombre) ?: continue
            sessions.detachRoutine(vieja.id)
            routines.deleteRoutine(vieja.id)
        }
        seedPlan()
    }

    /**
     * Inserta el plan actual. Si una rutina ya existe por nombre, no se duplica pero
     * sí se actualizan sus datos de cabecera (color): si no, un cambio en el plan
     * nunca llegaría a quien ya tiene la rutina creada.
     */
    suspend fun seedPlan() {
        for (plan in SeedData.plans) {
            val existente = routines.findByName(plan.name)
            if (existente != null) {
                routines.update(
                    existente.copy(planKey = plan.key, dayHint = "", accent = plan.accent)
                )
                continue
            }
            val routineId = routines.insert(
                Routine(name = plan.name, planKey = plan.key, dayHint = "", accent = plan.accent)
            )
            plan.items.forEachIndexed { index, item ->
                routines.insertItem(
                    RoutineItem(
                        routineId = routineId,
                        exerciseId = item.exerciseId,
                        position = index,
                        targetSets = item.sets,
                        targetReps = item.reps,
                        targetWeight = item.kg,
                        targetMinutes = item.minutes,
                        restSeconds = item.rest,
                        note = item.note
                    )
                )
            }
        }
    }

    /** Series completadas de un ejercicio en la última sesión anterior a [antesDe]. */
    suspend fun lastSession(exerciseId: String, antesDe: LocalDate): List<SetEntry> =
        sets.lastSessionDone(exerciseId, antesDe.toEpochDay())

    /**
     * Vuelca todos los ejercicios de una rutina en la sesión indicada, con el peso y
     * las repeticiones que sugiere [Progresion] a partir de la última vez.
     */
    suspend fun applyRoutine(sessionId: Long, routineId: Long) {
        val items = routines.itemsOf(routineId)
        val dia = sessions.findById(sessionId)?.date?.let { LocalDate.ofEpochDay(it) }
            ?: LocalDate.now()
        var pos = sets.maxPosition(sessionId) + 1
        val entries = mutableListOf<SetEntry>()
        for (item in items) {
            if (sets.countFor(sessionId, item.exerciseId) > 0) continue
            val ultima = lastSession(item.exerciseId, dia)
            val fechaUltima = ultima.firstOrNull()?.let { sessions.findById(it.sessionId)?.date }
            val sugerencia = Progresion.sugerir(
                ejercicio = ExerciseCatalog.find(item.exerciseId),
                ultimaSesion = ultima,
                objetivo = item.targetReps,
                series = item.targetSets,
                pesoInicial = item.targetWeight,
                diasSinHacer = fechaUltima?.let { dia.toEpochDay() - it }
            )
            val peso = sugerencia.kg
            val reps = if (item.targetReps > 0) Progresion.repsDeArranque(sugerencia, ultima) else 0
            repeat(item.targetSets) { i ->
                entries += SetEntry(
                    sessionId = sessionId,
                    exerciseId = item.exerciseId,
                    position = pos,
                    setNumber = i + 1,
                    reps = reps,
                    weight = peso,
                    minutes = item.targetMinutes
                )
            }
            pos++
        }
        if (entries.isNotEmpty()) sets.insertAll(entries)
    }
}
