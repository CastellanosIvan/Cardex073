package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FormularioMateriaState

/**
 * Diálogo modal para la captura y edición de materias con validación completa.
 */
@Composable
fun DialogoFormularioMateria(
    state: FormularioMateriaState,
    onActualizarCampo: (nombre: String?, semestre: String?, normal: String?, repeticion: String?, especial: String?) -> Unit,
    onGuardar: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!state.mostrarDialogo) return

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (state.esEdicion) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.esEdicion) "Editar Materia" else "Registrar Materia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campo: Nombre de la materia
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { onActualizarCampo(it, null, null, null, null) },
                    label = { Text("Nombre de la materia *") },
                    placeholder = { Text("Ej. Estructuras de Datos") },
                    leadingIcon = { Icon(Icons.Default.Book, contentDescription = null) },
                    isError = state.errorNombre != null,
                    supportingText = { if (state.errorNombre != null) Text(state.errorNombre) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_materia_nombre"),
                    singleLine = true
                )

                // Campo: Semestre
                Column {
                    OutlinedTextField(
                        value = state.semestre,
                        onValueChange = { onActualizarCampo(null, it, null, null, null) },
                        label = { Text("Semestre (1 - 20) *") },
                        placeholder = { Text("Ej. 3") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.errorSemestre != null,
                        supportingText = { if (state.errorSemestre != null) Text(state.errorSemestre) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_materia_semestre"),
                        singleLine = true
                    )

                    // Selector rápido de semestres comunes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (sem in 1..9) {
                            FilterChip(
                                selected = state.semestre == sem.toString(),
                                onClick = { onActualizarCampo(null, sem.toString(), null, null, null) },
                                label = { Text("Sem $sem", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // Sección informativa de calificaciones
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Las calificaciones son opcionales (0 - 100). Prevalencia académica: Especial > Repetición > Normal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Campo: Calificación Curso Normal
                OutlinedTextField(
                    value = state.calificacionNormal,
                    onValueChange = { onActualizarCampo(null, null, it, null, null) },
                    label = { Text("Calificación Curso Normal") },
                    placeholder = { Text("Ej. 85.0 (Opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = state.errorNormal != null,
                    supportingText = { if (state.errorNormal != null) Text(state.errorNormal) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_calificacion_normal"),
                    singleLine = true
                )

                // Campo: Calificación Repetición
                OutlinedTextField(
                    value = state.calificacionRepeticion,
                    onValueChange = { onActualizarCampo(null, null, null, it, null) },
                    label = { Text("Calificación Repetición (2do intento)") },
                    placeholder = { Text("Ej. 70.0 (Opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = state.errorRepeticion != null,
                    supportingText = { if (state.errorRepeticion != null) Text(state.errorRepeticion) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_calificacion_repeticion"),
                    singleLine = true
                )

                // Campo: Calificación Especial
                OutlinedTextField(
                    value = state.calificacionEspecial,
                    onValueChange = { onActualizarCampo(null, null, null, null, it) },
                    label = { Text("Calificación Especial (3er intento)") },
                    placeholder = { Text("Ej. 75.0 (Opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = state.errorEspecial != null,
                    supportingText = { if (state.errorEspecial != null) Text(state.errorEspecial) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_calificacion_especial"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onGuardar,
                modifier = Modifier.testTag("btn_guardar_materia")
            ) {
                Text(if (state.esEdicion) "Guardar Cambios" else "Registrar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancelar_materia")
            ) {
                Text("Cancelar")
            }
        }
    )
}
