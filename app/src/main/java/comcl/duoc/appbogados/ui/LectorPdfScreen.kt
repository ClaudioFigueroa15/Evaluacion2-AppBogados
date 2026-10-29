package comcl.duoc.appbogados.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import comcl.duoc.appbogados.R

@Composable
fun LectorPdfScreen(
    documento: Documento,
    paginas: List<Bitmap>,
    cargando: Boolean,
    error: Boolean,
    onVolver: () -> Unit,
    onDescargar: () -> Unit
) {
    val estadoLista = rememberLazyListState()

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Barra superior: volver + nombre + página actual + descargar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = documento.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (paginas.isNotEmpty()) {
                        Text(
                            text = "Página ${estadoLista.firstVisibleItemIndex + 1} de ${paginas.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                IconButton(onClick = onDescargar) {
                    Icon(
                        painter = painterResource(R.drawable.ic_download),
                        contentDescription = "Descargar ${documento.nombre}"
                    )
                }
            }
            HorizontalDivider()

            // Contenido: spinner, error o las páginas del PDF
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    cargando -> CircularProgressIndicator()
                    error -> Text(
                        text = "No se pudo abrir el documento",
                        modifier = Modifier.padding(16.dp)
                    )
                    else -> LazyColumn(
                        state = estadoLista,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                    ) {
                        itemsIndexed(paginas) { indice, pagina ->
                            Image(
                                bitmap = pagina.asImageBitmap(),
                                contentDescription = "Página ${indice + 1}",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
