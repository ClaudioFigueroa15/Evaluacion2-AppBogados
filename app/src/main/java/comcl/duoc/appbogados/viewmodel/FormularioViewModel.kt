package comcl.duoc.appbogados.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FormularioViewModel : ViewModel() {

    // Campos para Registro
    var nombreUsuarioReg = MutableStateFlow("")
    var rutReg = MutableStateFlow("")
    var contrasenaReg = MutableStateFlow("")

    // Campos para Login
    var rutLogin = MutableStateFlow("")
    var contrasenaLogin = MutableStateFlow("")

    private val _errorValidacion = MutableStateFlow<String?>(null)
    val errorValidacion: StateFlow<String?> = _errorValidacion

    private val rutRegex = Regex("^\\d{8}-[0-9kK]$")

    fun validarRegistro(): Boolean {
        val nombre = nombreUsuarioReg.value.trim()
        val rut = rutReg.value.trim()
        val pass = contrasenaReg.value

        if (nombre.isEmpty() && rut.isEmpty() && pass.isEmpty()) {
            _errorValidacion.value = "Por favor completa todos los campos de registro"
            return false
        }

        if (nombre.isEmpty() || rut.isEmpty() || pass.isEmpty()) {
            _errorValidacion.value = "Por favor completa todos los campos"
            return false
        }

        if (!rutRegex.matches(rut)) {
            _errorValidacion.value = "El RUT debe tener exactamente 8 dígitos, un guion y un dígito o K (Ej: 12345678-9)"
            return false
        }

        if (pass.length < 7) {
            _errorValidacion.value = "La contraseña debe ser de mínimo 7 caracteres"
            return false
        }
        if (pass.length > 30) {
            _errorValidacion.value = "La contraseña debe tener como máximo 30 caracteres"
            return false
        }

        _errorValidacion.value = null
        return true
    }

    fun validarLogin(): Boolean {
        val rut = rutLogin.value.trim()
        val pass = contrasenaLogin.value

        if (rut.isEmpty() && pass.isEmpty()) {
            _errorValidacion.value = "Por favor completa todos los campos de inicio de sesión"
            return false
        }

        if (rut.isEmpty() || pass.isEmpty()) {
            _errorValidacion.value = "Por favor completa todos los campos"
            return false
        }

        if (!rutRegex.matches(rut)) {
            _errorValidacion.value = "Formato de RUT inválido (Ej: 12345678-9)"
            return false
        }

        if (pass.length < 7) {
            _errorValidacion.value = "La contraseña debe ser de mínimo 7 caracteres"
            return false
        }
        if (pass.length > 30) {
            _errorValidacion.value = "La contraseña debe tener como máximo 30 caracteres"
            return false
        }

        _errorValidacion.value = null
        return true
    }

    // Limpia tanto los campos de Login como los de Registro y el error de validación
    fun limpiarCampos() {
        nombreUsuarioReg.value = ""
        rutReg.value = ""
        contrasenaReg.value = ""
        rutLogin.value = ""
        contrasenaLogin.value = ""
        _errorValidacion.value = null
    }
}