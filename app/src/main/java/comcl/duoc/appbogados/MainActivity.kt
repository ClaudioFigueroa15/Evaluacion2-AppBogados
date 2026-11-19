package comcl.duoc.appbogados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import comcl.duoc.appbogados.ui.CarpetasScreen
import comcl.duoc.appbogados.ui.LoginScreen
import comcl.duoc.appbogados.ui.PerfilScreen
import comcl.duoc.appbogados.ui.RegisterScreen
import comcl.duoc.appbogados.ui.theme.AppBogadosTheme
import comcl.duoc.appbogados.viewmodel.FormularioViewModel
import comcl.duoc.appbogados.viewmodel.LectorPdfViewModel
import comcl.duoc.appbogados.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {

    private val sesionViewModel: SesionViewModel by viewModels()
    private val formularioViewModel: FormularioViewModel by viewModels()
    private val lectorPdfViewModel: LectorPdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBogadosTheme {
                var currentScreen by remember { mutableStateOf("login") }
                var mostrandoPerfil by remember { mutableStateOf(false) }
                val sesionIniciada by sesionViewModel.sesionIniciada.collectAsState()

                if (sesionIniciada) {
                    if (mostrandoPerfil) {
                        PerfilScreen(
                            sesionViewModel = sesionViewModel,
                            onBack = { mostrandoPerfil = false }
                        )
                    } else {
                        CarpetasScreen(
                            sesionViewModel = sesionViewModel,
                            lectorPdfViewModel = lectorPdfViewModel,
                            onProfileClick = { mostrandoPerfil = true },
                            onLogout = {
                                mostrandoPerfil = false
                                lectorPdfViewModel.limpiarSeleccion()
                                formularioViewModel.limpiarCampos() // Limpia al cerrar sesión
                                sesionViewModel.cerrarSesion()
                                currentScreen = "login"
                            }
                        )
                    }
                } else {
                    when (currentScreen) {
                        "login" -> LoginScreen(
                            formularioViewModel = formularioViewModel,
                            sesionViewModel = sesionViewModel,
                            onNavigateToRegister = {
                                formularioViewModel.limpiarCampos() // Limpia al ir a registro
                                currentScreen = "register"
                            },
                            onLoginSuccess = {
                                formularioViewModel.limpiarCampos() // Limpia tras iniciar sesión
                                mostrandoPerfil = false
                            }
                        )
                        "register" -> RegisterScreen(
                            formularioViewModel = formularioViewModel,
                            sesionViewModel = sesionViewModel,
                            onNavigateToLogin = {
                                formularioViewModel.limpiarCampos() // Limpia al volver a login
                                currentScreen = "login"
                            },
                            onRegisterSuccess = {
                                formularioViewModel.limpiarCampos() // Limpia tras registrarse
                                mostrandoPerfil = false
                            }
                        )
                    }
                }
            }
        }
    }
}