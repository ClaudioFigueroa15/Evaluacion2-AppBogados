package comcl.duoc.appbogados

import android.app.Application
import comcl.duoc.appbogados.model.Usuario
import comcl.duoc.appbogados.repository.XanoRepository
import comcl.duoc.appbogados.model.SessionManager
import comcl.duoc.appbogados.viewmodel.SesionViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockApp: Application
    private lateinit var mockRepository: XanoRepository
    private lateinit var mockSessionManager: SessionManager
    private lateinit var viewModel: SesionViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockApp = mockk(relaxed = true)
        mockRepository = mockk()
        mockSessionManager = mockk(relaxed = true)

        every { mockSessionManager.obtenerIsLoggedIn() } returns false
        every { mockSessionManager.obtenerFotoPerfil(any()) } returns null

        viewModel = SesionViewModel(
            application = mockApp,
            repository = mockRepository,
            sessionManager = mockSessionManager
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mockErrorBody(): ResponseBody = mockk(relaxed = true) {
        every { contentType() } returns null
        every { contentLength() } returns 0L
    }

    @Test
    @DisplayName("Login exitoso con credenciales válidas guarda sesión y carga documentos")
    fun loginExitosoGuardaSesion() = runTest(testDispatcher) {
        val usuarioEsperado = Usuario(
            id = 1,
            nombre_usuario = "Claudio",
            rut = "12345678-9",
            contrasena = null
        )
        coEvery {
            mockRepository.iniciarSesion("12345678-9", "clave123")
        } returns Response.success(usuarioEsperado)
        coEvery { mockRepository.obtenerDocumentos() } returns emptyList()

        var callbackEjecutado = false
        viewModel.iniciarSesion("12345678-9", "clave123") {
            callbackEjecutado = true
        }
        advanceUntilIdle()

        assertTrue(callbackEjecutado)
        assertTrue(viewModel.sesionIniciada.value)
        assertEquals("Claudio", viewModel.usuarioActual.value?.nombre_usuario)
        assertNull(viewModel.mensajeError.value)
        verify { mockSessionManager.guardarSesion(true, "Claudio", "12345678-9") }
        coVerify { mockRepository.obtenerDocumentos() }
    }

    @Test
    @DisplayName("Login con RUT no registrado (HTTP 404) muestra 'Usuario no registrado'")
    fun loginConRutNoRegistradoMuestraError() = runTest(testDispatcher) {
        coEvery {
            mockRepository.iniciarSesion("99999999-9", "clave123")
        } returns Response.error(404, mockErrorBody())

        var callbackEjecutado = false
        viewModel.iniciarSesion("99999999-9", "clave123") {
            callbackEjecutado = true
        }
        advanceUntilIdle()

        assertFalse(callbackEjecutado)
        assertFalse(viewModel.sesionIniciada.value)
        assertEquals("Usuario no registrado", viewModel.mensajeError.value)
    }

    @Test
    @DisplayName("Login con contraseña incorrecta (HTTP 401) muestra 'Contraseña incorrecta'")
    fun loginConContrasenaIncorrectaMuestraError() = runTest(testDispatcher) {
        coEvery {
            mockRepository.iniciarSesion("12345678-9", "erronea123")
        } returns Response.error(401, mockErrorBody())

        viewModel.iniciarSesion("12345678-9", "erronea123") {}
        advanceUntilIdle()

        assertFalse(viewModel.sesionIniciada.value)
        assertEquals("Contraseña incorrecta", viewModel.mensajeError.value)
    }
}