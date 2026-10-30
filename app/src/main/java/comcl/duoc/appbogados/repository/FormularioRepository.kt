package comcl.duoc.appbogados.repository

import comcl.duoc.appbogados.model.FormularioModel
import comcl.duoc.appbogados.model.MensajesError

class FormularioRepository {

    private var formulario = FormularioModel()
    private var errores = MensajesError()

    // Retorna el modelo de datos actual del formulario
    fun getFormulario(): FormularioModel = formulario

    // Retorna el objeto que almacena los mensajes de error
    fun getMensajesError(): MensajesError = errores

    //Valida que el campo del nombre del formulario no esté vacío
    fun validacionNombre(): Boolean {
        if(formulario.nombre == "")
            return false
        else
            return true
    }
}