package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Materia
import com.example.data.model.PromedioSemestre
import kotlinx.coroutines.flow.Flow

/**
 * ============================================================================
 * EXPLICACIÓN DE ROOM: @Dao (Data Access Object)
 * ============================================================================
 * @Dao: Marca esta interfaz como el componente de acceso a datos de Room.
 * En lugar de escribir sentencias JDBC o SQLiteCursor manualmente, Room genera
 * automáticamente la implementación de todos los métodos en tiempo de compilación.
 * 
 * Anotaciones utilizadas:
 * - @Insert: Genera el código SQL "INSERT INTO materias ...".
 *   onConflict = OnConflictStrategy.REPLACE indica que si ya existe un registro con
 *   la misma PrimaryKey, se reemplaza. Se declara como 'suspend' para ejecutarse
 *   en un hilo secundario sin bloquear el hilo principal (UI).
 * 
 * - @Update: Genera el código SQL "UPDATE materias SET ... WHERE id = :id".
 *   Actualiza los valores de la materia basándose en su ID. También es 'suspend'.
 * 
 * - @Delete: Genera el código SQL "DELETE FROM materias WHERE id = :id".
 *   Elimina el registro de la entidad proporcionada. Es 'suspend'.
 * 
 * - @Query: Permite ejecutar sentencias SQL personalizadas (SELECT, DELETE, etc.).
 *   Room valida la sintaxis SQL y los nombres de las columnas en tiempo de compilación.
 *   Al retornar Flow<T>, Room observa la base de datos de manera reactiva: cualquier
 *   cambio (insert, update o delete) emitirá automáticamente la nueva lista de
 *   datos hacia los observadores (ViewModel y Compose UI).
 * ============================================================================
 */
@Dao
interface MateriaDao {

    /**
     * @Query para obtener todas las materias registradas ordenadas por semestre
     * y alfabéticamente por nombre.
     * Retorna Flow para actualización automática y reactiva en la UI.
     */
    @Query("SELECT * FROM materias ORDER BY semestre ASC, nombre ASC")
    fun obtenerTodas(): Flow<List<Materia>>

    /**
     * @Query para obtener una materia específica por su ID.
     */
    @Query("SELECT * FROM materias WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Materia?

    /**
     * @Query para filtrar materias por un semestre específico.
     */
    @Query("SELECT * FROM materias WHERE semestre = :semestre ORDER BY nombre ASC")
    fun obtenerPorSemestre(semestre: Int): Flow<List<Materia>>

    /**
     * @Query para calcular el Promedio General de la carrera.
     * Regla académica: Se toma la última calificación existente de cada materia
     * (Especial > Repetición > Normal), lograda mediante COALESCE.
     * Solo se toman en cuenta materias que tengan al menos una calificación registrada.
     */
    @Query("""
        SELECT AVG(COALESCE(calificacionEspecial, calificacionRepeticion, calificacionNormal))
        FROM materias
        WHERE calificacionEspecial IS NOT NULL 
           OR calificacionRepeticion IS NOT NULL 
           OR calificacionNormal IS NOT NULL
    """)
    fun obtenerPromedioGeneral(): Flow<Double?>

    /**
     * @Query para calcular el promedio agrupado por cada semestre (GROUP BY semestre).
     * Además del promedio, calcula el total de materias cursadas y cuántas fueron aprobadas (>= 70).
     */
    @Query("""
        SELECT 
            semestre,
            AVG(COALESCE(calificacionEspecial, calificacionRepeticion, calificacionNormal)) AS promedio,
            COUNT(*) AS totalMaterias,
            SUM(CASE WHEN COALESCE(calificacionEspecial, calificacionRepeticion, calificacionNormal) >= 70.0 THEN 1 ELSE 0 END) AS aprobadas
        FROM materias
        WHERE calificacionEspecial IS NOT NULL 
           OR calificacionRepeticion IS NOT NULL 
           OR calificacionNormal IS NOT NULL
        GROUP BY semestre
        ORDER BY semestre ASC
    """)
    fun obtenerPromediosPorSemestre(): Flow<List<PromedioSemestre>>

    /**
     * @Query para obtener el conteo total de materias registradas en el Cardex.
     */
    @Query("SELECT COUNT(*) FROM materias")
    fun obtenerTotalMaterias(): Flow<Int>

    /**
     * @Query para obtener el conteo de materias aprobadas (calificación vigente >= 70.0).
     */
    @Query("""
        SELECT COUNT(*) 
        FROM materias 
        WHERE COALESCE(calificacionEspecial, calificacionRepeticion, calificacionNormal) >= 70.0
    """)
    fun obtenerTotalAprobadas(): Flow<Int>

    /**
     * @Insert para registrar una nueva materia en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(materia: Materia): Long

    /**
     * @Update para actualizar los datos de una materia existente.
     */
    @Update
    suspend fun actualizar(materia: Materia)

    /**
     * @Delete para eliminar una materia recibiendo el objeto Entity.
     */
    @Delete
    suspend fun eliminar(materia: Materia)

    /**
     * @Query para eliminar una materia directamente por su ID único.
     */
    @Query("DELETE FROM materias WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
