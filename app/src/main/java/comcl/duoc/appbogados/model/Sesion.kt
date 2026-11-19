package comcl.duoc.appbogados.model

data class Usuario(
    val id: Int? = null,
    val nombre_usuario: String,
    val rut: String,
    val contrasena: String? = null
)

data class LoginRequest(
    val rut: String,
    val contrasena: String
)

data class XanoErrorResponse(
    val message: String? = null
)

data class Documento(
    val id: Int? = null,
    val titulo: String,
    val contenido: String,
    val categoria: String? = "General",
    val pdf_base64: String? = null
)