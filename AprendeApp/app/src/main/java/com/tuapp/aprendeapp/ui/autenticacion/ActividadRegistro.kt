package com.tuapp.aprendeapp.ui.autenticacion

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tuapp.aprendeapp.R
import com.tuapp.aprendeapp.databinding.ActividadRegistroBinding
import com.tuapp.aprendeapp.datos.modelo.Usuario
import com.tuapp.aprendeapp.datos.remoto.ClienteRetrofit
import com.tuapp.aprendeapp.utilidades.ValidadorContrasena
import com.tuapp.aprendeapp.utilidades.mostrarToast
import com.tuapp.aprendeapp.utilidades.ocultar
import com.tuapp.aprendeapp.utilidades.mostrar
import kotlinx.coroutines.launch

class ActividadRegistro : AppCompatActivity() {

    private lateinit var binding: ActividadRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textoVolverLogin.setOnClickListener {
            finish() // Vuelve al Login
        }

        binding.botonRegistrarse.setOnClickListener {
            val email = binding.entradaEmailRegistro.text?.toString()?.trim() ?: ""
            val contrasena = binding.entradaContrasenaRegistro.text?.toString() ?: ""
            val confirmar = binding.entradaConfirmarContrasena.text?.toString() ?: ""

            // Determinar el rol seleccionado
            val rol = if (binding.radioDocente.isChecked) "Docente" else "Estudiante"

            if (validarFormulario(email, contrasena, confirmar)) {
                registrarUsuarioEnApi(email, contrasena, rol)
            }
        }
    }

    private fun validarFormulario(email: String, contrasena: String, confirmar: String): Boolean {
        var esValido = true
        binding.contenedorEmailRegistro.error = null
        binding.contenedorContrasenaRegistro.error = null
        binding.contenedorConfirmarContrasena.error = null

        // Validar Email
        if (email.isEmpty()) {
            binding.contenedorEmailRegistro.error = getString(R.string.error_email_vacio)
            esValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.contenedorEmailRegistro.error = getString(R.string.error_email_invalido)
            esValido = false
        }

        // Validar Contraseña con nuestra regla estricta
        if (contrasena.isEmpty()) {
            binding.contenedorContrasenaRegistro.error = getString(R.string.error_contrasena_vacia)
            esValido = false
        } else if (!ValidadorContrasena.esValida(contrasena)) {
            // Obtenemos el mensaje exacto del porqué falló
            binding.contenedorContrasenaRegistro.error = ValidadorContrasena.obtenerMensajeError(contrasena)
            esValido = false
        }

        // Validar Confirmación
        if (confirmar != contrasena) {
            binding.contenedorConfirmarContrasena.error = getString(R.string.error_contrasenas_no_coinciden)
            esValido = false
        }

        return esValido
    }

    private fun registrarUsuarioEnApi(email: String, contrasena: String, rol: String) {
        binding.progresoRegistro.mostrar()
        binding.botonRegistrarse.isEnabled = false

        lifecycleScope.launch {
            try {
                val nuevoUsuario = Usuario(email = email, contrasena = contrasena, rol = rol)

                // Hacemos el POST a MockAPI
                ClienteRetrofit.servicio.crearUsuario(nuevoUsuario)

                // Si no hubo excepción, se creó bien
                mostrarToast(getString(R.string.registro_exitoso))

                // Regresamos al login automáticamente
                startActivity(Intent(this@ActividadRegistro, ActividadLogin::class.java))
                finish()

            } catch (e: Exception) {
                mostrarToast(getString(R.string.error_registro_fallido))
                e.printStackTrace()
            } finally {
                binding.progresoRegistro.ocultar()
                binding.botonRegistrarse.isEnabled = true
            }
        }
    }
}