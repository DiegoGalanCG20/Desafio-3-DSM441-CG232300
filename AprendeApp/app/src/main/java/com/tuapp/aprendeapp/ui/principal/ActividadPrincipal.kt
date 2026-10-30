package com.tuapp.aprendeapp.ui.principal

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuapp.aprendeapp.R
import com.tuapp.aprendeapp.databinding.ActividadPrincipalBinding
import com.tuapp.aprendeapp.datos.local.GestorSesion
import com.tuapp.aprendeapp.datos.modelo.Recurso
import com.tuapp.aprendeapp.ui.autenticacion.ActividadLogin
import com.tuapp.aprendeapp.ui.detalle.ActividadDetalleRecurso
import com.tuapp.aprendeapp.ui.docente.ActividadFormularioRecurso
import com.tuapp.aprendeapp.utilidades.mostrarToast
import com.tuapp.aprendeapp.utilidades.ocultar
import com.tuapp.aprendeapp.utilidades.mostrar
import com.tuapp.aprendeapp.ui.favoritos.ActividadFavoritos

class ActividadPrincipal : AppCompatActivity() {

    private lateinit var binding: ActividadPrincipalBinding
    private val viewModel: ViewModelRecursos by viewModels()
    private lateinit var adaptador: AdaptadorRecursos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbar()
        configurarRecyclerView()
        configurarBuscador()
        configurarRol()
        observarViewModel()

        viewModel.cargarRecursos()
    }
    override fun onResume() {
        super.onResume()
        viewModel.cargarRecursos()
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun configurarRecyclerView() {
        adaptador = AdaptadorRecursos(emptyList()) { recurso ->
            // Al hacer clic en una tarjeta, vamos al detalle
            if (recurso.id != null) {
                val intent = Intent(this, ActividadDetalleRecurso::class.java)
                intent.putExtra("recurso_id", recurso.id)
                startActivity(intent)
            } else {
                mostrarToast("Error: Este recurso no tiene un ID válido")
            }
        }
        binding.listaRecursos.layoutManager = LinearLayoutManager(this)
        binding.listaRecursos.adapter = adaptador
    }

    private fun configurarBuscador() {
        binding.entradaBusqueda.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.buscar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun configurarRol() {
        val usuario = GestorSesion.obtenerUsuario(this)

        if (usuario?.rol == "Docente") {
            // Si es Docente, mostramos el botón flotante para agregar
            binding.botonAgregar.mostrar()
            binding.botonAgregar.setOnClickListener {
                startActivity(Intent(this, ActividadFormularioRecurso::class.java))
            }
        } else {
            // Si es Estudiante, lo ocultamos
            binding.botonAgregar.ocultar()
        }
    }

    private fun observarViewModel() {
        viewModel.listaRecursos.observe(this) { lista ->
            adaptador.actualizarLista(lista)
            binding.textoVacio.visibility = if (lista.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.cargando.observe(this) { cargando ->
            if (cargando) binding.progresoPrincipal.mostrar() else binding.progresoPrincipal.ocultar()
        }

        viewModel.error.observe(this) { error ->
            if (error != null) mostrarToast(error)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        val usuario = GestorSesion.obtenerUsuario(this)
        if (usuario?.rol == "Docente") {
            menu?.findItem(R.id.accion_favoritos)?.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.accion_favoritos -> {
                startActivity(Intent(this, ActividadFavoritos::class.java))
                true
            }
            R.id.accion_cerrar_sesion -> {
                GestorSesion.cerrarSesion(this)
                val intent = Intent(this, ActividadLogin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}