package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.DatosAlumno

/**
 * Pantalla inicial de bienvenida que se muestra exclusivamente en el primer inicio de la app,
 * cuando los datos del alumno aún no existen en SharedPreferences.
 */
@Composable
fun PantallaBienvenida(
    onGuardar: (nombre: String, numControl: String, carrera: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var nombre by remember { mutableStateOf("") }
    var numControl by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("Ingeniería en Sistemas Computacionales") }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorNumControl by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Graduación",
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "¡Bienvenido a Mi Cardex073!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Para comenzar el control de tus calificaciones universitarias, ingresa tus datos de estudiante. Estos se guardarán permanentemente en tu dispositivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        errorNombre = null
                    },
                    label = { Text("Nombre Completo del Alumno *") },
                    placeholder = { Text("Ej. Iván Núñez") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    isError = errorNombre != null,
                    supportingText = {
                        if (errorNombre != null) Text(errorNombre!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_nombre_inicial"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = numControl,
                    onValueChange = {
                        numControl = it.uppercase()
                        errorNumControl = null
                    },
                    label = { Text("Número de Control *") },
                    placeholder = { Text("Ej. 21010073") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null)
                    },
                    isError = errorNumControl != null,
                    supportingText = {
                        if (errorNumControl != null) Text(errorNumControl!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_control_inicial"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = carrera,
                    onValueChange = { carrera = it },
                    label = { Text("Carrera / Especialidad") },
                    placeholder = { Text("Ej. Ing. en Sistemas") },
                    leadingIcon = {
                        Icon(Icons.Default.School, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_carrera_inicial"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        var valido = true
                        if (nombre.trim().isBlank()) {
                            errorNombre = "El nombre es obligatorio"
                            valido = false
                        }
                        if (numControl.trim().isBlank()) {
                            errorNumControl = "El número de control es obligatorio"
                            valido = false
                        }
                        if (valido) {
                            onGuardar(nombre.trim(), numControl.trim(), carrera.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_comenzar_cardex"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Comenzar mi Cardex",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Diálogo emergente para editar los datos del alumno en cualquier momento
 * desde el encabezado principal de la aplicación.
 */
@Composable
fun DialogoEditarPerfil(
    datosActuales: DatosAlumno,
    onDismiss: () -> Unit,
    onGuardar: (nombre: String, numControl: String, carrera: String) -> Unit
) {
    var nombre by remember(datosActuales) { mutableStateOf(datosActuales.nombre) }
    var numControl by remember(datosActuales) { mutableStateOf(datosActuales.numeroControl) }
    var carrera by remember(datosActuales) { mutableStateOf(datosActuales.carrera) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorNumControl by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar Perfil del Alumno",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Los datos se almacenan en SharedPreferences (fuera de Room).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        errorNombre = null
                    },
                    label = { Text("Nombre del Alumno *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    isError = errorNombre != null,
                    supportingText = { if (errorNombre != null) Text(errorNombre!!) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_nombre_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = numControl,
                    onValueChange = {
                        numControl = it.uppercase()
                        errorNumControl = null
                    },
                    label = { Text("Número de Control *") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    isError = errorNumControl != null,
                    supportingText = { if (errorNumControl != null) Text(errorNumControl!!) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_control_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = carrera,
                    onValueChange = { carrera = it },
                    label = { Text("Carrera") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_carrera_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var valido = true
                    if (nombre.trim().isBlank()) {
                        errorNombre = "El nombre es obligatorio"
                        valido = false
                    }
                    if (numControl.trim().isBlank()) {
                        errorNumControl = "El número de control es obligatorio"
                        valido = false
                    }
                    if (valido) {
                        onGuardar(nombre.trim(), numControl.trim(), carrera.trim())
                    }
                },
                modifier = Modifier.testTag("dialog_perfil_guardar_btn")
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_perfil_cancelar_btn")
            ) {
                Text("Cancelar")
            }
        }
    )
}
