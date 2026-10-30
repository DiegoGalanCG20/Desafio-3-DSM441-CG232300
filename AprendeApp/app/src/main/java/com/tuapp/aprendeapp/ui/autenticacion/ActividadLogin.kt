package com.tuapp.aprendeapp.ui.autenticacion

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tuapp.aprendeapp.R
import com.tuapp.aprendeapp.databinding.ActividadLoginBinding
import com.tuapp.aprendeapp.datos.local.GestorSesion
import com.tuapp.aprendeapp.ui.principal.ActividadPrincipal
import com.tuapp.aprendeapp.utilidades.mostrarToast
import com.tuapp.aprendeapp.utilidades.ocultar
import com.tuapp.aprendeapp.utilidades.mostrar

class ActividadLogin : AppCompatActivity() {

    private lateinit var binding: ActividadLoginBinding
    private val viewModel: ViewModelAutenticacion by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val usuarioGuardado = GestorSesion.obtenerUsuario(this)
        if (usuarioGuardado != null) {
            startActivity(Intent(this, ActividadPrincipal::class.java))
            finish()
            return
        }
        binding = ActividadLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarListeners()
        observarViewModel()
    }

    private fun configurarListeners() {
        binding.botonIngresar.setOnClickListener {
            val email = binding.entradaEmail.text?.toString()?.trim() ?: ""
            val contrasena = binding.entradaContrasena.text?.toString() ?: ""

            if (validarCampos(email, contrasena)) {
                viewModel.iniciarSesion(email, contrasena)
            }
        }

        binding.textoRegistro.setOnClickListener {
            startActivity(Intent(this, ActividadRegistro::class.java))
        }
    }

    private fun validarCampos(email: String, contrasena: String): Boolean {
        var valido = true

        binding.contenedorEmail.error = null
        binding.contenedorContrasena.error = null

        if (email.isEmpty()) {
            binding.contenedorEmail.error = getString(R.string.error_email_vacio)
            valido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.contenedorEmail.error = getString(R.string.error_email_invalido)
            valido = false
        }

        if (contrasena.isEmpty()) {
            binding.contenedorContrasena.error = getString(R.string.error_contrasena_vacia)
            valido = false
        }

        return valido
    }

    private fun observarViewModel() {
        viewModel.cargando.observe(this) { cargando ->
            if (cargando) {
                binding.progresoCarga.mostrar()
                binding.botonIngresar.isEnabled = false
            } else {
                binding.progresoCarga.ocultar()
                binding.botonIngresar.isEnabled = true
            }
        }

        viewModel.usuarioLogueado.observe(this) { usuario ->
            if (usuario != null) {
                GestorSesion.guardarUsuario(this, usuario)
                mostrarToast("${getString(R.string.bienvenido)} ${usuario.email}")
                startActivity(Intent(this, ActividadPrincipal::class.java))
                finish()
            }
        }

        viewModel.error.observe(this) { mensaje ->
            if (!mensaje.isNullOrEmpty()) {
                mostrarToast(mensaje)
                viewModel.limpiarError()
            }
        }
    }
}