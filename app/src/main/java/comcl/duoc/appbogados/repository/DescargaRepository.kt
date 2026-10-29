package comcl.duoc.appbogados.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import comcl.duoc.appbogados.ui.Documento
import java.io.File

/**
 * Copia los PDFs guardados en res/raw a la carpeta Descargas del teléfono.
 */
class DescargaRepository(private val context: Context) {

    /** Descarga un solo documento. Devuelve true si salió bien. */
    fun descargar(documento: Documento): Boolean {
        return try {
            context.resources.openRawResource(documento.recurso).use { entrada ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+: se guarda en Descargas usando MediaStore (sin permisos)
                    val valores = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, documento.archivo)
                        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = context.contentResolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, valores
                    ) ?: return false
                    context.contentResolver.openOutputStream(uri)?.use { salida ->
                        entrada.copyTo(salida)
                    } ?: return false
                } else {
                    // Android 9 o menor: carpeta de descargas propia de la app (sin permisos)
                    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        ?: return false
                    dir.mkdirs()
                    File(dir, documento.archivo).outputStream().use { salida ->
                        entrada.copyTo(salida)
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Descarga varios documentos de una vez. Devuelve cuántos se descargaron bien. */
    fun descargarTodos(documentos: List<Documento>): Int {
        return documentos.count { descargar(it) }
    }
}
