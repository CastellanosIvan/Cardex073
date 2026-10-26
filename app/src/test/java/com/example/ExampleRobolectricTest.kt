package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.dao.MateriaDao
import com.example.data.database.AppDatabase
import com.example.data.model.Materia
import com.example.data.preferences.PreferenciasAlumno
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: MateriaDao
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.materiaDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `verificar nombre de app correcto`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("Mi Cardex073", appName)
    }

    @Test
    fun `preferencias de alumno guardan y recuperan datos en SharedPreferences`() {
        val prefs = PreferenciasAlumno(context)
        prefs.guardarDatos("Iván Núñez", "21010073", "Ingeniería en Sistemas")

        assertTrue(prefs.estaRegistrado())
        assertEquals("Iván Núñez", prefs.obtenerNombre())
        assertEquals("21010073", prefs.obtenerNumeroControl())
    }

    @Test
    fun `room calcula promedio general considerando regla de prevalencia Especial sobre Repeticion y Normal`() = runBlocking {
        // Materia 1: aprobada en Normal (80.0)
        dao.insertar(Materia(nombre = "Programación", semestre = 1, calificacionNormal = 80.0))

        // Materia 2: reprobada en Normal (50.0), aprobada en Repetición (90.0) -> prevalece 90.0
        dao.insertar(Materia(nombre = "Cálculo", semestre = 1, calificacionNormal = 50.0, calificacionRepeticion = 90.0))

        // Materia 3: reprobada en Normal (40.0), reprobada en Rep (60.0), aprobada en Especial (100.0) -> prevalece 100.0
        dao.insertar(Materia(nombre = "Física", semestre = 2, calificacionNormal = 40.0, calificacionRepeticion = 60.0, calificacionEspecial = 100.0))

        val promedioGeneral = dao.obtenerPromedioGeneral().first()
        assertNotNull(promedioGeneral)
        // Promedio esperado: (80 + 90 + 100) / 3 = 270 / 3 = 90.0
        assertEquals(90.0, promedioGeneral!!, 0.01)

        val promediosSemestre = dao.obtenerPromediosPorSemestre().first()
        assertEquals(2, promediosSemestre.size)

        // Semestre 1: (80 + 90) / 2 = 85.0
        val sem1 = promediosSemestre.first { it.semestre == 1 }
        assertEquals(85.0, sem1.promedio, 0.01)
        assertEquals(2, sem1.totalMaterias)
        assertEquals(2, sem1.aprobadas)

        // Semestre 2: 100.0
        val sem2 = promediosSemestre.first { it.semestre == 2 }
        assertEquals(100.0, sem2.promedio, 0.01)
        assertEquals(1, sem2.totalMaterias)
        assertEquals(1, sem2.aprobadas)
    }
}
