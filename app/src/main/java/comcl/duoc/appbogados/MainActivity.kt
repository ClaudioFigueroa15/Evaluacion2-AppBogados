package comcl.duoc.appbogados

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import comcl.duoc.appbogados.ui.CarpetasScreen
import comcl.duoc.appbogados.ui.Formulario
import comcl.duoc.appbogados.repository.DescargaRepository
import comcl.duoc.appbogados.ui.HomeScreen
import comcl.duoc.appbogados.ui.LectorPdfScreen
import comcl.duoc.appbogados.ui.theme.AppBogadosTheme
import comcl.duoc.appbogados.viewmodel.FormularioViewModel
import comcl.duoc.appbogados.viewmodel.LectorPdfViewModel
import comcl.duoc.appbogados.viewmodel.SesionViewModel
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private val sesionViewModel: SesionViewModel by viewModels()
    private val formularioViewModel: FormularioViewModel by viewModels()
    private val lectorPdfViewModel: LectorPdfViewModel by viewModels()

    // La foto de cada usuario se guarda en el almacenamiento interno, identificada por su RUT
    private fun archivoFoto(rut: String) = File(filesDir, "foto_perfil_$rut.jpg")

    private fun guardarFoto(rut: String, bitmap: Bitmap) {
        FileOutputStream(archivoFoto(rut)).use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, salida)
        }
    }

    private fun cargarFoto(rut: String): Bitmap? {
        val archivo = archivoFoto(rut)
        return if (archivo.exists()) BitmapFactory.decodeFile(archivo.absolutePath) else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBogadosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val sesion = sesionViewModel.sesionActual

                    // false = pantalla de carpetas (página principal), true = tu HomeScreen
                    var verPerfil by rememberSaveable { mutableStateOf(false) }

                    // Foto de perfil del usuario: se lee del almacenamiento al iniciar sesión
                    var fotoPerfil by remember(sesion?.rut) {
                        mutableStateOf(sesion?.rut?.let { cargarFoto(it) })
                    }

                    // Cámara: guarda la foto de forma permanente y luego muestra el perfil
                    val camara = rememberLauncherForActivityResult(
                        ActivityResultContracts.TakePicturePreview()
                    ) { bitmap ->
                        if (bitmap != null && sesion != null) {
                            guardarFoto(sesion.rut, bitmap)
                            fotoPerfil = bitmap
                        }
                        verPerfil = true
                    }

                    val abrirCamara: () -> Unit = {
                        try {
                            camara.launch(null)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(
                                this@MainActivity,
                                "No se encontró una app de cámara",
                                Toast.LENGTH_SHORT
                            ).show()
                            verPerfil = true
                        }
                    }

                    // "Ver perfil": si ya hay foto guardada va directo al perfil,
                    // si no, abre la cámara para tomarla la primera vez
                    val verMiPerfil: () -> Unit = {
                        if (fotoPerfil != null) verPerfil = true else abrirCamara()
                    }

                    // Cierra la sesión y avisa con un mensaje (la foto queda guardada)
                    val cerrarSesion = {
                        verPerfil = false
                        lectorPdfViewModel.cerrar()
                        sesionViewModel.cerrarSesion()
                        Toast.makeText(
                            this@MainActivity,
                            "Se cerró la sesión",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // Botón atrás del teléfono: del lector PDF vuelve a las carpetas
                    BackHandler(enabled = lectorPdfViewModel.documentoActual != null) {
                        lectorPdfViewModel.cerrar()
                    }

                    // Botón atrás del teléfono: de HomeScreen vuelve a las carpetas
                    BackHandler(enabled = sesion != null && verPerfil) {
                        verPerfil = false
                    }

                    if (sesionViewModel.cargandoLogin) {
                        // Spinner de carga mientras dura la espera después de iniciar sesión
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (sesion != null) {
                        if (verPerfil) {
                            HomeScreen(
                                sesion = sesion,
                                onCerrarSesion = cerrarSesion,
                                foto = fotoPerfil,
                                onTomarFoto = abrirCamara
                            )
                        } else {
                            // Las carpetas quedan debajo del lector, así al volver
                            // siguen desplegadas tal como las dejaste
                            Box(modifier = Modifier.fillMaxSize()) {
                                CarpetasScreen(
                                    sesion = sesion,
                                    onVerPerfil = verMiPerfil,
                                    onCerrarSesion = cerrarSesion,
                                    foto = fotoPerfil,
                                    onAbrirDocumento = { lectorPdfViewModel.abrir(it) }
                                )

                                val documento = lectorPdfViewModel.documentoActual
                                if (documento != null) {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.background
                                    ) {
                                        LectorPdfScreen(
                                            documento = documento,
                                            paginas = lectorPdfViewModel.paginas,
                                            cargando = lectorPdfViewModel.cargando,
                                            error = lectorPdfViewModel.error,
                                            onVolver = { lectorPdfViewModel.cerrar() },
                                            onDescargar = {
                                                val ok = DescargaRepository(this@MainActivity)
                                                    .descargar(documento)
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    if (ok) "Descargado: ${documento.archivo}"
                                                    else "No se pudo descargar ${documento.archivo}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Formulario(
                            viewModel = formularioViewModel,
                            onIngresoExitoso = { nombre, rut ->
                                sesionViewModel.iniciarSesion(nombre, rut)
                            }
                        )
                    }
                }
            }
        }
    }
}
