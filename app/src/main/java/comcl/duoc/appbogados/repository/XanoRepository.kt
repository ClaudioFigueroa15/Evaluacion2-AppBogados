package comcl.duoc.appbogados.repository

import comcl.duoc.appbogados.model.Documento
import comcl.duoc.appbogados.model.LoginRequest
import comcl.duoc.appbogados.model.Usuario
import comcl.duoc.appbogados.remote.RetrofitInstance
import retrofit2.Response

class XanoRepository {

    suspend fun registrarUsuario(usuario: Usuario): Response<Usuario> {
        return RetrofitInstance.api.registrarUsuario(usuario)
    }

    suspend fun iniciarSesion(rut: String, contrasena: String): Response<Usuario> {
        val request = LoginRequest(rut = rut.trim(), contrasena = contrasena)
        return RetrofitInstance.api.iniciarSesion(request)
    }

    suspend fun obtenerDocumentos(): List<Documento> {
        return RetrofitInstance.api.obtenerDocumentos()
    }
}