package com.tuapp.aprendeapp.utilidades

object ValidadorContrasena {
    fun esValida(contrasena: String): Boolean {
        if (contrasena.length < 12) return false
        if (!contrasena.any { it.isUpperCase() }) return false
        if (!contrasena.any { it.isLowerCase() }) return false
        if (!contrasena.any { it.isDigit() }) return false
        if (!contrasena.any { "!@#$%^&*".contains(it) }) return false
        return true
    }

    fun obtenerMensajeError(contrasena: String): String? {
        if (contrasena.length < 12) return "Mínimo 12 caracteres"
        if (!contrasena.any { it.isUpperCase() }) return "Falta una mayúscula"
        if (!contrasena.any { it.isLowerCase() }) return "Falta una minúscula"
        if (!contrasena.any { it.isDigit() }) return "Falta un número"
        if (!contrasena.any { "!@#$%^&*".contains(it) }) return "Falta un carácter especial"
        return null
    }
}