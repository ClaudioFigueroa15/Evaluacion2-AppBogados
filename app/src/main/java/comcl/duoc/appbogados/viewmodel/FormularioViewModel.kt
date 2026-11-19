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

    // Expresión regular para RUT: 8 dígitos obligatorios, un guion, y un número o K (ej: 12345678-9 o 12345678-K)
    private val rutRegex = Regex("^\\d{8}-[0-9kK]$")

    fun validarRegistro(): Boolean {
        val nombre = nombreUsuarioReg.value.trim()
        val rut = rutReg.value.trim()
        val pass = contrasenaReg.value

        // 1. Si todo está vacío
        if (nombre.isEmpty() && rut.isEmpty() && pass.isEmpty()) {
            _errorValidacion.value = "Por favor completa todos los campos de registro"
            return false
        }

        // 2. Validar campos vacíos individuales
        if (nombre.isEmpty() || rut.isEmpty() || pass.isEmpty()) {
            _errorValidacion.value = "Por favor completa todos los campos"
            return false
        }

        // 3. Validación estricta del RUT
        if (!rutRegex.matches(rut)) {
            _errorValidacion.value = "El RUT debe tener exactamente 8 dígitos, un guion y un dígito o K (Ej: 12345678-9)"
            return false
        }

        // 4. Validación de longitud de contraseña
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
}