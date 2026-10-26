package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.preferences.PreferenciasAlumno
import com.example.data.repository.MateriaRepository
import com.example.ui.MateriaViewModel
import com.example.ui.MateriaViewModelFactory
import com.example.ui.components.PantallaBienvenida
import com.example.ui.screens.PantallaPrincipal
import com.example.ui.theme.CardexTheme

/**
 * Actividad Principal de "Mi Cardex073".
 * Configura la inyección de dependencias mediante factory para Room y SharedPreferences,
 * y enruta entre la Pantalla de Bienvenida (primer inicio) y la Pantalla Principal.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inicialización de la base de datos Room (Singleton)
        val database = AppDatabase.getDatabase(applicationContext)

        // 2. Creación del Repositorio con el DAO de Room
        val repository = MateriaRepository(database.materiaDao())

        // 3. Inicialización de SharedPreferences para los datos permanentes del alumno
        val preferenciasAlumno = PreferenciasAlumno(applicationContext)

        // 4. Instancia de la factoría del ViewModel
        val factory = MateriaViewModelFactory(repository, preferenciasAlumno)

        setContent {
            CardexTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MiCardexApp(factory = factory)
                }
            }
        }
    }
}

@Composable
fun MiCardexApp(factory: MateriaViewModelFactory) {
    val viewModel: MateriaViewModel = viewModel(factory = factory)
    val datosAlumno by viewModel.datosAlumno.collectAsStateWithLifecycle()

    // Control de primer inicio: si no está registrado, se solicita nombre y no. de control
    if (!datosAlumno.estaRegistrado) {
        PantallaBienvenida(
            onGuardar = { nombre, numControl, carrera ->
                viewModel.guardarPerfilAlumno(nombre, numControl, carrera)
            }
        )
    } else {
        PantallaPrincipal(viewModel = viewModel)
    }
}
