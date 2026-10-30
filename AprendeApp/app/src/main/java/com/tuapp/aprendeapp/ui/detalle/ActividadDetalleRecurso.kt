package com.tuapp.aprendeapp.ui.detalle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.tuapp.aprendeapp.R
import com.tuapp.aprendeapp.databinding.ActividadDetalleRecursoBinding
import com.tuapp.aprendeapp.datos.local.GestorFavoritos
import com.tuapp.aprendeapp.datos.local.GestorSesion
import com.tuapp.aprendeapp.datos.modelo.Recurso
import com.tuapp.aprendeapp.datos.remoto.ClienteRetrofit
import com.tuapp.aprendeapp.ui.docente.ActividadFormularioRecurso
import com.tuapp.aprendeapp.utilidades.mostrarToast
import com.tuapp.aprendeapp.utilidades.ocultar
import com.tuapp.aprendeapp.utilidades.mostrar
import kotlinx.coroutines.launch

class ActividadDetalleRecurso : AppCompatActivity() {

    private lateinit var binding: ActividadDetalleRecursoBinding
    private var recursoActual: Recurso? = null
    private var idRecurso: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadDetalleRecursoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idRecurso = intent.getStringExtra("recurso_id")

        binding.toolbarDetalle.setNavigationOnClickListener { finish() }

        if (idRecurso != null) {
            cargarDetalleRecurso(idRecurso!!)
        } else {
            mostrarToast(getString(R.string.error_recurso_no_encontrado))
            finish()
        }
    }

    private fun cargarDetalleRecurso(id: String) {
        binding.progresoDetalle.mostrar()
        lifecycleScope.launch {
            try {
                recursoActual = ClienteRetrofit.servicio.obtenerRecursoPorId(id)
                recursoActual?.let { recurso ->
                    llenarUI(recurso)
                    configurarBotonesPorRol(recurso)
                }
            } catch (e: Exception) {
                mostrarToast(getString(R.string.error_cargar_detalle))
                finish()
            } finally {
                binding.progresoDetalle.ocultar()
            }
        }
    }

    private fun llenarUI(recurso: Recurso) {
        binding.textoTituloDetalle.text = recurso.titulo
        binding.textoTipoDetalle.text = getString(R.string.etiqueta_tipo) + " " + recurso.tipo
        binding.textoDescripcionDetalle.text = recurso.descripcion

        if (recurso.votos > 0) {
            val promedioFormateado = String.format("%.1f", recurso.promedio)
            binding.textoPromedioDetalle.text = "${getString(R.string.etiqueta_promedio)} ⭐ $promedioFormateado (${recurso.votos} ${getString(R.string.votos)})"
        } else {
            binding.textoPromedioDetalle.text = getString(R.string.sin_calificacion)
        }

        if (recurso.imagen.isNotEmpty()) {
            Glide.with(this).load(recurso.imagen).into(binding.imagenDetalle)
        }

        // Abrir enlace en el navegador
        binding.botonAbrirEnlace.setOnClickListener {
            if (recurso.enlace.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recurso.enlace))
                startActivity(intent)
            }
        }
    }

    private fun configurarBotonesPorRol(recurso: Recurso) {
        val usuario = GestorSesion.obtenerUsuario(this)

        if (usuario?.rol == "Docente") {
            // Mostrar botones de Docente
            binding.seccionDocente.visibility = View.VISIBLE
            binding.seccionEstudiante.visibility = View.GONE

            binding.botonEditarDetalle.setOnClickListener {
                val intent = Intent(this, ActividadFormularioRecurso::class.java)
                intent.putExtra("recurso_id", recurso.id)
                startActivity(intent)
            }

            binding.botonEliminarDetalle.setOnClickListener {
                mostrarDialogoEliminar(recurso.id!!)
            }

        } else {
            // Mostrar sección de Estudiante
            binding.seccionDocente.visibility = View.GONE
            binding.seccionEstudiante.visibility = View.VISIBLE

            // Configurar Favoritos
            actualizarEstadoFavorito(recurso.id!!)
            binding.botonFavorito.setOnClickListener {
                val esFav = GestorFavoritos.alternarFavorito(this, recurso.id!!)
                if (esFav) {
                    mostrarToast(getString(R.string.favorito_agregado))
                } else {
                    mostrarToast(getString(R.string.favorito_removido))
                }
                actualizarEstadoFavorito(recurso.id!!)
            }

            // Configurar Rating
            binding.barraCalificacion.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    guardarCalificacion(recurso, rating.toInt())
                }
            }
        }
    }

    private fun actualizarEstadoFavorito(idRecurso: String) {
        val esFav = GestorFavoritos.esFavorito(this, idRecurso)
        if (esFav) {
            binding.botonFavorito.text = getString(R.string.quitar_favorito)
            binding.botonFavorito.setIconResource(android.R.drawable.btn_star_big_on)
        } else {
            binding.botonFavorito.text = getString(R.string.anadir_favorito)
            binding.botonFavorito.setIconResource(android.R.drawable.btn_star_big_off)
        }
    }

    private fun guardarCalificacion(recurso: Recurso, estrellas: Int) {
        binding.progresoDetalle.mostrar()

        lifecycleScope.launch {
            try {
                val totalVotos = recurso.votos + 1
                val nuevoPromedio = ((recurso.promedio * recurso.votos) + estrellas) / totalVotos

                val recursoActualizado = recurso.copy(promedio = nuevoPromedio, votos = totalVotos)
                ClienteRetrofit.servicio.actualizarRecurso(recurso.id!!, recursoActualizado)

                mostrarToast(getString(R.string.calificacion_guardada))
                cargarDetalleRecurso(recurso.id!!) // Recargar para ver el nuevo promedio
            } catch (e: Exception) {
                mostrarToast(getString(R.string.error_guardar_calificacion))
            } finally {
                binding.progresoDetalle.ocultar()
            }
        }
    }

    private fun mostrarDialogoEliminar(id: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.boton_eliminar))
            .setMessage(getString(R.string.confirmar_eliminar))
            .setPositiveButton(getString(R.string.si_eliminar)) { _, _ ->
                eliminarRecurso(id)
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    private fun eliminarRecurso(id: String) {
        binding.progresoDetalle.mostrar()
        lifecycleScope.launch {
            try {
                ClienteRetrofit.servicio.eliminarRecurso(id)
                mostrarToast(getString(R.string.recurso_eliminado))
                finish() // Volver a la lista
            } catch (e: Exception) {
                mostrarToast(getString(R.string.error_eliminar))
            } finally {
                binding.progresoDetalle.ocultar()
            }
        }
    }
}