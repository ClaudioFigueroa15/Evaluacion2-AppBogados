package comcl.duoc.appbogados.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import comcl.duoc.appbogados.repository.FormularioRepository
import comcl.duoc.appbogados.model.FormularioModel
import comcl.duoc.appbogados.model.MensajesError

class FormularioViewModel : ViewModel() {
    private val repository = FormularioRepository()

    var formulario: FormularioModel by mutableStateOf( repository.getFormulario() )
    var mensajesError: MensajesError by mutableStateOf( repository.getMensajesError() )

    fun verificarFormulario(): Boolean {
        return verificarNombre() && verificarRut()
    }

    fun verificarNombre(): Boolean {
        if (!repository.validacionNombre()) {
            mensajesError.nombre = "El nombre no puede estar vacío"
            return false
        } else {
            mensajesError.nombre = ""
            return true
        }
    }

    fun verificarRut(): Boolean {
        val rut = formulario.rut.uppercase()
        // 9 caracteres: los primeros 8 son números y el último es número o K
        val valido = rut.length == 9 &&
                rut.take(8).all { it.isDigit() } &&
                (rut.last().isDigit() || rut.last() == 'K')

        if (!valido) {
            mensajesError.rut = "El RUT debe tener 9 caracteres: números, y el último puede ser K"
            return false
        } else {
            mensajesError.rut = ""
            return true
        }
    }
}
