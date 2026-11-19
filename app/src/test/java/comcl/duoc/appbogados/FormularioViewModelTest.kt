package comcl.duoc.appbogados

import comcl.duoc.appbogados.viewmodel.FormularioViewModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FormularioViewModelTest {

    private lateinit var viewModel: FormularioViewModel

    @BeforeEach
    fun setUp() {
        viewModel = FormularioViewModel()
    }

    @Test
    @DisplayName("Registro falla cuando todos los campos están vacíos")
    fun registroFallaConCamposVacios() {
        val esValido = viewModel.validarRegistro()

        assertFalse(esValido)
        assertEquals(
            "Por favor completa todos los campos de registro",
            viewModel.errorValidacion.value
        )
    }

    @Test
    @DisplayName("Registro falla cuando el formato del RUT es incorrecto")
    fun registroFallaConRutInvalido() {
        viewModel.nombreUsuarioReg.value = "Claudio"
        viewModel.rutReg.value = "1234567-9" // Solo 7 dígitos antes del guion
        viewModel.contrasenaReg.value = "segura123"

        val esValido = viewModel.validarRegistro()

        assertFalse(esValido)
        assertEquals(
            "El RUT debe tener exactamente 8 dígitos, un guion y un dígito o K (Ej: 12345678-9)",
            viewModel.errorValidacion.value
        )
    }

    @Test
    @DisplayName("Registro falla cuando la contraseña tiene menos de 7 caracteres")
    fun registroFallaConContrasenaCorta() {
        viewModel.nombreUsuarioReg.value = "Claudio"
        viewModel.rutReg.value = "12345678-K"
        viewModel.contrasenaReg.value = "123456"

        val esValido = viewModel.validarRegistro()

        assertFalse(esValido)
        assertEquals(
            "La contraseña debe ser de mínimo 7 caracteres",
            viewModel.errorValidacion.value
        )
    }

    @Test
    @DisplayName("Registro falla cuando la contraseña supera los 30 caracteres")
    fun registroFallaConContrasenaLarga() {
        viewModel.nombreUsuarioReg.value = "Claudio"
        viewModel.rutReg.value = "12345678-9"
        viewModel.contrasenaReg.value = "a".repeat(31)

        val esValido = viewModel.validarRegistro()

        assertFalse(esValido)
        assertEquals(
            "La contraseña debe tener como máximo 30 caracteres",
            viewModel.errorValidacion.value
        )
    }

    @Test
    @DisplayName("Registro válido pasa y limpiarCampos deja los campos en blanco")
    fun registroValidoYLimpiezaDeCampos() {
        viewModel.nombreUsuarioReg.value = "Claudio"
        viewModel.rutReg.value = "12345678-K"
        viewModel.contrasenaReg.value = "claveSegura123"

        assertTrue(viewModel.validarRegistro())
        assertNull(viewModel.errorValidacion.value)

        viewModel.limpiarCampos()

        assertEquals("", viewModel.nombreUsuarioReg.value)
        assertEquals("", viewModel.rutReg.value)
        assertEquals("", viewModel.contrasenaReg.value)
        assertEquals("", viewModel.rutLogin.value)
        assertEquals("", viewModel.contrasenaLogin.value)
    }
}