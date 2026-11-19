package comcl.duoc.appbogados.viewmodel

import androidx.lifecycle.ViewModel
import comcl.duoc.appbogados.model.Documento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LectorPdfViewModel : ViewModel() {

    private val _documentoSeleccionado = MutableStateFlow<Documento?>(null)
    val documentoSeleccionado: StateFlow<Documento?> = _documentoSeleccionado

    fun seleccionarDocumento(documento: Documento) {
        _documentoSeleccionado.value = documento
    }

    fun limpiarSeleccion() {
        _documentoSeleccionado.value = null
    }
}