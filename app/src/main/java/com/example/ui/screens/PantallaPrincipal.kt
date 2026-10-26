package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MateriaViewModel
import com.example.ui.components.DialogoEditarPerfil
import com.example.ui.components.DialogoFormularioMateria
import com.example.ui.components.HeaderAlumno

/**
 * Pantalla principal que integra el Encabezado con datos del Alumno (SharedPreferences),
 * navegación por pestañas (Materias vs. Promedios por Semestre con Room @Query),
 * y los diálogos modales de captura, edición y confirmación de eliminación.
 */
@Composable
fun PantallaPrincipal(
    viewModel: MateriaViewModel,
    modifier: Modifier = Modifier
) {
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val datosAlumno by viewModel.datosAlumno.collectAsStateWithLifecycle()
    val promedioGeneral by viewModel.promedioGeneral.collectAsStateWithLifecycle()
    val promediosPorSemestre by viewModel.promediosPorSemestre.collectAsStateWithLifecycle()
    val totalMaterias by viewModel.totalMaterias.collectAsStateWithLifecycle()
    val totalAprobadas by viewModel.totalAprobadas.collectAsStateWithLifecycle()
    val materiasFiltradas by viewModel.materiasFiltradas.collectAsStateWithLifecycle()
    val semestreFiltro by viewModel.semestreFiltro.collectAsStateWithLifecycle()

    val formularioState by viewModel.formularioState.collectAsStateWithLifecycle()
    val materiaAEliminar by viewModel.materiaAEliminar.collectAsStateWithLifecycle()
    val mostrarDialogoPerfil by viewModel.mostrarDialogoPerfil.collectAsStateWithLifecycle()
    val mensajeSnackbar by viewModel.mensajeSnackbar.collectAsStateWithLifecycle()

    // Mostrar mensajes en Snackbar cuando el ViewModel emita uno
    LaunchedEffect(mensajeSnackbar) {
        mensajeSnackbar?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajeSnackbar()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            // El botón flotante solo se muestra cuando ya hay materias registradas,
            // evitando tener botones duplicados al inicio.
            if (pestanaSeleccionada == 0 && materiasFiltradas.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.prepararNuevaMateria() },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Registrar Materia", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("fab_registrar_materia"),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Encabezado con datos del alumno (SharedPreferences) y métricas globales (Room)
            HeaderAlumno(
                datosAlumno = datosAlumno,
                promedioGeneral = promedioGeneral,
                totalMaterias = totalMaterias,
                totalAprobadas = totalAprobadas,
                onEditarPerfil = { viewModel.abrirDialogoPerfil() }
            )

            // Pestañas de Navegación: Materias | Promedios por Semestre
            TabRow(
                selectedTabIndex = pestanaSeleccionada,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Materias ($totalMaterias)", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_materias")
                )

                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Promedios Semestre", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("tab_promedios")
                )
            }

            // Contenedor de pantallas según la pestaña activa
            Box(modifier = Modifier.fillMaxSize()) {
                when (pestanaSeleccionada) {
                    0 -> PantallaMaterias(
                        materias = materiasFiltradas,
                        semestreFiltro = semestreFiltro,
                        onSeleccionarSemestre = { viewModel.setSemestreFiltro(it) },
                        onEditarMateria = { viewModel.prepararEdicionMateria(it) },
                        onEliminarMateria = { viewModel.solicitarEliminarMateria(it) },
                        onNuevaMateria = { viewModel.prepararNuevaMateria() }
                    )
                    1 -> PantallaPromedios(
                        promedioGeneral = promedioGeneral,
                        promediosPorSemestre = promediosPorSemestre,
                        totalMaterias = totalMaterias,
                        totalAprobadas = totalAprobadas
                    )
                }
            }
        }
    }

    // Diálogo de Formulario para Registrar / Editar Materia
    if (formularioState.mostrarDialogo) {
        DialogoFormularioMateria(
            state = formularioState,
            onActualizarCampo = { nombre, semestre, normal, repeticion, especial ->
                viewModel.actualizarCampoFormulario(nombre, semestre, normal, repeticion, especial)
            },
            onGuardar = { viewModel.guardarMateria() },
            onDismiss = { viewModel.cerrarFormulario() }
        )
    }

    // Diálogo de Confirmación para Eliminar Materia
    materiaAEliminar?.let { materia ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelarEliminacion() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("¿Eliminar materia?")
            },
            text = {
                Text(
                    text = "Se eliminará '${materia.nombre}' (Semestre ${materia.semestre}) del Cardex. Esta acción recalculará los promedios en Room."
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmarEliminarMateria() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("btn_confirmar_eliminar_materia")
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.cancelarEliminacion() },
                    modifier = Modifier.testTag("btn_cancelar_eliminar_materia")
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para Editar Perfil del Alumno (SharedPreferences)
    if (mostrarDialogoPerfil) {
        DialogoEditarPerfil(
            datosActuales = datosAlumno,
            onDismiss = { viewModel.cerrarDialogoPerfil() },
            onGuardar = { nombre, numControl, carrera ->
                viewModel.guardarPerfilAlumno(nombre, numControl, carrera)
            }
        )
    }
}
