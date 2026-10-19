package comcl.duoc.appbogados.repository

import comcl.duoc.appbogados.model.FormularioModel
import comcl.duoc.appbogados.model.MensajesError

class  FormularioRepository {

    private var formulario = FormularioModel()
    private var errores = MensajesError()

    fun getFormulario():  FormularioModel = formulario
    fun getMensajesError():  MensajesError = errores

    fun validacionNombre(): Boolean {
        if(formulario.nombre=="")
            return false
        else
            return true
    }

    fun validacionCorreo(): Boolean {
        if (!formulario.correo.matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$")))
            return false
        else
            return true
    }

}