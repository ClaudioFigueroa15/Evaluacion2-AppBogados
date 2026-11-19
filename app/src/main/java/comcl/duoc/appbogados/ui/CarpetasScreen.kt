package comcl.duoc.appbogados.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import comcl.duoc.appbogados.model.Documento
import comcl.duoc.appbogados.viewmodel.LectorPdfViewModel
import comcl.duoc.appbogados.viewmodel.SesionViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpetasScreen(
    sesionViewModel: SesionViewModel,
    lectorPdfViewModel: LectorPdfViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val documentos by sesionViewModel.documentos.collectAsState()
    val usuarioActual by sesionViewModel.usuarioActual.collectAsState()
    val docSeleccionado by lectorPdfViewModel.documentoSeleccionado.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }

    // Cargar foto persistente si existe
    var fotoPerfilBitmap by remember {
        mutableStateOf<Bitmap?>(
            sesionViewModel.obtenerFotoPersistente()?.let { base64Str ->
                try {
                    val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) {
                    null
                }
            }
        )
    }

    // Launcher para tomar la foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoPerfilBitmap = bitmap
            // Convertir Bitmap a Base64 para persistencia
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encodedBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
            sesionViewModel.actualizarFotoPersistente(encodedBase64)
        }
    }

    // Launcher nativo para solicitar permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    LaunchedEffect(Unit) {
        sesionViewModel.cargarDocumentos()
    }

    if (docSeleccionado != null) {
        LectorPdfScreen(
            documento = docSeleccionado,
            onBack = { lectorPdfViewModel.limpiarSeleccion() }
        )
        return
    }

    val categoriasDisponibles = listOf("Todas", "Civil", "Contratos", "Notarial")
    val documentosFiltrados = documentos.filter { doc ->
        val coincideBusqueda = doc.titulo.contains(searchQuery, ignoreCase = true) ||
                doc.contenido.contains(searchQuery, ignoreCase = true)
        val coincideCategoria = categoriaSeleccionada == "Todas" || doc.categoria.equals(categoriaSeleccionada, ignoreCase = true)
        coincideBusqueda && coincideCategoria
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    // Validar si ya tiene el permiso de cámara concedido
                                    when {
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED -> {
                                            cameraLauncher.launch(null)
                                        }
                                        else -> {
                                            // Lanzar diálogo nativo del sistema operativo
                                            permissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    }
                                }
                        ) {
                            if (fotoPerfilBitmap != null) {
                                Image(
                                    bitmap = fotoPerfilBitmap!!.asImageBitmap(),
                                    contentDescription = "Foto de Perfil",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = usuarioActual?.nombre_usuario?.take(1)?.uppercase() ?: "A",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = usuarioActual?.nombre_usuario ?: "Abogado", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Toca para usar cámara", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar documento legal...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoriasDisponibles) { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { categoriaSeleccionada = categoria },
                        label = { Text(categoria) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Carpetas de Documentos",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (documentos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (documentosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron documentos en esta categoría.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(documentosFiltrados) { doc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    lectorPdfViewModel.seleccionarDocumento(doc)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = doc.titulo,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = doc.contenido.take(90) + "...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}