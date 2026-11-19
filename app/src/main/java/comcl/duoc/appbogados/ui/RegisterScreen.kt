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
fun RegisterScreen(
    formularioViewModel: FormularioViewModel,
    sesionViewModel: SesionViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val nombre by formularioViewModel.nombreUsuarioReg.collectAsState()
    val rut by formularioViewModel.rutReg.collectAsState()
    val contrasena by formularioViewModel.contrasenaReg.collectAsState()

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

            Text(
                text = "Registro - AppBogados",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { formularioViewModel.nombreUsuarioReg.value = it },
                label = { Text("Nombre de Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = rut,
                onValueChange = { formularioViewModel.rutReg.value = it },
                label = { Text("RUT (ej: 12345678-9)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { formularioViewModel.contrasenaReg.value = it },
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
                    if (formularioViewModel.validarRegistro()) {
                        sesionViewModel.registrarUsuario(
                            nombre_usuario = nombre,
                            rut = rut,
                            contrasena = contrasena,
                            onSuccess = onRegisterSuccess
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
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión aquí")
            }
        }
    }
}