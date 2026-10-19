package comcl.duoc.appbogados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import comcl.duoc.appbogados.ui.Formulario
import comcl.duoc.appbogados.ui.HomeScreen
import comcl.duoc.appbogados.ui.theme.AppBogadosTheme
import comcl.duoc.appbogados.viewmodel.FormularioViewModel
import comcl.duoc.appbogados.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {

    private val sesionViewModel: SesionViewModel by viewModels()
    private val formularioViewModel: FormularioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBogadosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val sesion = sesionViewModel.sesionActual
                    if (sesion != null) {
                        HomeScreen(
                            sesion = sesion,
                            onCerrarSesion = { sesionViewModel.cerrarSesion() }
                        )
                    } else {
                        Formulario(
                            viewModel = formularioViewModel,
                            onIngresoExitoso = { nombre, correo ->
                                // Llama aquí a la función de tu SesionViewModel que crea/guarda la sesión
                                sesionViewModel.iniciarSesion(nombre, correo)
                            }
                        )
                    }
                }
            }
        }
    }
}