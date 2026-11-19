package comcl.duoc.appbogados.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import comcl.duoc.appbogados.model.Documento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectorPdfScreen(
    documento: Documento?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(documento?.titulo ?: "Detalle del Documento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
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
            if (documento == null) {
                Text(
                    text = "No se ha seleccionado ningún documento.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = documento.titulo,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = documento.contenido,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}