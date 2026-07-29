package com.alejandro.appgym.data

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
        reps: Int = 10,
        weight: Double = 0.0
    ) {
        val existing = sets.countFor(sessionId, exerciseId)
        if (existing > 0) {
            addSet(sessionId, exerciseId)
            return
        }
        val pos = sets.maxPosition(sessionId) + 1
        val last = lastPerformance(exerciseId)
        val r = last?.reps ?: reps
        val w = last?.weight ?: weight
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
     * Solo toca rutinas que creó la propia app y que siguen intactas: si una rutina
     * antigua ya se usó en algún entrenamiento, se conserva para no romper el historial.
     * Las rutinas creadas a mano nunca se tocan.
     */
    suspend fun reseedPlan() {
        val usadas = sessions.routineIdsInUse().toSet()
        for (nombre in SeedData.obsoleteNames) {
            val vieja = routines.findByName(nombre) ?: continue
            if (vieja.id !in usadas) routines.deleteRoutine(vieja.id)
        }
        seedPlan()
    }

    /**
     * Inserta el plan actual. Si una rutina ya existe por nombre, no se duplica pero
     * sí se actualizan sus datos de cabecera (día de la semana, color): si no,
     * un cambio en el plan nunca llegaría a quien ya tiene la rutina creada.
     */
    suspend fun seedPlan() {
        for (plan in SeedData.plans) {
            val existente = routines.findByName(plan.name)
            if (existente != null) {
                routines.update(
                    existente.copy(
                        planKey = plan.key,
                        dayHint = plan.dayHint,
                        accent = plan.accent
                    )
                )
                continue
            }
            val routineId = routines.insert(
                Routine(
                    name = plan.name,
                    planKey = plan.key,
                    dayHint = plan.dayHint,
                    accent = plan.accent
                )
            )
            plan.items.forEachIndexed { index, item ->
                routines.insertItem(
                    RoutineItem(
                        routineId = routineId,
                        exerciseId = item.exerciseId,
                        position = index,
                        targetSets = item.sets,
                        targetReps = item.reps,
                        targetMinutes = item.minutes,
                        restSeconds = item.rest,
                        note = item.note
                    )
                )
            }
        }
    }

    /** Vuelca todos los ejercicios de una rutina en la sesión indicada. */
    suspend fun applyRoutine(sessionId: Long, routineId: Long) {
        val items = routines.itemsOf(routineId)
        var pos = sets.maxPosition(sessionId) + 1
        val entries = mutableListOf<SetEntry>()
        for (item in items) {
            if (sets.countFor(sessionId, item.exerciseId) > 0) continue
            repeat(item.targetSets) { i ->
                entries += SetEntry(
                    sessionId = sessionId,
                    exerciseId = item.exerciseId,
                    position = pos,
                    setNumber = i + 1,
                    reps = item.targetReps,
                    weight = item.targetWeight,
                    minutes = item.targetMinutes
                )
            }
            pos++
        }
        if (entries.isNotEmpty()) sets.insertAll(entries)
    }
}
