package com.tuapp.aprendeapp.datos.local

import android.content.Context
import com.tuapp.aprendeapp.datos.modelo.Usuario

object GestorSesion {
    private const val PREF_NAME = "sesion_usuario"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROL = "rol"
    private const val KEY_ID = "id"

    fun guardarUsuario(contexto: Context, usuario: Usuario) {
        val prefs = contexto.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_EMAIL, usuario.email)
            putString(KEY_ROL, usuario.rol)
            putString(KEY_ID, usuario.id)
            apply()
        }
    }

    fun obtenerUsuario(contexto: Context): Usuario? {
        val prefs = contexto.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val rol = prefs.getString(KEY_ROL, "Estudiante") ?: "Estudiante"
        val id = prefs.getString(KEY_ID, null)
        return Usuario(id, email, "", rol)
    }

    fun cerrarSesion(contexto: Context) {
        contexto.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}