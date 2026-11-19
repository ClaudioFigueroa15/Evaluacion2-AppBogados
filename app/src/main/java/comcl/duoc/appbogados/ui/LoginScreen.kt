package comcl.duoc.appbogados.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import comcl.duoc.appbogados.R
import comcl.duoc.appbogados.viewmodel.FormularioViewModel
import comcl.duoc.appbogados.viewmodel.SesionViewModel

@Composable
fun LoginScreen(
    formularioViewModel: FormularioViewModel,
    sesionViewModel: SesionViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val rut by formularioViewModel.rutLogin.collectAsState()
    val contrasena by formularioViewModel.contrasenaLogin.collectAsState()

    val cargando by sesionViewModel.cargando.collectAsState()
    val errorServidor by sesionViewModel.mensajeError.collectAsState()
    val errorValidacion by formularioViewModel.errorValidacion.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_abogado),
                contentDescription = "Logo Abogado",
                modifier = Modifier
                    .size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Iniciar Sesión - AppBogados",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = rut,
                onValueChange = { formularioViewModel.rutLogin.value = it },
                label = { Text("RUT") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { formularioViewModel.contrasenaLogin.value = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (!errorValidacion.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorValidacion!!, color = MaterialTheme.colorScheme.error)
            }

            if (!errorServidor.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorServidor!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (formularioViewModel.validarLogin()) {
                        sesionViewModel.iniciarSesion(
                            rut = rut,
                            contrasena = contrasena,
                            onSuccess = onLoginSuccess
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Ingresar")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}