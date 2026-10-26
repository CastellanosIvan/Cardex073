package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromedioSemestre
import com.example.ui.theme.GradeFailing
import com.example.ui.theme.GradeFailingContainer
import com.example.ui.theme.GradePassing
import com.example.ui.theme.GradePassingContainer
import com.example.ui.theme.GradeWarning
import com.example.ui.theme.GradeWarningContainer
import java.util.Locale

/**
 * Pantalla dedicada para la visualización de los promedios calculados por Room
 * mediante sentencias SQL @Query con GROUP BY semestre y promedio general acumulado.
 */
@Composable
fun PantallaPromedios(
    promedioGeneral: Double?,
    promediosPorSemestre: List<PromedioSemestre>,
    totalMaterias: Int,
    totalAprobadas: Int,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("pantalla_promedios_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tarjeta de Resumen Global de Carrera
        item {
            TarjetaResumenAcumulado(
                promedioGeneral = promedioGeneral,
                semestresCursados = promediosPorSemestre.size,
                totalMaterias = totalMaterias,
                totalAprobadas = totalAprobadas
            )
        }

        // Explicación didáctica de la consulta Room SQL GROUP BY
        item {
            TarjetaExplicacionSql()
        }

        // Título de la sección de semestres
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Promedio por Semestre (GROUP BY)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${promediosPorSemestre.size} periodos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (promediosPorSemestre.isEmpty()) {
            item {
                EstadoVacioPromedios()
            }
        } else {
            items(promediosPorSemestre, key = { it.semestre }) { itemSemestre ->
                TarjetaSemestrePromedio(itemSemestre = itemSemestre)
            }
        }
    }
}

@Composable
private fun TarjetaResumenAcumulado(
    promedioGeneral: Double?,
    semestresCursados: Int,
    totalMaterias: Int,
    totalAprobadas: Int
) {
    val colorPromedio = when {
        promedioGeneral == null -> MaterialTheme.colorScheme.onSurfaceVariant
        promedioGeneral >= 85.0 -> GradePassing
        promedioGeneral >= 70.0 -> GradeWarning
        else -> GradeFailing
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_resumen_acumulado"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cardex Académico",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Promedio General Acumulado",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (promedioGeneral != null) String.format(Locale.US, "%.2f", promedioGeneral) else "--",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = colorPromedio,
                        lineHeight = 44.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "/ 100",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Estatus
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                promedioGeneral == null -> MaterialTheme.colorScheme.surfaceVariant
                                promedioGeneral >= 70.0 -> GradePassingContainer
                                else -> GradeFailingContainer
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when {
                            promedioGeneral == null -> "Sin notas"
                            promedioGeneral >= 90.0 -> "Excelente"
                            promedioGeneral >= 80.0 -> "Muy Bueno"
                            promedioGeneral >= 70.0 -> "Aprobatorio"
                            else -> "No Aprobatorio"
                        },
                        fontWeight = FontWeight.Bold,
                        color = when {
                            promedioGeneral == null -> MaterialTheme.colorScheme.onSurfaceVariant
                            promedioGeneral >= 70.0 -> GradePassing
                            else -> GradeFailing
                        },
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Barra de avance
            val porcentajeAprobacion = if (totalMaterias > 0) (totalAprobadas.toFloat() / totalMaterias.toFloat()) else 0f
            LinearProgressIndicator(
                progress = { porcentajeAprobacion },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = GradePassing,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$semestresCursados semestres cursados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$totalAprobadas de $totalMaterias materias aprobadas (${(porcentajeAprobacion * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TarjetaExplicacionSql() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Consulta SQL en Room (MateriaDao)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = "SELECT semestre,\n" +
                            "  AVG(COALESCE(calificacionEspecial, calificacionRepeticion, calificacionNormal)) AS promedio\n" +
                            "FROM materias WHERE ... GROUP BY semestre ORDER BY semestre ASC",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun TarjetaSemestrePromedio(itemSemestre: PromedioSemestre) {
    val promedio = itemSemestre.promedio
    val esAprobatorio = promedio >= 70.0
    val tasaAprobacion = if (itemSemestre.totalMaterias > 0) {
        itemSemestre.aprobadas.toFloat() / itemSemestre.totalMaterias.toFloat()
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tarjeta_semestre_${itemSemestre.semestre}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${itemSemestre.semestre}°",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Semestre ${itemSemestre.semestre}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${itemSemestre.totalMaterias} ${if (itemSemestre.totalMaterias == 1) "materia calificada" else "materias calificadas"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Promedio del Semestre
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (esAprobatorio) GradePassingContainer else GradeFailingContainer)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f", promedio),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (esAprobatorio) GradePassing else GradeFailing
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso y materias aprobadas
            LinearProgressIndicator(
                progress = { tasaAprobacion },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (esAprobatorio) GradePassing else GradeWarning,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GradePassing,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${itemSemestre.aprobadas} de ${itemSemestre.totalMaterias} materias acreditadas",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (esAprobatorio) "Semestre Regular" else "Con Repeticiones",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (esAprobatorio) GradePassing else GradeWarning
                )
            }
        }
    }
}

@Composable
private fun EstadoVacioPromedios() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sin calificaciones suficientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ingresa calificaciones en tus materias (Normal, Repetición o Especial) para ver el desglose agrupado por semestre.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
