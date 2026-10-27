package comcl.duoc.appbogados.repository

import comcl.duoc.appbogados.model.FormularioModel
import comcl.duoc.appbogados.model.MensajesError

class FormularioRepository {

    private var formulario = FormularioModel()
    private var errores = MensajesError()

    fun getFormulario(): FormularioModel = formulario
    fun getMensajesError(): MensajesError = errores

    fun validacionNombre(): Boolean {
        if(formulario.nombre == "")
            return false
        else
            return true
    }

    // 9 caracteres: 8 números y el último número o K
    fun validacionRut(): Boolean {
        val rut = formulario.rut.uppercase()
        return rut.length == 9 &&
                rut.take(8).all { it.isDigit() } &&
                (rut.last().isDigit() || rut.last() == 'K')
    }
}