package comcl.duoc.appbogados


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import comcl.duoc.appbogados.ui.*
import comcl.duoc.appbogados.ui.theme.AppBogadosTheme
import comcl.duoc.appbogados.viewmodel.FormularioViewModel

class MainActivity : ComponentActivity() {

    private val viewModelForm = FormularioViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBogadosTheme(){ // <- aquí va el nombre de TU TEMA, lo puedes encontrar en ui/theme/Theme.kt
                Formulario(viewModelForm)
            }
        }
    }



}