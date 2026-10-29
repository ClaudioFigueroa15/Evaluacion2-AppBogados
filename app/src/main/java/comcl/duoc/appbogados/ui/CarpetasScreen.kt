package comcl.duoc.appbogados.ui

import android.graphics.Bitmap
import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import comcl.duoc.appbogados.R
import comcl.duoc.appbogados.model.Sesion
import comcl.duoc.appbogados.repository.DescargaRepository
import kotlinx.coroutines.launch

// nombre = lo que ve el usuario, archivo = nombre con que se guarda al descargar,
// recurso = PDF dentro de res/raw
data class Documento(val nombre: String, val archivo: String, @RawRes val recurso: Int)

data class Carpeta(val id: Int, val nombre: String, val documentos: List<Documento> = emptyList())

@Composable
fun CarpetasScreen(
    sesion: Sesion,
    onVerPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    foto: Bitmap? = null,   // foto de perfil (si existe, reemplaza el ícono)
    onAbrirDocumento: (Documento) -> Unit = {}   // abre el PDF dentro de la app
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var busqueda by remember { mutableStateOf("") }
    var menuPerfilAbierto by remember { mutableStateOf(false) }

    // Datos de ejemplo: más adelante se pueden cargar desde Room o una API
    val carpetas = remember {
        listOf(
            Carpeta(
                1, "Carpeta 1",
                listOf(
                    Documento("Contrato de servicios", "contrato_servicios.pdf", R.raw.contrato_servicios),
                    Documento("Demanda civil", "demanda_civil.pdf", R.raw.demanda_civil)
                )
            ),
            Carpeta(
                2, "Carpeta 2",
                listOf(Documento("Poder notarial", "poder_notarial.pdf", R.raw.poder_notarial))
            ),
            Carpeta(3, "Carpeta 3") // sin documentos
        )
    }
    // El buscador encuentra por nombre de carpeta o por nombre de documento
    val carpetasFiltradas = carpetas.filter { carpeta ->
        carpeta.nombre.contains(busqueda, ignoreCase = true) ||
                carpeta.documentos.any { it.nombre.contains(busqueda, ignoreCase = true) }
    }

    val context = LocalContext.current
    val descargaRepository = remember { DescargaRepository(context) }

    fun mostrarMensaje(texto: String) {
        scope.launch { snackbarHostState.showSnackbar(texto) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "AppBogados",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Fila superior: buscador + perfil
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Buscar documento") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Box {
                        IconButton(onClick = { menuPerfilAbierto = true }) {
                            if (foto != null) {
                                Image(
                                    bitmap = foto.asImageBitmap(),
                                    contentDescription = "Perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Perfil",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = menuPerfilAbierto,
                            onDismissRequest = { menuPerfilAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(sesion.nombre) },
                                onClick = {}
                            )
                            DropdownMenuItem(
                                text = { Text("RUT: ${sesion.rut}") },
                                onClick = {}
                            )
                            DropdownMenuItem(
                                text = { Text("Ver perfil") },
                                onClick = {
                                    menuPerfilAbierto = false
                                    onVerPerfil()
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = {
                                    menuPerfilAbierto = false
                                    onCerrarSesion()
                                }
                            )
                        }
                    }
                }

                // Fila: menú + Notas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = { mostrarMensaje("Notas próximamente") }) {
                        Text("Notas")
                    }
                }

                // Lista de carpetas
                if (carpetasFiltradas.isEmpty()) {
                    Text(
                        text = "No se encontraron resultados",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(carpetasFiltradas, key = { it.id }) { carpeta ->
                            FilaCarpeta(
                                carpeta = carpeta,
                                onDescargarTodo = {
                                    if (carpeta.documentos.isEmpty()) {
                                        mostrarMensaje("${carpeta.nombre} no tiene documentos")
                                    } else {
                                        val ok = descargaRepository.descargarTodos(carpeta.documentos)
                                        mostrarMensaje(
                                            if (ok == carpeta.documentos.size)
                                                "Se descargaron $ok documentos de ${carpeta.nombre}"
                                            else
                                                "Error: solo se descargaron $ok de ${carpeta.documentos.size}"
                                        )
                                    }
                                },
                                onAbrirDocumento = onAbrirDocumento,
                                onDescargarDocumento = { doc ->
                                    val ok = descargaRepository.descargar(doc)
                                    mostrarMensaje(
                                        if (ok) "Descargado: ${doc.archivo}"
                                        else "No se pudo descargar ${doc.archivo}"
                                    )
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaCarpeta(
    carpeta: Carpeta,
    onDescargarTodo: () -> Unit,
    onAbrirDocumento: (Documento) -> Unit,
    onDescargarDocumento: (Documento) -> Unit
) {
    var expandida by rememberSaveable(carpeta.id) { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Fila de la carpeta: al tocarla se despliega / se cierra
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandida = !expandida }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_folder),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = carpeta.nombre,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = when (carpeta.documentos.size) {
                        0 -> "Sin documentos"
                        1 -> "1 documento"
                        else -> "${carpeta.documentos.size} documentos"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = if (expandida) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expandida) "Cerrar carpeta" else "Abrir carpeta"
            )
            // Descarga todos los documentos de la carpeta juntos
            IconButton(onClick = onDescargarTodo) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = "Descargar todo de ${carpeta.nombre}"
                )
            }
        }

        // Contenido desplegable: lista de documentos
        AnimatedVisibility(visible = expandida) {
            Column(modifier = Modifier.fillMaxWidth().padding(start = 64.dp)) {
                if (carpeta.documentos.isEmpty()) {
                    Text(
                        text = "Esta carpeta no tiene documentos",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    carpeta.documentos.forEach { doc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAbrirDocumento(doc) } // tocar = leer el PDF
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PDF",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color(0xFFC62828), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Text(
                                text = doc.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            )
                            // Descarga solo este documento
                            IconButton(onClick = { onDescargarDocumento(doc) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_download),
                                    contentDescription = "Descargar ${doc.nombre}",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
