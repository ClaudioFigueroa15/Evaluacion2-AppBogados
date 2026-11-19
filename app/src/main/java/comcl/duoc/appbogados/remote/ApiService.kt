package comcl.duoc.appbogados.remote

import comcl.duoc.appbogados.model.Documento
import comcl.duoc.appbogados.model.LoginRequest
import comcl.duoc.appbogados.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("usuarios")
    suspend fun registrarUsuario(
        @Body usuario: Usuario
    ): Response<Usuario>

    @POST("login")
    suspend fun iniciarSesion(
        @Body request: LoginRequest
    ): Response<Usuario>

    @GET("documentos")
    suspend fun obtenerDocumentos(): List<Documento>
}