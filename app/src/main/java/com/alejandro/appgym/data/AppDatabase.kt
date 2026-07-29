package com.alejandro.appgym.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [WorkoutSession::class, SetEntry::class, Routine::class, RoutineItem::class, BodyWeight::class],
    version = 7,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao
    abstract fun setDao(): SetDao
    abstract fun routineDao(): RoutineDao
    abstract fun bodyWeightDao(): BodyWeightDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Un día = una sesión. Antes dos acciones simultáneas podían crear dos sesiones
         * para la misma fecha y la segunda quedaba invisible en la pantalla Hoy.
         * Reasigna las series a la sesión más antigua de cada día antes de borrar duplicados.
         */
        /** Horas de entrada y salida, y agrupación de rutinas por plan. */
        /** Datos que dan las máquinas de cardio al terminar. */
        /** Días marcados como descanso. */
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sessions ADD COLUMN restDay INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE set_entries ADD COLUMN distanceKm REAL NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE set_entries ADD COLUMN calories INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE set_entries ADD COLUMN avgHeartRate INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sessions ADD COLUMN startMinute INTEGER NOT NULL DEFAULT -1")
                db.execSQL("ALTER TABLE sessions ADD COLUMN endMinute INTEGER NOT NULL DEFAULT -1")
                db.execSQL("ALTER TABLE routines ADD COLUMN planKey TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "UPDATE set_entries SET sessionId = (" +
                        "SELECT MIN(s2.id) FROM sessions s2 WHERE s2.date = (" +
                        "SELECT s3.date FROM sessions s3 WHERE s3.id = set_entries.sessionId)) " +
                        "WHERE EXISTS (SELECT 1 FROM sessions s4 WHERE s4.id = set_entries.sessionId)"
                )
                db.execSQL("DELETE FROM sessions WHERE id NOT IN (SELECT MIN(id) FROM sessions GROUP BY date)")
                db.execSQL("DROP INDEX IF EXISTS index_sessions_date")
                db.execSQL("CREATE UNIQUE INDEX index_sessions_date ON sessions (date)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS body_weight (" +
                        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "date INTEGER NOT NULL, " +
                        "weight REAL NOT NULL, " +
                        "note TEXT NOT NULL DEFAULT '')"
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_body_weight_date ON body_weight (date)")
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE routine_items ADD COLUMN targetMinutes INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appgym.db"
                ).addMigrations(
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
                    MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
