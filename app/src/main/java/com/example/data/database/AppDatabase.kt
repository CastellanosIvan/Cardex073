package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.MateriaDao
import com.example.data.model.Materia

/**
 * ============================================================================
 * EXPLICACIÓN DE ROOM: @Database y RoomDatabase
 * ============================================================================
 * @Database: Anota la clase principal que sirve como punto de acceso central a la
 * base de datos persistente SQLite subyacente.
 * 
 * - entities: Arreglo de todas las clases @Entity asociadas a esta base de datos
 *   (en este caso, solo [Materia::class] para la tabla 'materias').
 * - version: Número entero que representa la versión del esquema. Si en el futuro
 *   se agregan o modifican columnas, este número debe incrementarse y proveerse
 *   migraciones.
 * - exportSchema: Indica si Room debe exportar el esquema en formato JSON durante
 *   la compilación (útil para control de versiones en proyectos de producción).
 * 
 * Patrón Singleton (Instancia Única):
 * - Se implementa para asegurar que solo exista una instancia de AppDatabase abierta
 *   en toda la aplicación, evitando fugas de memoria y bloqueos de concurrencia en SQLite.
 * - @Volatile garantiza que cualquier cambio en la variable INSTANCE sea visible
 *   inmediatamente para todos los hilos de ejecución de la JVM.
 * - synchronized(this) previene condiciones de carrera donde dos hilos intenten crear
 *   la base de datos al mismo tiempo.
 * ============================================================================
 */
@Database(
    entities = [Materia::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Método abstracto que expone el DAO de Materias.
     * Room genera el código de implementación concreto automáticamente.
     */
    abstract fun materiaDao(): MateriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia Singleton de la base de datos Room.
         * Si no existe, la crea utilizando Room.databaseBuilder.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cardex_database" // Nombre del archivo SQLite en el almacenamiento interno
                )
                    // En caso de discrepancia de versión sin migración definida, recrea las tablas
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
