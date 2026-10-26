package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Materia
import com.example.ui.theme.GradeFailing
import com.example.ui.theme.GradeFailingContainer
import com.example.ui.theme.GradePassing
import com.example.ui.theme.GradePassingContainer
import java.util.Locale

/**
 * Pantalla que muestra el listado de materias del Cardex con filtrado por semestre,
 * visualización clara de notas en Normal, Repetición y Especial, y acciones de edición/eliminación.
 */
@Composable
fun PantallaMaterias(
    materias: List<Materia>,
    semestreFiltro: Int,
    onSeleccionarSemestre: (Int) -> Unit,
    onEditarMateria: (Materia) -> Unit,
    onEliminarMateria: (Materia) -> Unit,
    onNuevaMateria: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Barra de filtros por semestre
        FilaFiltrosSemestre(
            semestreFiltro = semestreFiltro,
            onSeleccionarSemestre = onSeleccionarSemestre,
            materias = materias
        )

        if (materias.isEmpty()) {
            EstadoVacioMaterias(
                semestreFiltro = semestreFiltro,
                onNuevaMateria = onNuevaMateria
            )
        } else {
            // Agrupación o listado por semestre
            val materiasPorSemestre = materias.groupBy { it.semestre }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("lista_materias"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                materiasPorSemestre.forEach { (semestre, listaMaterias) ->
                    item(key = "header_semestre_$semestre") {
                        EncabezadoGrupoSemestre(
                            semestre = semestre,
                            cantidad = listaMaterias.size
                        )
                    }

                    items(listaMaterias, key = { it.id }) { materia ->
                        TarjetaMateriaItem(
                            materia = materia,
                            onEditar = { onEditarMateria(materia) },
                            onEliminar = { onEliminarMateria(materia) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaFiltrosSemestre(
    semestreFiltro: Int,
    onSeleccionarSemestre: (Int) -> Unit,
    materias: List<Materia>
) {
    val semestresExistentes = materias.map { it.semestre }.distinct().sorted()
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = semestreFiltro == 0,
            onClick = { onSeleccionarSemestre(0) },
            label = { Text("Todos (${materias.size})", fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("filtro_semestre_todos")
        )

        // Botones rápidos para semestres encontrados en la base de datos o estándar 1..8
        val semestresAMostrar = if (semestresExistentes.isNotEmpty()) {
            semestresExistentes
        } else {
            (1..8).toList()
        }

        semestresAMostrar.forEach { sem ->
            val conteo = materias.count { it.semestre == sem }
            FilterChip(
                selected = semestreFiltro == sem,
                onClick = { onSeleccionarSemestre(sem) },
                label = {
                    Text(
                        if (conteo > 0) "Sem $sem ($conteo)" else "Sem $sem",
                        fontWeight = if (semestreFiltro == sem) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag("filtro_semestre_$sem")
            )
        }
    }
}

@Composable
private fun EncabezadoGrupoSemestre(semestre: Int, cantidad: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Semestre $semestre",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "$cantidad ${if (cantidad == 1) "materia" else "materias"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TarjetaMateriaItem(
    materia: Materia,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val califVigente = materia.calificacionVigente

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("item_materia_${materia.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fila superior: Nombre de la materia y Calificación Vigente
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = materia.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Semestre ${materia.semestre} • Estado: ${materia.estadoTexto}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Badge de Calificación Vigente
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                califVigente == null -> MaterialTheme.colorScheme.surfaceVariant
                                materia.estaAprobada -> GradePassingContainer
                                else -> GradeFailingContainer
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (califVigente != null) String.format(Locale.US, "%.1f", califVigente) else "S/C",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = when {
                                califVigente == null -> MaterialTheme.colorScheme.onSurfaceVariant
                                materia.estaAprobada -> GradePassing
                                else -> GradeFailing
                            }
                        )
                        Text(
                            text = materia.tipoAcreditacion,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                califVigente == null -> MaterialTheme.colorScheme.onSurfaceVariant
                                materia.estaAprobada -> GradePassing
                                else -> GradeFailing
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Desglose de evaluaciones: Normal, Repetición, Especial
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NotaColumna(
                        titulo = "Normal",
                        valor = materia.calificacionNormal,
                        esVigente = materia.calificacionEspecial == null && materia.calificacionRepeticion == null && materia.calificacionNormal != null
                    )
                    Text("•", color = MaterialTheme.colorScheme.outlineVariant)
                    NotaColumna(
                        titulo = "Repetición",
                        valor = materia.calificacionRepeticion,
                        esVigente = materia.calificacionEspecial == null && materia.calificacionRepeticion != null
                    )
                    Text("•", color = MaterialTheme.colorScheme.outlineVariant)
                    NotaColumna(
                        titulo = "Especial",
                        valor = materia.calificacionEspecial,
                        esVigente = materia.calificacionEspecial != null
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fila inferior: Prevalencia y botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SuggestionChip(
                    onClick = { },
                    label = {
                        Text(
                            text = if (califVigente != null) "Vía: ${materia.tipoAcreditacion}" else "Sin calificar",
                            fontSize = 11.sp
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.height(28.dp)
                )

                Row {
                    IconButton(
                        onClick = onEditar,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_editar_materia_${materia.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Materia",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onEliminar,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_eliminar_materia_${materia.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Materia",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotaColumna(
    titulo: String,
    valor: Double?,
    esVigente: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = if (esVigente) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (esVigente) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = if (valor != null) String.format(Locale.US, "%.1f", valor) else "--",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (esVigente) FontWeight.ExtraBold else FontWeight.Medium,
            color = when {
                valor == null -> MaterialTheme.colorScheme.outline
                valor >= 70.0 -> GradePassing
                else -> GradeFailing
            }
        )
    }
}

@Composable
private fun EstadoVacioMaterias(
    semestreFiltro: Int,
    onNuevaMateria: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (semestreFiltro == 0) "No hay materias registradas" else "No hay materias en el Semestre $semestreFiltro",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Registra tus materias y calificaciones para calcular automáticamente tus promedios por semestre y general en Room.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNuevaMateria,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_vacio_registrar_materia")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar Materia")
            }
        }
    }
}
