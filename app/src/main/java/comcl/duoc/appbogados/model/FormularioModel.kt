package comcl.duoc.appbogados.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FormularioModel {
    var nombre by mutableStateOf("")

    var rut by mutableStateOf("")
}