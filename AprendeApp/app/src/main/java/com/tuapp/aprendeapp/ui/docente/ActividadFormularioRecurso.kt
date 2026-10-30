package com.tuapp.aprendeapp.ui.docente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuapp.aprendeapp.R
import com.tuapp.aprendeapp.databinding.ActividadFormularioRecursoBinding
import com.tuapp.aprendeapp.datos.modelo.Recurso
import com.tuapp.aprendeapp.datos.remoto.ClienteRetrofit
import com.tuapp.aprendeapp.utilidades.mostrarToast
import com.tuapp.aprendeapp.utilidades.ocultar
import com.tuapp.aprendeapp.utilidades.mostrar
import kotlinx.coroutines.launch

class ActividadFormularioRecurso : AppCompatActivity() {

    private lateinit var binding: ActividadFormularioRecursoBinding
    private var idRecurso: String? = null
    private var recursoExistente: Recurso? = null
    private var esEdicion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadFormularioRecursoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibimos el ID del recurso si venimos de la pantalla de detalle
        idRecurso = intent.getStringExtra("recurso_id")
        esEdicion = idRecurso != null

        configurarUI()

        if (esEdicion) {
            cargarRecursoParaEditar()
        }
    }

    private fun configurarUI() {
        if (esEdicion) {
            binding.toolbarFormulario.title = getString(R.string.titulo_editar_recurso)
            binding.botonGuardar.text = getString(R.string.boton_actualizar)
        } else {
            binding.toolbarFormulario.title = getString(R.string.titulo_nuevo_recurso)
            binding.botonGuardar.text = getString(R.string.boton_guardar)
        }

        binding.toolbarFormulario.setNavigationOnClickListener { finish() }

        binding.botonGuardar.setOnClickListener {
            if (validarCampos()) {
                if (esEdicion) {
                    actualizarRecurso()
                } else {
                    crearRecurso()
                }
            }
        }
    }

    private fun cargarRecursoParaEditar() {
        binding.progresoFormulario.mostrar()
        lifecycleScope.launch {
            try {
                recursoExistente = ClienteRetrofit.servicio.obtenerRecursoPorId(idRecurso!!)
                recursoExistente?.let { recurso ->
                    binding.entradaTitulo.setText(recurso.titulo)
                    binding.entradaDescripcion.setText(recurso.descripcion)
                    binding.entradaTipo.setText(recurso.tipo)
                    binding.entradaEnlace.setText(recurso.enlace)
                    binding.entradaImagen.setText(recurso.imagen)
                }
            } catch (e: Exception) {
                mostrarToast("Error al cargar los datos del recurso")
                finish()
            } finally {
                binding.progresoFormulario.ocultar()
            }
        }
    }

    private fun validarCampos(): Boolean {
        var esValido = true
        binding.contenedorTitulo.error = null
        binding.contenedorDescripcion.error = null
        binding.contenedorTipo.error = null
        binding.contenedorEnlace.error = null

        if (binding.entradaTitulo.text.isNullOrBlank()) {
            binding.contenedorTitulo.error = "Requerido"
            esValido = false
        }
        if (binding.entradaDescripcion.text.isNullOrBlank()) {
            binding.contenedorDescripcion.error = "Requerido"
            esValido = false
        }
        if (binding.entradaTipo.text.isNullOrBlank()) {
            binding.contenedorTipo.error = "Requerido"
            esValido = false
        }
        if (binding.entradaEnlace.text.isNullOrBlank()) {
            binding.contenedorEnlace.error = "Requerido"
            esValido = false
        }

        return esValido
    }

    private fun obtenerRecursoDelFormulario(): Recurso {
        // Si estamos editando, conservamos el ID, promedio y votos actuales
        val id = recursoExistente?.id
        val promedio = recursoExistente?.promedio ?: 0.0
        val votos = recursoExistente?.votos ?: 0

        return Recurso(
            id = id,
            titulo = binding.entradaTitulo.text.toString().trim(),
            descripcion = binding.entradaDescripcion.text.toString().trim(),
            tipo = binding.entradaTipo.text.toString().trim(),
            enlace = binding.entradaEnlace.text.toString().trim(),
            imagen = binding.entradaImagen.text.toString().trim(),
            promedio = promedio,
            votos = votos
        )
    }

    private fun crearRecurso() {
        binding.progresoFormulario.mostrar()
        binding.botonGuardar.isEnabled = false

        lifecycleScope.launch {
            try {
                val nuevoRecurso = obtenerRecursoDelFormulario()
                ClienteRetrofit.servicio.crearRecurso(nuevoRecurso)
                mostrarToast(getString(R.string.recurso_guardado))
                finish() // Volvemos a la lista
            } catch (e: Exception) {
                mostrarToast(getString(R.string.error_guardar_recurso))
            } finally {
                binding.progresoFormulario.ocultar()
                binding.botonGuardar.isEnabled = true
            }
        }
    }

    private fun actualizarRecurso() {
        binding.progresoFormulario.mostrar()
        binding.botonGuardar.isEnabled = false

        lifecycleScope.launch {
            try {
                val recursoActualizado = obtenerRecursoDelFormulario()
                ClienteRetrofit.servicio.actualizarRecurso(idRecurso!!, recursoActualizado)
                mostrarToast(getString(R.string.recurso_actualizado))
                finish() // Volvemos a la lista
            } catch (e: Exception) {
                mostrarToast(getString(R.string.error_guardar_recurso))
            } finally {
                binding.progresoFormulario.ocultar()
                binding.botonGuardar.isEnabled = true
            }
        }
    }
}