package com.alejandro.appgym.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions WHERE date = :epochDay LIMIT 1")
    suspend fun findByDate(epochDay: Long): WorkoutSession?

    @Query("SELECT * FROM sessions WHERE date = :epochDay LIMIT 1")
    fun observeByDate(epochDay: Long): Flow<WorkoutSession?>

    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<WorkoutSession?>

    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): WorkoutSession?

    @Query("SELECT DISTINCT routineId FROM sessions WHERE routineId IS NOT NULL")
    suspend fun routineIdsInUse(): List<Long>

    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun observeAll(): Flow<List<WorkoutSession>>

    /**
     * IGNORE en vez de fallar: si dos acciones simultáneas intentan crear la sesión
     * del mismo día, gana la primera y la segunda reutiliza esa.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(session: WorkoutSession): Long

    @Update
    suspend fun update(session: WorkoutSession)

    @Delete
    suspend fun delete(session: WorkoutSession)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface SetDao {

    @Query("SELECT * FROM set_entries WHERE sessionId = :sessionId ORDER BY position, setNumber")
    fun observeForSession(sessionId: Long): Flow<List<SetEntry>>

    @Query("SELECT * FROM set_entries ORDER BY id")
    fun observeAll(): Flow<List<SetEntry>>

    @Query("SELECT * FROM set_entries WHERE exerciseId = :exerciseId ORDER BY createdAt")
    fun observeForExercise(exerciseId: String): Flow<List<SetEntry>>

    @Query("SELECT COALESCE(MAX(position), -1) FROM set_entries WHERE sessionId = :sessionId")
    suspend fun maxPosition(sessionId: Long): Int

    @Query("SELECT * FROM set_entries WHERE exerciseId = :exerciseId AND done = 1 ORDER BY createdAt DESC LIMIT 1")
    suspend fun lastDone(exerciseId: String): SetEntry?

    @Query("SELECT COUNT(*) FROM set_entries WHERE sessionId = :sessionId AND exerciseId = :exerciseId")
    suspend fun countFor(sessionId: Long, exerciseId: String): Int

    @Query("SELECT * FROM set_entries WHERE sessionId = :sessionId AND exerciseId = :exerciseId ORDER BY setNumber")
    suspend fun setsFor(sessionId: Long, exerciseId: String): List<SetEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SetEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<SetEntry>)

    @Update
    suspend fun update(entry: SetEntry)

    @Delete
    suspend fun delete(entry: SetEntry)

    @Query("DELETE FROM set_entries WHERE sessionId = :sessionId AND exerciseId = :exerciseId")
    suspend fun deleteExercise(sessionId: Long, exerciseId: String)

    @Query("DELETE FROM set_entries WHERE sessionId = :sessionId")
    suspend fun deleteAllFor(sessionId: Long)
}

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routines ORDER BY createdAt")
    fun observeAll(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<Routine?>

    @Query("SELECT * FROM routine_items WHERE routineId = :routineId ORDER BY position")
    fun observeItems(routineId: Long): Flow<List<RoutineItem>>

    @Query("SELECT * FROM routine_items ORDER BY routineId, position")
    fun observeAllItems(): Flow<List<RoutineItem>>

    @Query("SELECT * FROM routine_items WHERE routineId = :routineId ORDER BY position")
    suspend fun itemsOf(routineId: Long): List<RoutineItem>

    @Query("SELECT COALESCE(MAX(position), -1) FROM routine_items WHERE routineId = :routineId")
    suspend fun maxItemPosition(routineId: Long): Int

    @Query("SELECT * FROM routines WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Routine?

    @Insert
    suspend fun insert(routine: Routine): Long

    @Update
    suspend fun update(routine: Routine)

    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutine(id: Long)

    @Insert
    suspend fun insertItem(item: RoutineItem): Long

    @Update
    suspend fun updateItem(item: RoutineItem)

    @Delete
    suspend fun deleteItem(item: RoutineItem)
}

@Dao
interface BodyWeightDao {

    @Query("SELECT * FROM body_weight ORDER BY date")
    fun observeAll(): Flow<List<BodyWeight>>

    @Query("SELECT COUNT(*) FROM body_weight")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: BodyWeight): Long

    @Query("DELETE FROM body_weight WHERE id = :id")
    suspend fun deleteById(id: Long)
}
