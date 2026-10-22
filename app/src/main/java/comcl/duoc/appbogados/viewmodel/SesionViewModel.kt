package comcl.duoc.appbogados.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import comcl.duoc.appbogados.model.AppDatabase
import comcl.duoc.appbogados.model.Sesion
import kotlinx.coroutines.launch

class SesionViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getInstance(app).sesionDao()

    // true mientras se lee Room al abrir la app (evita mostrar el formulario un instante)
    var cargando by mutableStateOf(true)
        private set

    // null = no hay sesión iniciada
    var sesionActual by mutableStateOf<Sesion?>(null)
        private set

    init {
        viewModelScope.launch {
            sesionActual = dao.obtener()
            cargando = false
        }
    }

    fun iniciarSesion(nombre: String, rut: String) { // Parámetro cambiado a rut
        viewModelScope.launch {
            val sesion = Sesion(nombre = nombre, rut = rut) // Cambiado a rut
            dao.guardar(sesion)
            sesionActual = sesion
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            dao.cerrar()
            sesionActual = null
        }
    }
}