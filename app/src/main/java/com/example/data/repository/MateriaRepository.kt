package com.example.data.repository

import com.example.data.dao.MateriaDao
import com.example.data.model.Materia
import com.example.data.model.PromedioSemestre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * ============================================================================
 * EXPLICACIÓN DEL PATRÓN REPOSITORIO (MateriaRepository)
 * ============================================================================
 * El Repositorio actúa como un mediador entre las fuentes de datos (en este caso
 * la base de datos Room a través de MateriaDao) y el ViewModel.
 * 
 * Beneficios:
 * 1. Abstracción: El ViewModel no necesita saber cómo o dónde se guardan los datos.
 * 2. Desacoplamiento: Si se agrega una sincronización remota o caché en memoria,
 *    solo se modifica el repositorio sin alterar la lógica de presentación.
 * 3. Control de hilos: Asegura que las operaciones de inserción, actualización y
 *    eliminación se despachen de forma segura en hilos secundarios (Dispatchers.IO).
 * ============================================================================
 */
class MateriaRepository(private val materiaDao: MateriaDao) {

    /**
     * Flujo reactivo con la lista completa de materias ordenadas por semestre.
     */
    val todasLasMaterias: Flow<List<Materia>> = materiaDao.obtenerTodas()
        .flowOn(Dispatchers.IO)

    /**
     * Flujo reactivo con el promedio general acumulado de la carrera.
     */
    val promedioGeneral: Flow<Double?> = materiaDao.obtenerPromedioGeneral()
        .flowOn(Dispatchers.IO)

    /**
     * Flujo reactivo con el desglose de promedios agrupados por semestre (GROUP BY semestre).
     */
    val promediosPorSemestre: Flow<List<PromedioSemestre>> = materiaDao.obtenerPromediosPorSemestre()
        .flowOn(Dispatchers.IO)

    /**
     * Conteo total de materias registradas.
     */
    val totalMaterias: Flow<Int> = materiaDao.obtenerTotalMaterias()
        .flowOn(Dispatchers.IO)

    /**
     * Conteo total de materias aprobadas.
     */
    val totalAprobadas: Flow<Int> = materiaDao.obtenerTotalAprobadas()
        .flowOn(Dispatchers.IO)

    /**
     * Obtiene una materia por su ID.
     */
    suspend fun obtenerPorId(id: Int): Materia? = withContext(Dispatchers.IO) {
        materiaDao.obtenerPorId(id)
    }

    /**
     * Obtiene materias de un semestre específico.
     */
    fun obtenerPorSemestre(semestre: Int): Flow<List<Materia>> =
        materiaDao.obtenerPorSemestre(semestre).flowOn(Dispatchers.IO)

    /**
     * Inserta una nueva materia en la base de datos Room.
     */
    suspend fun insertar(materia: Materia): Long = withContext(Dispatchers.IO) {
        materiaDao.insertar(materia)
    }

    /**
     * Actualiza los datos de una materia existente.
     */
    suspend fun actualizar(materia: Materia) = withContext(Dispatchers.IO) {
        materiaDao.actualizar(materia)
    }

    /**
     * Elimina una materia de la base de datos Room.
     */
    suspend fun eliminar(materia: Materia) = withContext(Dispatchers.IO) {
        materiaDao.eliminar(materia)
    }

    /**
     * Elimina una materia por su ID único.
     */
    suspend fun eliminarPorId(id: Int) = withContext(Dispatchers.IO) {
        materiaDao.eliminarPorId(id)
    }
}
