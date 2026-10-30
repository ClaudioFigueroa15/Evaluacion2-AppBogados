package comcl.duoc.appbogados.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import comcl.duoc.appbogados.model.Sesion

//Pantalla de perfil del usuario que muestra los datos de la sesión activa (nombre y RUT),
//su fotografía de perfil capturada con la cámara del dispositivo (si existe) y las opciones
//para capturar o actualizar la fotografía y cerrar la sesión actual.
@Composable
fun HomeScreen(
    sesion: Sesion,
    onCerrarSesion: () -> Unit,
    foto: Bitmap? = null,                 // foto de perfil tomada con la cámara (opcional)
    onTomarFoto: (() -> Unit)? = null     // abre la cámara (opcional)
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (foto != null) {
            Image(
                bitmap = foto.asImageBitmap(),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(16.dp))
        }
        Text("Bienvenido, ${sesion.nombre}")
        Text(sesion.rut) // Cambiado de correo a rut
        if (onTomarFoto != null) {
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onTomarFoto) {
                Text(if (foto == null) "Tomar foto" else "Tomar otra foto")
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onCerrarSesion) {
            Text("Cerrar sesión")
        }
    }
}
