package com.alejandro.appgym.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Un día de entrenamiento. `date` se guarda como epochDay (LocalDate.toEpochDay()). */
@Entity(tableName = "sessions", indices = [Index(value = ["date"], unique = true)])
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val title: String = "Entrenamiento",
    val notes: String = "",
    val routineId: Long? = null,
    val finished: Boolean = false,
    /** Día marcado como descanso. No es lo mismo que un día sin registrar. */
    val restDay: Boolean = false,
    /** Minutos desde medianoche. -1 = sin registrar. */
    val startMinute: Int = -1,
    val endMinute: Int = -1
) {
    val hasTimes: Boolean get() = startMinute >= 0 && endMinute >= 0

    /** Duración en minutos, contemplando sesiones que cruzan la medianoche. */
    val durationMinutes: Int
        get() = if (!hasTimes) 0
        else if (endMinute >= startMinute) endMinute - startMinute
        else (24 * 60 - startMinute) + endMinute
}

/** Una serie concreta de un ejercicio dentro de una sesión. */
@Entity(
    tableName = "set_entries",
    indices = [Index("sessionId"), Index("exerciseId")],
    foreignKeys = [ForeignKey(
        entity = WorkoutSession::class,
        parentColumns = ["id"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SetEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseId: String,
    @ColumnInfo(name = "position") val position: Int = 0,
    val setNumber: Int = 1,
    val reps: Int = 10,
    val weight: Double = 0.0,
    val minutes: Int = 0,
    /* Solo para cardio. 0 = sin registrar. */
    val distanceKm: Double = 0.0,
    val calories: Int = 0,
    val avgHeartRate: Int = 0,
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val volume: Double get() = reps * weight

    val hasCardioData: Boolean get() = distanceKm > 0 || calories > 0 || avgHeartRate > 0
}

/** Plantilla de entrenamiento planificada. */
@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** Clave del plan de [SeedData] al que pertenece, o "" si la creaste tú. */
    val planKey: String = "",
    val dayHint: String = "",
    val accent: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "routine_items",
    indices = [Index("routineId")],
    foreignKeys = [ForeignKey(
        entity = Routine::class,
        parentColumns = ["id"],
        childColumns = ["routineId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RoutineItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long,
    val exerciseId: String,
    @ColumnInfo(name = "position") val position: Int = 0,
    val targetSets: Int = 4,
    val targetReps: Int = 10,
    val targetWeight: Double = 0.0,
    val targetMinutes: Int = 0,
    val restSeconds: Int = 90,
    val note: String = ""
)

/** Registro de peso corporal. Una entrada por día como máximo. */
@Entity(tableName = "body_weight", indices = [Index(value = ["date"], unique = true)])
data class BodyWeight(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val weight: Double,
    val note: String = ""
)

/* ---------- Modelos auxiliares (no persistidos) ---------- */

data class ExerciseBlock(
    val exerciseId: String,
    val position: Int,
    val sets: List<SetEntry>
) {
    val volume: Double get() = sets.filter { it.done }.sumOf { it.volume }
    val doneSets: Int get() = sets.count { it.done }
}

data class SessionSummary(
    val session: WorkoutSession,
    val totalSets: Int,
    val volume: Double,
    val exerciseIds: List<String>
)

data class DayVolume(val epochDay: Long, val volume: Double)

data class ExercisePr(
    val exerciseId: String,
    val bestWeight: Double,
    val bestReps: Int,
    val bestVolume: Double,
    val lastEpochDay: Long
)
