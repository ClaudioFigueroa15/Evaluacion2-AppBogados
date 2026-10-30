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

    // Estado observable que expone los campos actuales del formulario (nombre y RUT) a la vista
    var formulario: FormularioModel by mutableStateOf( repository.getFormulario() )

    // Estado observable que expone los mensajes de error vigentes para cada campo del formulario
    var mensajesError: MensajesError by mutableStateOf( repository.getMensajesError() )


    //Evalúa de forma conjunta si todos los campos del formulario son válidos
    fun verificarFormulario(): Boolean {
        return verificarNombre() && verificarRut()
    }

    //Verifica la validez del campo nombre consultando al repositorio y actualiza el mensaje de error
    fun verificarNombre(): Boolean {
        if (!repository.validacionNombre()) {
            mensajesError.nombre = "El nombre no puede estar vacío"
            return false
        } else {
            mensajesError.nombre = ""
            return true
        }
    }

    //Verifica que el RUT ingresado tenga exactamente 9 caracteres (8 dígitos iniciales y
    //un dígito o letra 'K' al final) y actualiza el mensaje de error del campo según el resultado
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
