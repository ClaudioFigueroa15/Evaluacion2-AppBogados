package comcl.duoc.appbogados.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import comcl.duoc.appbogados.model.Documento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LectorPdfViewModel(application: Application) : AndroidViewModel(application) {

    private val _documentoSeleccionado = MutableStateFlow<Documento?>(null)
    val documentoSeleccionado: StateFlow<Documento?> = _documentoSeleccionado

    private val _paginasPdf = MutableStateFlow<List<Bitmap>>(emptyList())
    val paginasPdf: StateFlow<List<Bitmap>> = _paginasPdf

    private val _cargandoPdf = MutableStateFlow(false)
    val cargandoPdf: StateFlow<Boolean> = _cargandoPdf

    private val _errorPdf = MutableStateFlow<String?>(null)
    val errorPdf: StateFlow<String?> = _errorPdf

    private var trabajoCarga: Job? = null

    fun seleccionarDocumento(documento: Documento) {
        _documentoSeleccionado.value = documento
        _paginasPdf.value = emptyList()
        _errorPdf.value = null

        val rawBase64 = documento.pdf_base64
        if (rawBase64.isNullOrBlank()) {
            _errorPdf.value = "Este documento no tiene un archivo PDF guardado en Xano."
            return
        }

        trabajoCarga?.cancel()
        trabajoCarga = viewModelScope.launch {
            _cargandoPdf.value = true
            try {
                val bitmaps = withContext(Dispatchers.IO) {
                    val archivoLocal = guardarBase64EnCache(
                        rawBase64 = rawBase64,
                        nombreArchivo = "doc_${documento.id ?: documento.titulo.hashCode()}.pdf"
                    )
                    renderizarPaginasPdf(archivoLocal)
                }
                _paginasPdf.value = bitmaps
            } catch (e: Exception) {
                _errorPdf.value = "No se pudo abrir el PDF: ${e.localizedMessage}"
            } finally {
                _cargandoPdf.value = false
            }
        }
    }

    private fun guardarBase64EnCache(rawBase64: String, nombreArchivo: String): File {
        // Elimina prefijo opcional (data:application/pdf;base64,) y saltos de línea/espacios
        val base64Limpio = rawBase64
            .substringAfter("base64,", rawBase64)
            .replace("\\s".toRegex(), "")

        val pdfBytes = Base64.decode(base64Limpio, Base64.DEFAULT)

        val archivoDestino = File(getApplication<Application>().cacheDir, nombreArchivo)
        archivoDestino.writeBytes(pdfBytes)
        return archivoDestino
    }

    private fun renderizarPaginasPdf(archivoPdf: File): List<Bitmap> {
        val paginas = mutableListOf<Bitmap>()
        val descriptor = ParcelFileDescriptor.open(archivoPdf, ParcelFileDescriptor.MODE_READ_ONLY)

        descriptor.use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                for (i in 0 until renderer.pageCount) {
                    renderer.openPage(i).use { pagina ->
                        val anchoDestino = 1080
                        val altoDestino = (anchoDestino.toFloat() / pagina.width * pagina.height)
                            .toInt()
                            .coerceAtLeast(1)

                        val bitmap = Bitmap.createBitmap(
                            anchoDestino,
                            altoDestino,
                            Bitmap.Config.ARGB_8888
                        )

                        // Fondo blanco para asegurar legibilidad en tema oscuro o claro
                        val canvas = Canvas(bitmap)
                        canvas.drawColor(Color.WHITE)

                        pagina.render(
                            bitmap,
                            null,
                            null,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                        )
                        paginas.add(bitmap)
                    }
                }
            }
        }
        return paginas
    }

    fun limpiarSeleccion() {
        trabajoCarga?.cancel()
        _documentoSeleccionado.value = null
        _paginasPdf.value = emptyList()
        _errorPdf.value = null
        _cargandoPdf.value = false
    }
}