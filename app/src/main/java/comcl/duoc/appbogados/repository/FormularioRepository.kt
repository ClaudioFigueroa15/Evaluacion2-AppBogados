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

    // Validación matemática Módulo 11 para RUT chileno
    fun validacionRut(): Boolean {
        val rut = formulario.rut

        // Retorna falso inmediatamente si está vacío o es muy corto
        if (rut.isBlank() || rut.length < 2) return false

        val cleanRut = rut.uppercase()
        val rutBody = cleanRut.dropLast(1)
        val dv = cleanRut.takeLast(1)

        return try {
            var sum = 0
            var multiplier = 2

            // Recorre el cuerpo del RUT de derecha a izquierda
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

            // Compara el dígito calculado con el ingresado
            dv == calculatedDv
        } catch (e: NumberFormatException) {
            false
        }
    }
}