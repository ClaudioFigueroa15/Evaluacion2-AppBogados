package comcl.duoc.appbogados.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import comcl.duoc.appbogados.R
import comcl.duoc.appbogados.viewmodel.FormularioViewModel

@Composable
fun Formulario(
    viewModel: FormularioViewModel,
    onIngresoExitoso: (nombre: String, rut: String) -> Unit // Parámetro actualizado a rut
) {

    var abrirModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título central implementado
        Text(
            text = "AppBogados",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        // Imagen del logo (archivo: res/drawable/logo_abogado.png)
        Image(
            painter = painterResource(id = R.drawable.logo_abogado),
            contentDescription = "Logo AppBogados",
            modifier = Modifier.size(220.dp).padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = viewModel.formulario.nombre,
            onValueChange = { viewModel.formulario.nombre = it },
            label = { Text("Ingresa nombre") },
            isError = !viewModel.verificarNombre(),
            supportingText = { Text( viewModel.mensajesError.nombre, color = androidx.compose.ui.graphics.Color.Red) }
        )

        // Campo de RUT con filtro estricto
        OutlinedTextField(
            value = viewModel.formulario.rut,
            onValueChange = { newValue ->
                // Máximo 9 caracteres: solo números, y el último puede ser K
                val valido = newValue.length <= 9 && newValue.withIndex().all { (i, c) ->
                    c.isDigit() || (i == 8 && c.equals('k', ignoreCase = true))
                }
                if (valido) {
                    viewModel.formulario.rut = newValue.uppercase()
                }
            },
            label = { Text("Ingresa RUT (9 caracteres, el último puede ser K)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = !viewModel.verificarRut(),
            supportingText = { Text( viewModel.mensajesError.rut, color = androidx.compose.ui.graphics.Color.Red) }
        )

        Button(
            enabled = viewModel.verificarFormulario(),
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                if(viewModel.verificarFormulario()) {
                    abrirModal = true
                }
            }

        ) {
            Text("Enviar")
        }

        if (abrirModal) {
            AlertDialog(
                onDismissRequest = { /* ... */ },
                title = { Text("Ingreso exitoso") },
                text = { Text("Bienvenido...") },
                confirmButton = {
                    Button(
                        onClick = {
                            // Enviar datos al callback
                            onIngresoExitoso(
                                viewModel.formulario.nombre,
                                viewModel.formulario.rut
                            )
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}