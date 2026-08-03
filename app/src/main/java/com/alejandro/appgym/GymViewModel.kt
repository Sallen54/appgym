package com.alejandro.appgym

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alejandro.appgym.data.AppDatabase
import com.alejandro.appgym.data.BodyWeight
import com.alejandro.appgym.data.ExerciseBlock
import com.alejandro.appgym.data.GymRepository
import com.alejandro.appgym.data.Routine
import com.alejandro.appgym.data.RoutineItem
import com.alejandro.appgym.data.SeedData
import com.alejandro.appgym.data.SetEntry
import com.alejandro.appgym.data.WorkoutSession
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Profile(
    val name: String,
    val heightCm: Int,
    val age: Int
) {
    /** Fórmula de Tanaka, más fiable que 220 − edad. */
    val maxHeartRate: Int get() = (208 - 0.7 * age).toInt()

    /** Rango de zona 2: el ritmo cómodo en el que se puede hablar. */
    val zone2: IntRange get() = (maxHeartRate * 0.60).toInt()..(maxHeartRate * 0.72).toInt()

    fun zoneOf(bpm: Int): String = when {
        bpm <= 0 -> ""
        bpm < maxHeartRate * 0.60 -> "suave"
        bpm < maxHeartRate * 0.72 -> "zona 2"
        bpm < maxHeartRate * 0.82 -> "moderado"
        bpm < maxHeartRate * 0.89 -> "umbral"
        else -> "máximo"
    }
}

data class DayUiState(
    val date: LocalDate = LocalDate.now(),
    val session: WorkoutSession? = null,
    val blocks: List<ExerciseBlock> = emptyList()
) {
    val totalVolume: Double get() = blocks.sumOf { it.volume }
    val totalSets: Int get() = blocks.sumOf { it.sets.size }
    val doneSets: Int get() = blocks.sumOf { it.doneSets }
    val isEmpty: Boolean get() = blocks.isEmpty()
}

