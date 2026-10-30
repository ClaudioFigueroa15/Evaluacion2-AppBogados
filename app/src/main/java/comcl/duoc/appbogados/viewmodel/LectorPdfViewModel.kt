package comcl.duoc.appbogados.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import comcl.duoc.appbogados.repository.PdfRepository
import comcl.duoc.appbogados.ui.Documento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Responsable de administrar el estado del lector de PDF
class LectorPdfViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = PdfRepository(app)
    private var trabajo: Job? = null

    // null = no hay ningún documento abierto
    var documentoActual by mutableStateOf<Documento?>(null)
        private set

    var paginas by mutableStateOf<List<Bitmap>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf(false)
        private set


    //Inicia la apertura y renderizado de un PDF seleccionado por el usuario
    fun abrir(documento: Documento) {
        trabajo?.cancel()
        documentoActual = documento
        paginas = emptyList()
        error = false
        cargando = true
        trabajo = viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                runCatching { repositorio.renderizar(documento) }
            }
            resultado
                .onSuccess { paginas = it }
                .onFailure { error = true }
            cargando = false
        }
    }

    //Cierra el PDF actual y cancela cualquier renderizado en curso
    fun cerrar() {
        trabajo?.cancel()
        documentoActual = null
        paginas = emptyList()
        cargando = false
        error = false
    }
}
