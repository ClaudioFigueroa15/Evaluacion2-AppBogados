package comcl.duoc.appbogados.viewmodel

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
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

    private val _descargando = MutableStateFlow(false)
    val descargando: StateFlow<Boolean> = _descargando

    private val _mensajeDescarga = MutableStateFlow<String?>(null)
    val mensajeDescarga: StateFlow<String?> = _mensajeDescarga

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
                    val pdfBytes = decodificarBase64(rawBase64)
                    val archivoLocal = File(
                        getApplication<Application>().cacheDir,
                        "doc_${documento.id ?: documento.titulo.hashCode()}.pdf"
                    )
                    archivoLocal.writeBytes(pdfBytes)
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

    fun descargarDocumento(documento: Documento) {
        val rawBase64 = documento.pdf_base64
        if (rawBase64.isNullOrBlank()) {
            _mensajeDescarga.value = "Este documento no tiene un PDF disponible para descargar."
            return
        }

        viewModelScope.launch {
            _descargando.value = true
            try {
                val nombreArchivo = generarNombreArchivoSeguro(documento.titulo)
                withContext(Dispatchers.IO) {
                    val pdfBytes = decodificarBase64(rawBase64)
                    guardarPdfEnDescargas(pdfBytes, nombreArchivo)
                }
                _mensajeDescarga.value = "Guardado en Descargas: $nombreArchivo"
            } catch (e: Exception) {
                _mensajeDescarga.value = "Error al descargar el PDF: ${e.localizedMessage}"
            } finally {
                _descargando.value = false
            }
        }
    }

    private fun decodificarBase64(rawBase64: String): ByteArray {
        val base64Limpio = rawBase64
            .substringAfter("base64,", rawBase64)
            .replace("\\s".toRegex(), "")
        return Base64.decode(base64Limpio, Base64.DEFAULT)
    }

    private fun generarNombreArchivoSeguro(titulo: String): String {
        val limpio = titulo
            .trim()
            .replace(Regex("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ_-]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
            .ifBlank { "documento_appbogados" }

        return if (limpio.endsWith(".pdf", ignoreCase = true)) limpio else "$limpio.pdf"
    }

    private fun guardarPdfEnDescargas(pdfBytes: ByteArray, nombreArchivo: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, nombreArchivo)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val resolver = getApplication<Application>().contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("No se pudo crear el archivo en la carpeta Descargas")

            resolver.openOutputStream(uri)?.use { output ->
                output.write(pdfBytes)
            } ?: throw Exception("No se pudo escribir el contenido del PDF")

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        } else {
            @Suppress("DEPRECATION")
            val directorioDescargas =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!directorioDescargas.exists()) {
                directorioDescargas.mkdirs()
            }
            val archivoDestino = File(directorioDescargas, nombreArchivo)
            archivoDestino.writeBytes(pdfBytes)
        }
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

    fun limpiarMensajeDescarga() {
        _mensajeDescarga.value = null
    }

    fun limpiarSeleccion() {
        trabajoCarga?.cancel()
        _documentoSeleccionado.value = null
        _paginasPdf.value = emptyList()
        _errorPdf.value = null
        _cargandoPdf.value = false
    }
}