@OptIn(ExperimentalCoroutinesApi::class)
class GymViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.get(app)
    private val repo = GymRepository(db.sessionDao(), db.setDao(), db.routineDao(), db.bodyWeightDao())

    private val prefs = app.getSharedPreferences("appgym", android.content.Context.MODE_PRIVATE)

    init {
        // La resiembra solo afecta a quien ya tiene el plan puesto. En una
        // instalación nueva no se siembra nada hasta que la bienvenida lo diga:
        // el plan es el de Alejandro y no todo el mundo lo quiere.
        if (prefs.contains(KEY_SEED_VERSION) &&
            prefs.getInt(KEY_SEED_VERSION, 0) < SeedData.VERSION
        ) {
            viewModelScope.launch {
                repo.reseedPlan()
                prefs.edit().putInt(KEY_SEED_VERSION, SeedData.VERSION).apply()
            }
        }
    }

    /* ---------------- Bienvenida ---------------- */

    /**
     * Quien ya había abierto la app antes de que existiera la bienvenida no debe
     * verla: se le da por hecha si tiene una versión de plan guardada.
     */
    private val _onboarded = MutableStateFlow(
        prefs.getBoolean(KEY_ONBOARDED, prefs.contains(KEY_SEED_VERSION))
    )
    val onboarded: StateFlow<Boolean> = _onboarded.asStateFlow()

    /**
     * Cierra la bienvenida. Todos los datos son opcionales: un 0 o un texto vacío
     * significa "no lo digo" y se queda el valor por defecto.
     */
    fun completeOnboarding(
        name: String,
        heightCm: Int,
        age: Int,
        weightKg: Double,
        conPlan: Boolean
    ) = viewModelScope.launch {
        val perfil = Profile(
            name = name.trim(),
            heightCm = if (heightCm > 0) heightCm else DEFAULT_HEIGHT,
            age = if (age > 0) age else DEFAULT_AGE
        )
        prefs.edit()
            .putString(KEY_NAME, perfil.name)
            .putInt(KEY_HEIGHT, perfil.heightCm)
            .putInt(KEY_AGE, perfil.age)
            // Se marca sembrado en los dos casos: quien empieza en blanco no debe
            // encontrarse el plan apareciendo solo en la siguiente actualización.
            .putInt(KEY_SEED_VERSION, SeedData.VERSION)
            .putBoolean(KEY_ONBOARDED, true)
            .apply()
        _profile.value = perfil
        if (weightKg > 0) repo.logWeight(LocalDate.now(), weightKg)
        if (conPlan) repo.seedPlan()
        _onboarded.value = true
    }

    /* ---------------- Perfil ---------------- */

    private val _profile = MutableStateFlow(
        Profile(
            name = prefs.getString(KEY_NAME, "").orEmpty(),
            heightCm = prefs.getInt(KEY_HEIGHT, DEFAULT_HEIGHT),
            age = prefs.getInt(KEY_AGE, DEFAULT_AGE)
        )
    )
    val profile: StateFlow<Profile> = _profile.asStateFlow()

    fun saveProfile(name: String, heightCm: Int, age: Int) {
        prefs.edit()
            .putString(KEY_NAME, name)
            .putInt(KEY_HEIGHT, heightCm)
            .putInt(KEY_AGE, age)
            .apply()
        _profile.value = Profile(name, heightCm, age)
    }

    /* ---------------- Peso corporal ---------------- */

    val bodyWeights: StateFlow<List<BodyWeight>> = repo.observeBodyWeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun logWeight(kg: Double, date: LocalDate = LocalDate.now()) = viewModelScope.launch {
        if (kg > 0) repo.logWeight(date, kg)
    }

    fun deleteWeight(id: Long) = viewModelScope.launch { repo.deleteWeight(id) }

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    /** Sesión del día seleccionado (se crea en cuanto se añade el primer ejercicio). */
    private val currentSession: StateFlow<WorkoutSession?> = _selectedDate
        .flatMapLatest { date -> db.sessionDao().observeByDate(date.toEpochDay()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val dayState: StateFlow<DayUiState> = currentSession
        .flatMapLatest { session ->
            val setsFlow: Flow<List<SetEntry>> =
                if (session == null) flowOf(emptyList()) else repo.observeSets(session.id)
            setsFlow.map { sets -> session to sets }
        }
        .combine(_selectedDate) { (session, sets), date ->
            DayUiState(date = date, session = session, blocks = toBlocks(sets))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DayUiState())

    val sessions: StateFlow<List<WorkoutSession>> = repo.observeSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allSets: StateFlow<List<SetEntry>> = repo.observeAllSets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Volumen de la última sesión anterior a la seleccionada, para comparar. */
    val previousVolume: StateFlow<Double> = combine(sessions, allSets, _selectedDate) { ses, sets, date ->
        val anterior = ses.filter { it.date < date.toEpochDay() }.maxByOrNull { it.date }
            ?: return@combine 0.0
        sets.filter { it.sessionId == anterior.id && it.done }.sumOf { it.volume }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    val routines: StateFlow<List<Routine>> = repo.observeRoutines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /* ---------------- Plan activo ---------------- */

    /**
     * Qué plan está siguiendo. Lo elige él: deducirlo del historial daba semanas
     * que no cuadraban con la realidad.
     */
    private val _activePlan = MutableStateFlow(
        // Si el plan guardado se retiró en una versión posterior, se vuelve al actual
        // en vez de dejar la pantalla en blanco.
        prefs.getString(KEY_ACTIVE_PLAN, SeedData.DEFAULT_KEY)
            ?.takeIf { SeedData.groupOf(it) != null }
            ?: SeedData.DEFAULT_KEY
    )
    val activePlan: StateFlow<String> = _activePlan.asStateFlow()

    fun setActivePlan(key: String) {
        prefs.edit().putString(KEY_ACTIVE_PLAN, key).apply()
        _activePlan.value = key
    }

    /** Días distintos en los que ha entrenado de verdad. Un dato, no una regla. */
    val trainedDays: StateFlow<Int> = sessions
        .map { list -> list.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val routineItems: StateFlow<List<RoutineItem>> = repo.observeAllRoutineItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private fun toBlocks(sets: List<SetEntry>): List<ExerciseBlock> =
        sets.groupBy { it.exerciseId }
            .map { (id, list) ->
                ExerciseBlock(
                    exerciseId = id,
                    position = list.minOf { it.position },
                    sets = list.sortedBy { it.setNumber }
                )
            }
            .sortedBy { it.position }

    /* ---------------- Acciones del día ---------------- */

    fun selectDate(date: LocalDate) { _selectedDate.value = date }

    fun shiftDay(days: Long) { _selectedDate.value = _selectedDate.value.plusDays(days) }

    fun addExercise(exerciseId: String) = viewModelScope.launch {
        val session = repo.sessionForDate(_selectedDate.value)
        repo.addExerciseToSession(session.id, exerciseId)
    }

    fun addExercises(ids: List<String>) = viewModelScope.launch {
        val session = repo.sessionForDate(_selectedDate.value)
        ids.forEach { repo.addExerciseToSession(session.id, it) }
    }

    fun addSet(exerciseId: String) = viewModelScope.launch {
        val session = repo.sessionForDate(_selectedDate.value)
        repo.addSet(session.id, exerciseId)
    }

    fun updateSet(entry: SetEntry) = viewModelScope.launch { repo.updateSet(entry) }

    fun toggleDone(entry: SetEntry) = viewModelScope.launch {
        val marcando = !entry.done
        repo.updateSet(entry.copy(done = marcando))
        // Al marcar, las series que quedan heredan peso y repeticiones.
        if (marcando) repo.propagateToPendingSets(entry)
    }

    fun deleteSet(entry: SetEntry) = viewModelScope.launch { repo.deleteSet(entry) }

    /** Borra una serie guardándola para poder deshacer. */
    fun deleteSetWithUndo(entry: SetEntry) = viewModelScope.launch {
        repo.deleteSet(entry)
        _ultimaBorrada.value = entry
    }

    private val _ultimaBorrada = MutableStateFlow<SetEntry?>(null)
    val ultimaBorrada: StateFlow<SetEntry?> = _ultimaBorrada.asStateFlow()

    fun deshacerBorrado() = viewModelScope.launch {
        val entry = _ultimaBorrada.value ?: return@launch
        repo.restoreSet(entry)
        _ultimaBorrada.value = null
    }

    fun descartarDeshacer() { _ultimaBorrada.value = null }

    /** Quita la última serie del ejercicio. Si solo queda una, no hace nada. */
    fun removeLastSet(exerciseId: String) = viewModelScope.launch {
        val bloque = dayState.value.blocks.firstOrNull { it.exerciseId == exerciseId } ?: return@launch
        if (bloque.sets.size <= 1) return@launch
        repo.deleteSet(bloque.sets.last())
    }

    fun removeExercise(exerciseId: String) = viewModelScope.launch {
        val session = currentSession.value ?: return@launch
        repo.removeExercise(session.id, exerciseId)
    }

    fun renameSession(title: String) = viewModelScope.launch {
        val session = repo.sessionForDate(_selectedDate.value)
        repo.updateSession(session.copy(title = title))
    }

    fun setNotes(notes: String) = viewModelScope.launch {
        val session = repo.sessionForDate(_selectedDate.value)
        repo.updateSession(session.copy(notes = notes))
    }

    fun finishSession() = viewModelScope.launch {
        val session = currentSession.value ?: return@launch
        repo.updateSession(session.copy(finished = !session.finished))
    }

    fun deleteSession(id: Long) = viewModelScope.launch { repo.deleteSession(id) }

    fun setTimes(start: Int, end: Int) = viewModelScope.launch {
        repo.setTimes(_selectedDate.value, start, end)
    }

    fun toggleRestDay() = viewModelScope.launch {
        val actual = dayState.value.session?.restDay ?: false
        repo.setRestDay(_selectedDate.value, !actual)
    }

    fun swapExercise(fromId: String, toId: String) = viewModelScope.launch {
        val session = currentSession.value ?: return@launch
        repo.swapExercise(session.id, fromId, toId)
    }

    /** Días marcados como descanso, para pintarlos en la tira. */
    val restDays: StateFlow<Set<Long>> = sessions
        .map { list -> list.filter { it.restDay }.map { it.date }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    /** Vacía el día para poder cargar otra rutina. */
    fun clearDay() = viewModelScope.launch {
        val session = currentSession.value ?: return@launch
        repo.clearDay(session.id)
    }

    fun startFromRoutine(routineId: Long) = viewModelScope.launch {
        val session = repo.sessionForDate(_selectedDate.value)
        val routine = routines.value.firstOrNull { it.id == routineId }
        if (routine != null && session.title == "Entrenamiento") {
            repo.updateSession(session.copy(title = routine.name, routineId = routineId))
        }
        repo.applyRoutine(session.id, routineId)
    }

    /* ---------------- Rutinas ---------------- */

    fun createRoutine(name: String, dayHint: String, accent: Int, onCreated: (Long) -> Unit = {}) =
        viewModelScope.launch {
            val id = repo.createRoutine(name.ifBlank { "Nueva rutina" }, dayHint, accent)
            onCreated(id)
        }

    fun updateRoutine(routine: Routine) = viewModelScope.launch { repo.updateRoutine(routine) }

    fun deleteRoutine(id: Long) = viewModelScope.launch { repo.deleteRoutine(id) }

    fun addToRoutine(routineId: Long, exerciseIds: List<String>) = viewModelScope.launch {
        exerciseIds.forEach { repo.addExerciseToRoutine(routineId, it) }
    }

    fun updateRoutineItem(item: RoutineItem) = viewModelScope.launch { repo.updateRoutineItem(item) }

    fun deleteRoutineItem(item: RoutineItem) = viewModelScope.launch { repo.deleteRoutineItem(item) }

    companion object {

        private const val KEY_SEED_VERSION = "version_del_plan"
        private const val KEY_NAME = "perfil_nombre"
        private const val KEY_HEIGHT = "perfil_altura_cm"
        private const val KEY_AGE = "perfil_edad"
        private const val KEY_ACTIVE_PLAN = "plan_activo"
        private const val KEY_ONBOARDED = "bienvenida_hecha"

        /** Se usan solo si alguien se salta la bienvenida. */
        private const val DEFAULT_HEIGHT = 175
        private const val DEFAULT_AGE = 30

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: androidx.lifecycle.viewmodel.CreationExtras
            ): T {
                val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                return GymViewModel(app) as T
            }
        }
    }
}
