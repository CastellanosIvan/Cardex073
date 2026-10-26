package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ============================================================================
 * EXPLICACIÓN DE ROOM: @Entity
 * ============================================================================
 * @Entity: Marca esta clase de Kotlin como una entidad que representa una tabla
 * en la base de datos relacional SQLite de Room.
 * 
 * - tableName: Define el nombre explícito de la tabla en la base de datos ("materias").
 * - Cada propiedad de la data class se convertirá automáticamente en una columna
 *   de la tabla, a menos que se ignore con @Ignore.
 * - @PrimaryKey(autoGenerate = true): Define la clave primaria única para cada registro.
 *   Con autoGenerate = true, SQLite incrementa automáticamente el ID numérico
 *   cada vez que se inserta una nueva materia sin necesidad de asignarlo manualmente.
 * ============================================================================
 */
@Entity(tableName = "materias")
data class Materia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val semestre: Int,
    val calificacionNormal: Double? = null,
    val calificacionRepeticion: Double? = null,
    val calificacionEspecial: Double? = null
) {
    /**
     * Calificación vigente de acuerdo al reglamento académico de Cardex universitario:
     * La última evaluación presentada prevalece sobre las anteriores
     * (Especial > Repetición > Normal).
     */
    val calificacionVigente: Double?
        get() = calificacionEspecial ?: calificacionRepeticion ?: calificacionNormal

    /**
     * Tipo de acreditación o curso mediante el cual se obtuvo la última calificación.
     */
    val tipoAcreditacion: String
        get() = when {
            calificacionEspecial != null -> "Especial"
            calificacionRepeticion != null -> "Repetición"
            calificacionNormal != null -> "Normal"
            else -> "Sin calificar"
        }

    /**
     * Determina si la materia está aprobada (calificación mínima aprobatoria: 70.0).
     */
    val estaAprobada: Boolean
        get() = (calificacionVigente ?: 0.0) >= 70.0

    /**
     * Estado legible para la interfaz gráfica.
     */
    val estadoTexto: String
        get() = when {
            calificacionVigente == null -> "En curso / Sin calificar"
            estaAprobada -> "Aprobada"
            else -> "Reprobada"
        }
}

/**
 * Modelo POJO (Plain Old Java Object) para almacenar el resultado de la consulta
 * agregada GROUP BY semestre con Room.
 */
data class PromedioSemestre(
    val semestre: Int,
    val promedio: Double,
    val totalMaterias: Int,
    val aprobadas: Int
)
