package com.tuapp.aprendeapp.datos.repositorio

import com.tuapp.aprendeapp.datos.modelo.Usuario
import com.tuapp.aprendeapp.datos.remoto.ServicioApi

class RepositorioAutenticacion(private val servicio: ServicioApi) {

    suspend fun iniciarSesion(email: String, contrasena: String): Result<Usuario> {
        return try {
            val usuarios = servicio.obtenerUsuarios(email)
            val usuario = usuarios.firstOrNull {
                it.email.equals(email, ignoreCase = true) && it.contrasena == contrasena
            }
            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión. Verifica tu internet"))
        }
    }
}