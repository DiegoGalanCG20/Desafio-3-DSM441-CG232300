package com.tuapp.aprendeapp.ui.principal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuapp.aprendeapp.datos.modelo.Recurso
import com.tuapp.aprendeapp.datos.remoto.ClienteRetrofit
import kotlinx.coroutines.launch

class ViewModelRecursos : ViewModel() {

    private val _listaRecursos = MutableLiveData<List<Recurso>>()
    val listaRecursos: LiveData<List<Recurso>> = _listaRecursos

    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> = _cargando

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var todosLosRecursos: List<Recurso> = emptyList()

    fun cargarRecursos() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                todosLosRecursos = ClienteRetrofit.servicio.obtenerRecursos()
                _listaRecursos.value = todosLosRecursos
                _cargando.value = false
            } catch (e: Exception) {
                _error.value = "Error al cargar: ${e.message}"
                _cargando.value = false
            }
        }
    }

    // Filtro de búsqueda (por título o por ID)
    fun buscar(texto: String) {
        if (texto.isBlank()) {
            _listaRecursos.value = todosLosRecursos
        } else {
            val filtrados = todosLosRecursos.filter {
                it.titulo.contains(texto, ignoreCase = true) ||
                        it.id == texto
            }
            _listaRecursos.value = filtrados
        }
    }
}