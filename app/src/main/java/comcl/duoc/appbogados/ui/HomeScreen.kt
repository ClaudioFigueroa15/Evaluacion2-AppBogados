package comcl.duoc.appbogados.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import comcl.duoc.appbogados.model.Sesion

@Composable
fun HomeScreen(sesion: Sesion, onCerrarSesion: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido, ${sesion.nombre}")
        Text(sesion.correo)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onCerrarSesion) {
            Text("Cerrar sesión")
        }
    }
}