package comcl.duoc.appbogados.model

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_bogados_sesion", Context.MODE_PRIVATE)

    private fun claveFotoPorRut(rut: String): String {
        return "foto_perfil_${rut.trim().uppercase()}"
    }

    fun guardarSesion(isLoggedIn: Boolean, nombre_usuario: String, rut: String) {
        prefs.edit().apply {
            putBoolean("is_logged_in", isLoggedIn)
            putString("nombre_usuario", nombre_usuario)
            putString("rut", rut.trim())
            apply()
        }
    }

    fun obtenerIsLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    fun obtenerNombreUsuario(): String? = prefs.getString("nombre_usuario", "")
    fun obtenerRut(): String? = prefs.getString("rut", "")

    fun actualizarFotoPerfil(rut: String, fotoBase64: String?) {
        if (rut.isBlank()) return
        prefs.edit().putString(claveFotoPorRut(rut), fotoBase64).apply()
    }

    fun obtenerFotoPerfil(rut: String?): String? {
        if (rut.isNullOrBlank()) return null
        return prefs.getString(claveFotoPorRut(rut), null)
    }

    fun cerrarSesion() {
        // Solo borra la sesión activa; conserva las fotos guardadas por RUT
        prefs.edit().apply {
            remove("is_logged_in")
            remove("nombre_usuario")
            remove("rut")
            apply()
        }
    }
}