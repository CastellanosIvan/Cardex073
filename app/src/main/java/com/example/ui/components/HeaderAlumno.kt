package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.DatosAlumno
import com.example.ui.theme.CardexBluePrimary
import com.example.ui.theme.CardexBlueVariant
import com.example.ui.theme.GradeFailing
import com.example.ui.theme.GradePassing
import com.example.ui.theme.GradeWarning
import java.util.Locale

/**
 * Encabezado principal que muestra los datos del alumno (almacenados en SharedPreferences)
 * y el resumen métrico global (calculado por las consultas SQL @Query de Room).
 */
@Composable
fun HeaderAlumno(
    datosAlumno: DatosAlumno,
    promedioGeneral: Double?,
    totalMaterias: Int,
    totalAprobadas: Int,
    onEditarPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tarjeta principal del Alumno con degradado institucional
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(CardexBluePrimary, CardexBlueVariant)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar Alumno",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (datosAlumno.nombre.isNotBlank()) datosAlumno.nombre else "Estudiante",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "No. Control: ${if (datosAlumno.numeroControl.isNotBlank()) datosAlumno.numeroControl else "---"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = datosAlumno.carrera,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.75f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        IconButton(
                            onClick = onEditarPerfil,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .testTag("btn_editar_perfil_header")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Perfil",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tarjetas de Métricas Académicas (Room @Query)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tarjeta 1: Promedio General
                val colorPromedio = when {
                    promedioGeneral == null -> MaterialTheme.colorScheme.onSurfaceVariant
                    promedioGeneral >= 85.0 -> GradePassing
                    promedioGeneral >= 70.0 -> GradeWarning
                    else -> GradeFailing
                }

                MetricaCard(
                    modifier = Modifier.weight(1.3f),
                    titulo = "Promedio General",
                    valor = if (promedioGeneral != null) String.format(Locale.US, "%.1f", promedioGeneral) else "--",
                    subtitulo = if (promedioGeneral != null) (if (promedioGeneral >= 70.0) "Aprobatorio" else "Requiere atención") else "Sin calif.",
                    icono = Icons.Default.Star,
                    colorAcento = colorPromedio,
                    testTag = "card_promedio_general"
                )

                // Tarjeta 2: Total de Materias
                MetricaCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Materias",
                    valor = "$totalMaterias",
                    subtitulo = "Registradas",
                    icono = Icons.Default.MenuBook,
                    colorAcento = MaterialTheme.colorScheme.primary,
                    testTag = "card_total_materias"
                )

                // Tarjeta 3: Materias Aprobadas
                val porcentaje = if (totalMaterias > 0) (totalAprobadas * 100) / totalMaterias else 0
                MetricaCard(
                    modifier = Modifier.weight(1.1f),
                    titulo = "Aprobadas",
                    valor = "$totalAprobadas",
                    subtitulo = "$porcentaje% éxito",
                    icono = Icons.Default.CheckCircle,
                    colorAcento = GradePassing,
                    testTag = "card_materias_aprobadas"
                )
            }
        }
    }
}

@Composable
private fun MetricaCard(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    subtitulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorAcento: Color,
    testTag: String
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colorAcento
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colorAcento
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
