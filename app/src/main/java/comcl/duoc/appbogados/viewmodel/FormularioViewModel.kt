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
        val rut = formulario.rut // Usa el campo rut del modelo
        if (rut.isBlank() || !validarRutChileno(rut)) {
            mensajesError.rut = "El RUT ingresado no es válido" // Muestra error si falla el Módulo 11
            return false
        } else {
            mensajesError.rut = ""
            return true
        }
    }

    // Lógica del Módulo 11
    private fun validarRutChileno(rut: String): Boolean {
        if (rut.length < 2) return false
        val cleanRut = rut.uppercase()
        val rutBody = cleanRut.dropLast(1)
        val dv = cleanRut.takeLast(1)

        return try {
            var sum = 0
            var multiplier = 2
            for (i in rutBody.reversed()) {
                sum += i.toString().toInt() * multiplier
                multiplier = if (multiplier == 7) 2 else multiplier + 1
            }

            val expectedDv = 11 - (sum % 11)
            val calculatedDv = when (expectedDv) {
                11 -> "0"
                10 -> "K"
                else -> expectedDv.toString()
            }

            dv == calculatedDv
        } catch (e: NumberFormatException) {
            false
        }
    }
}