package com.tuapp.aprendeapp.ui.favoritos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuapp.aprendeapp.databinding.ActividadPrincipalBinding
import com.tuapp.aprendeapp.datos.local.GestorFavoritos
import com.tuapp.aprendeapp.datos.remoto.ClienteRetrofit
import com.tuapp.aprendeapp.ui.detalle.ActividadDetalleRecurso
import com.tuapp.aprendeapp.ui.principal.AdaptadorRecursos
import com.tuapp.aprendeapp.utilidades.mostrar
import com.tuapp.aprendeapp.utilidades.ocultar
import kotlinx.coroutines.launch

class ActividadFavoritos : AppCompatActivity() {

    // Reutilizamos el layout de la pantalla principal
    private lateinit var binding: ActividadPrincipalBinding
    private lateinit var adaptador: AdaptadorRecursos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Mis Favoritos"
        binding.toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Ocultamos lo que no necesitamos en esta vista
        binding.contenedorBusqueda.ocultar()
        binding.botonAgregar.ocultar()

        configurarRecyclerView()
        cargarFavoritos()
    }

    private fun configurarRecyclerView() {
        adaptador = AdaptadorRecursos(emptyList()) { recurso ->
            val intent = Intent(this, ActividadDetalleRecurso::class.java)
            intent.putExtra("recurso_id", recurso.id)
            startActivity(intent)
        }
        binding.listaRecursos.layoutManager = LinearLayoutManager(this)
        binding.listaRecursos.adapter = adaptador
    }

    private fun cargarFavoritos() {
        binding.progresoPrincipal.mostrar()
        val idsFavoritos = GestorFavoritos.obtenerFavoritos(this)

        if (idsFavoritos.isEmpty()) {
            binding.textoVacio.mostrar()
            binding.progresoPrincipal.ocultar()
            return
        }

        lifecycleScope.launch {
            try {
                val todosLosRecursos = ClienteRetrofit.servicio.obtenerRecursos()
                // Filtramos solo los que están en favoritos
                val favoritos = todosLosRecursos.filter { idsFavoritos.contains(it.id) }

                adaptador.actualizarLista(favoritos)
                if (favoritos.isEmpty()) binding.textoVacio.mostrar() else binding.textoVacio.ocultar()

            } catch (e: Exception) {
                binding.textoVacio.mostrar()
            } finally {
                binding.progresoPrincipal.ocultar()
            }
        }
    }
}