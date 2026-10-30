package comcl.duoc.appbogados.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import comcl.duoc.appbogados.ui.Documento
import java.io.File

//Repositorio encargado de procesar PDF y tranformarlos en Bitmap utilizando la API nativa de PdfRenderer
//de Android para la visualización dentro de la app
class PdfRepository(private val context: Context) {

    //Extrae un PDF hacia el caché temporal y renderiza cada una de las páginas como un objeto Bitmap
    fun renderizar(documento: Documento, anchoPx: Int = 1080): List<Bitmap> {
        // PdfRenderer necesita un archivo real, así que copiamos el PDF a la caché
        val archivo = File(context.cacheDir, "lector_${documento.archivo}")
        context.resources.openRawResource(documento.recurso).use { entrada ->
            archivo.outputStream().use { salida -> entrada.copyTo(salida) }
        }

        val paginas = mutableListOf<Bitmap>()
        // Abre el archivo temporal en modo solo lectura mediante un ParcelFileDescriptor
        ParcelFileDescriptor.open(archivo, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
            val renderer = PdfRenderer(fd)
            try {
                for (i in 0 until renderer.pageCount) {
                    val pagina = renderer.openPage(i)
                    try {
                        val alto = (anchoPx.toFloat() * pagina.height / pagina.width).toInt()
                        val bitmap = Bitmap.createBitmap(anchoPx, alto, Bitmap.Config.ARGB_8888)
                        bitmap.eraseColor(Color.WHITE) // fondo blanco (el PDF viene transparente)
                        pagina.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        paginas.add(bitmap)
                    } finally {
                        // Cierra la página actual para liberar recursos nativos antes de abrir la siguiente
                        pagina.close()
                    }
                }
            } finally {
                // Cierra la instancia de PdfRenderer al finalizar la lectura de todas las páginas
                renderer.close()
            }
        }
        return paginas
    }
}
