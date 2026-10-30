package com.tuapp.aprendeapp.ui.autenticacion
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.aprendeapp.datos.modelo.Usuario
import com.tuapp.aprendeapp.datos.remoto.ClienteRetrofit
import com.tuapp.aprendeapp.datos.repositorio.RepositorioAutenticacion
import kotlinx.coroutines.launch

class ViewModelAutenticacion : ViewModel() {

    private val repositorio = RepositorioAutenticacion(ClienteRetrofit.servicio)

    // Estados expuestos a la Activity
    private val _cargando = MutableLiveData(false)
    val cargando: LiveData<Boolean> = _cargando

    private val _usuarioLogueado = MutableLiveData<Usuario?>()
    val usuarioLogueado: LiveData<Usuario?> = _usuarioLogueado

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun iniciarSesion(email: String, contrasena: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            val resultado = repositorio.iniciarSesion(email, contrasena)
            _cargando.value = false

            resultado.onSuccess { usuario ->
                _usuarioLogueado.value = usuario
            }.onFailure { ex ->
                _error.value = ex.message ?: "Error desconocido"
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}