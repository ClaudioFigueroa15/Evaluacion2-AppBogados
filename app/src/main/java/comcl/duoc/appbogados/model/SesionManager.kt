package comcl.duoc.appbogados.model

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_bogados_sesion", Context.MODE_PRIVATE)

    fun guardarSesion(isLoggedIn: Boolean, nombreUsuario: String, rut: String, fotoBase64: String?) {
        prefs.edit().apply {
            putBoolean("is_logged_in", isLoggedIn)
            putString("nombre_usuario", nombreUsuario)
            putString("rut", rut)
            putString("foto_perfil", fotoBase64)
            apply()
        }
    }

    fun obtenerIsLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    fun obtenerNombreUsuario(): String? = prefs.getString("nombre_usuario", "Abogado")
    fun obtenerRut(): String? = prefs.getString("rut", "")
    fun obtenerFotoPerfil(): String? = prefs.getString("foto_perfil", null)

    fun actualizarFotoPerfil(fotoBase64: String?) {
        prefs.edit().putString("foto_perfil", fotoBase64).apply()
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}