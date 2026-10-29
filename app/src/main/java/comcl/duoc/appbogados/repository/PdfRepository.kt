package comcl.duoc.appbogados.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import comcl.duoc.appbogados.ui.Documento
import java.io.File

/**
 * Lee un PDF de res/raw y lo convierte en imágenes (una por página)
 * usando el PdfRenderer que ya trae Android.
 */
class PdfRepository(private val context: Context) {

    /** Debe llamarse fuera del hilo principal (es un trabajo pesado). */
    fun renderizar(documento: Documento, anchoPx: Int = 1080): List<Bitmap> {
        // PdfRenderer necesita un archivo real, así que copiamos el PDF a la caché
        val archivo = File(context.cacheDir, "lector_${documento.archivo}")
        context.resources.openRawResource(documento.recurso).use { entrada ->
            archivo.outputStream().use { salida -> entrada.copyTo(salida) }
        }

        val paginas = mutableListOf<Bitmap>()
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
                        pagina.close()
                    }
                }
            } finally {
                renderer.close()
            }
        }
        return paginas
    }
}
