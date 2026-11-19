package comcl.duoc.appbogados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import comcl.duoc.appbogados.ui.CarpetasScreen
import comcl.duoc.appbogados.ui.LoginScreen
import comcl.duoc.appbogados.ui.RegisterScreen
import comcl.duoc.appbogados.ui.theme.AppBogadosTheme
import comcl.duoc.appbogados.viewmodel.FormularioViewModel
import comcl.duoc.appbogados.viewmodel.LectorPdfViewModel
import comcl.duoc.appbogados.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {

    private val sesionViewModel: SesionViewModel by viewModels()
    private val formularioViewModel: FormularioViewModel by viewModels()
    private val lectorPdfViewModel: LectorPdfViewModel by viewModels() // <-- Agregado aquí

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBogadosTheme {
                var currentScreen by remember { mutableStateOf("login") }
                val sesionIniciada by sesionViewModel.sesionIniciada.collectAsState()

                // Si la sesión está activa, redirige a Carpetas / Home
                if (sesionIniciada) {
                    CarpetasScreen(
                        sesionViewModel = sesionViewModel,
                        lectorPdfViewModel = lectorPdfViewModel, // <-- Pasado como parámetro
                        onLogout = {
                            sesionViewModel.cerrarSesion()
                            currentScreen = "login"
                        }
                    )
                } else {
                    when (currentScreen) {
                        "login" -> LoginScreen(
                            formularioViewModel = formularioViewModel,
                            sesionViewModel = sesionViewModel,
                            onNavigateToRegister = { currentScreen = "register" },
                            onLoginSuccess = { /* El StateFlow sesionIniciada cambiará automáticamente a true */ }
                        )
                        "register" -> RegisterScreen(
                            formularioViewModel = formularioViewModel,
                            sesionViewModel = sesionViewModel,
                            onNavigateToLogin = { currentScreen = "login" },
                            onRegisterSuccess = { /* El StateFlow sesionIniciada cambiará automáticamente a true */ }
                        )
                    }
                }
            }
        }
    }
}