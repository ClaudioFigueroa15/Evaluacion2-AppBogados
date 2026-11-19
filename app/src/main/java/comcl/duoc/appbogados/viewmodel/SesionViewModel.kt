package comcl.duoc.appbogados.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import comcl.duoc.appbogados.model.Documento
import comcl.duoc.appbogados.model.Usuario
import comcl.duoc.appbogados.model.XanoErrorResponse
import comcl.duoc.appbogados.repository.XanoRepository
import comcl.duoc.appbogados.model.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = XanoRepository()
    private val sessionManager = SessionManager(application)

    private val _usuarioActual = MutableStateFlow<Usuario?>(
        if (sessionManager.obtenerIsLoggedIn()) {
            Usuario(
                nombre_usuario = sessionManager.obtenerNombreUsuario() ?: "",
                rut = sessionManager.obtenerRut() ?: "",
                contrasena = null
            )
        } else null
    )
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _sesionIniciada = MutableStateFlow(sessionManager.obtenerIsLoggedIn())
    val sesionIniciada: StateFlow<Boolean> = _sesionIniciada

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _documentos = MutableStateFlow<List<Documento>>(emptyList())
    val documentos: StateFlow<List<Documento>> = _documentos

    fun registrarUsuario(
        nombre_usuario: String,
        rut: String,
        contrasena: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null
            try {
                val nuevoUsuario = Usuario(
                    nombre_usuario = nombre_usuario.trim(),
                    rut = rut.trim(),
                    contrasena = contrasena
                )
                val response = repository.registrarUsuario(nuevoUsuario)
                val userBody = response.body()

                if (response.isSuccessful && userBody != null && userBody.id != null) {
                    _usuarioActual.value = userBody
                    _sesionIniciada.value = true
                    sessionManager.guardarSesion(true, userBody.nombre_usuario, userBody.rut, null)
                    cargarDocumentos()
                    onSuccess()
                } else {
                    _mensajeError.value = extraerMensajeError(response.errorBody()?.string())
                        ?: "No se pudo registrar el usuario (Código: ${response.code()})"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de conexión con Xano: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun iniciarSesion(
        rut: String,
        contrasena: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null
            try {
                val response = repository.iniciarSesion(rut, contrasena)

                if (response.isSuccessful) {
                    val usuarioLogueado = response.body()

                    // Solo permite ingresar si Xano devolvió un registro real con id y rut válidos
                    if (usuarioLogueado != null && usuarioLogueado.id != null && usuarioLogueado.rut.isNotBlank()) {
                        _usuarioActual.value = usuarioLogueado
                        _sesionIniciada.value = true
                        sessionManager.guardarSesion(
                            isLoggedIn = true,
                            nombreUsuario = usuarioLogueado.nombre_usuario,
                            rut = usuarioLogueado.rut,
                            fotoBase64 = null
                        )
                        cargarDocumentos()
                        onSuccess()
                    } else {
                        _mensajeError.value = "Usuario no registrado"
                    }
                } else {
                    _mensajeError.value = when (response.code()) {
                        404 -> "Usuario no registrado"
                        401 -> "Contraseña incorrecta"
                        else -> extraerMensajeError(response.errorBody()?.string())
                            ?: "Error al iniciar sesión (Código: ${response.code()})"
                    }
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error al conectar con el servidor: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    private fun extraerMensajeError(errorJson: String?): String? {
        if (errorJson.isNullOrBlank()) return null
        return try {
            Gson().fromJson(errorJson, XanoErrorResponse::class.java)?.message
        } catch (e: Exception) {
            null
        }
    }

    fun cargarDocumentos() {
        viewModelScope.launch {
            try {
                _documentos.value = repository.obtenerDocumentos()
            } catch (e: Exception) {
                _mensajeError.value = "Error al obtener documentos: ${e.localizedMessage}"
            }
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }

    fun cerrarSesion() {
        sessionManager.cerrarSesion()
        _usuarioActual.value = null
        _sesionIniciada.value = false
        _mensajeError.value = null
    }

    fun actualizarFotoPersistente(fotoBase64: String?) {
        sessionManager.actualizarFotoPerfil(fotoBase64)
    }

    fun obtenerFotoPersistente(): String? = sessionManager.obtenerFotoPerfil()
}