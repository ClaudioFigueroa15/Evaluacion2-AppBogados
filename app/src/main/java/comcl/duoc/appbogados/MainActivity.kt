package comcl.duoc.appbogados

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import comcl.duoc.appbogados.ui.CarpetasScreen
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

                    // false = pantalla de carpetas (página principal), true = tu HomeScreen
                    var verPerfil by rememberSaveable { mutableStateOf(false) }

                    // Cierra la sesión y avisa con un mensaje
                    val cerrarSesion = {
                        verPerfil = false
                        sesionViewModel.cerrarSesion()
                        Toast.makeText(
                            this@MainActivity,
                            "Se cerró la sesión",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // Botón atrás del teléfono: de HomeScreen vuelve a las carpetas
                    BackHandler(enabled = sesion != null && verPerfil) {
                        verPerfil = false
                    }

                    if (sesion != null) {
                        if (verPerfil) {
                            HomeScreen(
                                sesion = sesion,
                                onCerrarSesion = cerrarSesion
                            )
                        } else {
                            CarpetasScreen(
                                sesion = sesion,
                                onVerPerfil = { verPerfil = true },
                                onCerrarSesion = cerrarSesion
                            )
                        }
                    } else {
                        Formulario(
                            viewModel = formularioViewModel,
                            onIngresoExitoso = { nombre, rut ->
                                sesionViewModel.iniciarSesion(nombre, rut)
                            }
                        )
                    }
                }
            }
        }
    }
}
