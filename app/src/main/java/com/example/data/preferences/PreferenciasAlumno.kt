package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ============================================================================
 * GESTIÓN DE DATOS PERMANENTES CON SharedPreferences
 * ============================================================================
 * Cumple con el requisito estricto:
 * - El número de control y el nombre del alumno NO deben residir en la base de datos Room.
 * - Se guardan localmente en el almacenamiento de clave-valor seguro de Android (SharedPreferences).
 * - Permite detectar el primer inicio de la app para solicitar estos datos.
 * - Mantiene un StateFlow reactivo para actualizar la UI en tiempo real cuando
 *   el alumno edite sus datos.
 * ============================================================================
 */
data class DatosAlumno(
    val nombre: String = "",
    val numeroControl: String = "",
    val carrera: String = "Ingeniería en Sistemas Computacionales",
    val estaRegistrado: Boolean = false
)

class PreferenciasAlumno(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _datosAlumno = MutableStateFlow(cargarDatos())
    val datosAlumno: StateFlow<DatosAlumno> = _datosAlumno.asStateFlow()

    companion object {
        private const val PREFS_NAME = "cardex_alumno_prefs"
        private const val KEY_NOMBRE = "key_nombre_alumno"
        private const val KEY_NUM_CONTROL = "key_numero_control"
        private const val KEY_CARRERA = "key_carrera"
        private const val KEY_REGISTRADO = "key_esta_registrado"
    }

    /**
     * Carga inicial síncrona de los datos almacenados en SharedPreferences.
     */
    private fun cargarDatos(): DatosAlumno {
        val nombre = prefs.getString(KEY_NOMBRE, "") ?: ""
        val numControl = prefs.getString(KEY_NUM_CONTROL, "") ?: ""
        val carrera = prefs.getString(KEY_CARRERA, "Ingeniería en Sistemas Computacionales") ?: "Ingeniería en Sistemas Computacionales"
        val registrado = prefs.getBoolean(KEY_REGISTRADO, false) && nombre.isNotBlank() && numControl.isNotBlank()

        return DatosAlumno(
            nombre = nombre,
            numeroControl = numControl,
            carrera = carrera,
            estaRegistrado = registrado
        )
    }

    /**
     * Guarda permanentemente el nombre, número de control y carrera del alumno
     * en SharedPreferences y actualiza el flujo reactivo.
     */
    fun guardarDatos(nombre: String, numeroControl: String, carrera: String = "Ingeniería en Sistemas Computacionales") {
        prefs.edit()
            .putString(KEY_NOMBRE, nombre.trim())
            .putString(KEY_NUM_CONTROL, numeroControl.trim().uppercase())
            .putString(KEY_CARRERA, carrera.trim())
            .putBoolean(KEY_REGISTRADO, true)
            .apply()

        _datosAlumno.value = DatosAlumno(
            nombre = nombre.trim(),
            numeroControl = numeroControl.trim().uppercase(),
            carrera = carrera.trim(),
            estaRegistrado = true
        )
    }

    /**
     * Obtiene de forma directa si el usuario ya realizó su registro inicial.
     */
    fun estaRegistrado(): Boolean = _datosAlumno.value.estaRegistrado

    /**
     * Obtiene el nombre actual guardado.
     */
    fun obtenerNombre(): String = _datosAlumno.value.nombre

    /**
     * Obtiene el número de control actual guardado.
     */
    fun obtenerNumeroControl(): String = _datosAlumno.value.numeroControl
}
