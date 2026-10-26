package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Materia
import com.example.data.model.PromedioSemestre
import com.example.data.preferences.DatosAlumno
import com.example.data.preferences.PreferenciasAlumno
import com.example.data.repository.MateriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado del formulario de captura y edición de materia.
 */
data class FormularioMateriaState(
    val id: Int = 0,
    val nombre: String = "",
    val semestre: String = "1",
    val calificacionNormal: String = "",
    val calificacionRepeticion: String = "",
    val calificacionEspecial: String = "",
    val errorNombre: String? = null,
    val errorSemestre: String? = null,
    val errorNormal: String? = null,
    val errorRepeticion: String? = null,
    val errorEspecial: String? = null,
    val esEdicion: Boolean = false,
    val mostrarDialogo: Boolean = false
)

/**
 * ============================================================================
 * VIEWMODEL: MateriaViewModel (Arquitectura MVVM)
 * ============================================================================
 * El ViewModel gestiona el estado de la UI y la lógica de negocio. Sobrevive a
 * cambios de configuración de Android (como rotaciones de pantalla) y comunica
 * la vista (Jetpack Compose) con el Repositorio de datos y SharedPreferences.
 * ============================================================================
 */
class MateriaViewModel(
    private val repository: MateriaRepository,
    private val preferenciasAlumno: PreferenciasAlumno
) : ViewModel() {

    // Estado del alumno obtenido desde SharedPreferences
    val datosAlumno: StateFlow<DatosAlumno> = preferenciasAlumno.datosAlumno

    // Lista completa de materias desde Room
    val todasLasMaterias: StateFlow<List<Materia>> = repository.todasLasMaterias
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Promedio general de la carrera calculado por Room mediante @Query SQL
    val promedioGeneral: StateFlow<Double?> = repository.promedioGeneral
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Lista de promedios agrupados por semestre (GROUP BY) calculados por Room
    val promediosPorSemestre: StateFlow<List<PromedioSemestre>> = repository.promediosPorSemestre
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Conteo total de materias registradas
    val totalMaterias: StateFlow<Int> = repository.totalMaterias
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Conteo de materias aprobadas
    val totalAprobadas: StateFlow<Int> = repository.totalAprobadas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Filtro de semestre seleccionado en la UI (0 = Todos)
    private val _semestreFiltro = MutableStateFlow(0)
    val semestreFiltro: StateFlow<Int> = _semestreFiltro.asStateFlow()

    // Materias filtradas según el semestre seleccionado
    val materiasFiltradas: StateFlow<List<Materia>> = combine(
        todasLasMaterias,
        _semestreFiltro
    ) { materias, semestre ->
        if (semestre == 0) materias else materias.filter { it.semestre == semestre }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Estado reactivo del formulario de captura / edición
    private val _formularioState = MutableStateFlow(FormularioMateriaState())
    val formularioState: StateFlow<FormularioMateriaState> = _formularioState.asStateFlow()

    // Control del diálogo de confirmación para eliminar
    private val _materiaAEliminar = MutableStateFlow<Materia?>(null)
    val materiaAEliminar: StateFlow<Materia?> = _materiaAEliminar.asStateFlow()

    // Control del diálogo de edición de perfil del alumno
    private val _mostrarDialogoPerfil = MutableStateFlow(false)
    val mostrarDialogoPerfil: StateFlow<Boolean> = _mostrarDialogoPerfil.asStateFlow()

    // Control de mensaje snackbar o notificación
    private val _mensajeSnackbar = MutableStateFlow<String?>(null)
    val mensajeSnackbar: StateFlow<String?> = _mensajeSnackbar.asStateFlow()

    fun setSemestreFiltro(semestre: Int) {
        _semestreFiltro.value = semestre
    }

    fun limpiarMensajeSnackbar() {
        _mensajeSnackbar.value = null
    }

    /**
     * Guarda la información del alumno en SharedPreferences (fuera de Room).
     */
    fun guardarPerfilAlumno(nombre: String, numeroControl: String, carrera: String) {
        if (nombre.isBlank() || numeroControl.isBlank()) {
            _mensajeSnackbar.value = "Nombre y número de control no pueden estar vacíos"
            return
        }
        preferenciasAlumno.guardarDatos(nombre, numeroControl, carrera)
        _mostrarDialogoPerfil.value = false
        _mensajeSnackbar.value = "Datos del alumno actualizados"
    }

    fun abrirDialogoPerfil() {
        _mostrarDialogoPerfil.value = true
    }

    fun cerrarDialogoPerfil() {
        _mostrarDialogoPerfil.value = false
    }

    /**
     * Prepara el formulario para registrar una nueva materia.
     */
    fun prepararNuevaMateria(semestreSugerido: Int = 1) {
        val sem = if (_semestreFiltro.value > 0) _semestreFiltro.value else semestreSugerido
        _formularioState.value = FormularioMateriaState(
            id = 0,
            nombre = "",
            semestre = sem.toString(),
            calificacionNormal = "",
            calificacionRepeticion = "",
            calificacionEspecial = "",
            esEdicion = false,
            mostrarDialogo = true
        )
    }

    /**
     * Prepara el formulario para editar una materia existente.
     */
    fun prepararEdicionMateria(materia: Materia) {
        _formularioState.value = FormularioMateriaState(
            id = materia.id,
            nombre = materia.nombre,
            semestre = materia.semestre.toString(),
            calificacionNormal = materia.calificacionNormal?.toString() ?: "",
            calificacionRepeticion = materia.calificacionRepeticion?.toString() ?: "",
            calificacionEspecial = materia.calificacionEspecial?.toString() ?: "",
            esEdicion = true,
            mostrarDialogo = true
        )
    }

    fun cerrarFormulario() {
        _formularioState.value = _formularioState.value.copy(mostrarDialogo = false)
    }

    fun actualizarCampoFormulario(
        nombre: String? = null,
        semestre: String? = null,
        normal: String? = null,
        repeticion: String? = null,
        especial: String? = null
    ) {
        _formularioState.value = _formularioState.value.copy(
            nombre = nombre ?: _formularioState.value.nombre,
            semestre = semestre ?: _formularioState.value.semestre,
            calificacionNormal = normal ?: _formularioState.value.calificacionNormal,
            calificacionRepeticion = repeticion ?: _formularioState.value.calificacionRepeticion,
            calificacionEspecial = especial ?: _formularioState.value.calificacionEspecial,
            errorNombre = null,
            errorSemestre = null,
            errorNormal = null,
            errorRepeticion = null,
            errorEspecial = null
        )
    }

    /**
     * Valida y guarda la materia en la base de datos Room.
     */
    fun guardarMateria() {
        val state = _formularioState.value

        var hayError = false
        var errorNombre: String? = null
        var errorSemestre: String? = null
        var errorNormal: String? = null
        var errorRepeticion: String? = null
        var errorEspecial: String? = null

        if (state.nombre.trim().isBlank()) {
            errorNombre = "El nombre de la materia es obligatorio"
            hayError = true
        }

        val semInt = state.semestre.trim().toIntOrNull()
        if (semInt == null || semInt <= 0 || semInt > 20) {
            errorSemestre = "Semestre inválido (ingrese de 1 a 20)"
            hayError = true
        }

        fun validarCalificacion(califStr: String): Pair<Double?, String?> {
            val trimmed = califStr.trim()
            if (trimmed.isEmpty()) return Pair(null, null)
            val valor = trimmed.toDoubleOrNull()
            return when {
                valor == null -> Pair(null, "Debe ser un número válido")
                valor < 0.0 || valor > 100.0 -> Pair(null, "Debe estar entre 0 y 100")
                else -> Pair(valor, null)
            }
        }

        val (valNormal, errNormal) = validarCalificacion(state.calificacionNormal)
        if (errNormal != null) {
            errorNormal = errNormal
            hayError = true
        }

        val (valRepeticion, errRepeticion) = validarCalificacion(state.calificacionRepeticion)
        if (errRepeticion != null) {
            errorRepeticion = errRepeticion
            hayError = true
        }

        val (valEspecial, errEspecial) = validarCalificacion(state.calificacionEspecial)
        if (errEspecial != null) {
            errorEspecial = errEspecial
            hayError = true
        }

        if (hayError) {
            _formularioState.value = state.copy(
                errorNombre = errorNombre,
                errorSemestre = errorSemestre,
                errorNormal = errorNormal,
                errorRepeticion = errorRepeticion,
                errorEspecial = errorEspecial
            )
            return
        }

        val materiaAGuardar = Materia(
            id = if (state.esEdicion) state.id else 0,
            nombre = state.nombre.trim(),
            semestre = semInt!!,
            calificacionNormal = valNormal,
            calificacionRepeticion = valRepeticion,
            calificacionEspecial = valEspecial
        )

        viewModelScope.launch {
            if (state.esEdicion) {
                repository.actualizar(materiaAGuardar)
                _mensajeSnackbar.value = "Materia '${materiaAGuardar.nombre}' actualizada"
            } else {
                repository.insertar(materiaAGuardar)
                _mensajeSnackbar.value = "Materia '${materiaAGuardar.nombre}' registrada"
            }
            _formularioState.value = FormularioMateriaState(mostrarDialogo = false)
        }
    }

    /**
     * Abre el diálogo de confirmación para eliminar una materia.
     */
    fun solicitarEliminarMateria(materia: Materia) {
        _materiaAEliminar.value = materia
    }

    fun cancelarEliminacion() {
        _materiaAEliminar.value = null
    }

    /**
     * Confirma y ejecuta la eliminación de la materia en Room.
     */
    fun confirmarEliminarMateria() {
        val materia = _materiaAEliminar.value ?: return
        viewModelScope.launch {
            repository.eliminar(materia)
            _mensajeSnackbar.value = "Materia '${materia.nombre}' eliminada"
            _materiaAEliminar.value = null
        }
    }

    /**
     * Carga materias de prueba para facilitar la demostración de la aplicación
     * con calificaciones en curso normal, repetición y especial.
     */
    fun cargarMateriasDeEjemplo() {
        viewModelScope.launch {
            val ejemplos = listOf(
                Materia(nombre = "Cálculo Diferencial", semestre = 1, calificacionNormal = 85.0),
                Materia(nombre = "Fundamentos de Programación", semestre = 1, calificacionNormal = 95.0),
                Materia(nombre = "Taller de Ética", semestre = 1, calificacionNormal = 90.0),
                Materia(nombre = "Química", semestre = 1, calificacionNormal = 65.0, calificacionRepeticion = 78.0),
                Materia(nombre = "Cálculo Integral", semestre = 2, calificacionNormal = 58.0, calificacionRepeticion = 82.0),
                Materia(nombre = "Programación Orientada a Objetos", semestre = 2, calificacionNormal = 98.0),
                Materia(nombre = "Álgebra Lineal", semestre = 2, calificacionNormal = 88.0),
                Materia(nombre = "Estructura de Datos", semestre = 3, calificacionNormal = 92.0),
                Materia(nombre = "Cálculo Vectorial", semestre = 3, calificacionNormal = 50.0, calificacionRepeticion = 60.0, calificacionEspecial = 74.0),
                Materia(nombre = "Sistemas Operativos", semestre = 3, calificacionNormal = 89.0),
                Materia(nombre = "Tópicos Avanzados de Programación", semestre = 4, calificacionNormal = 94.0),
                Materia(nombre = "Fundamentos de Bases de Datos", semestre = 4, calificacionNormal = 91.0)
            )
            for (materia in ejemplos) {
                repository.insertar(materia)
            }
            _mensajeSnackbar.value = "Materias de ejemplo agregadas al Cardex"
        }
    }
}

/**
 * Factory para instanciar MateriaViewModel con sus dependencias requeridas
 * sin necesidad de frameworks pesados de inyección.
 */
class MateriaViewModelFactory(
    private val repository: MateriaRepository,
    private val preferenciasAlumno: PreferenciasAlumno
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MateriaViewModel::class.java)) {
            return MateriaViewModel(repository, preferenciasAlumno) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}
