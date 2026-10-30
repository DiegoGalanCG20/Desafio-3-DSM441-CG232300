package com.tuapp.aprendeapp.datos.modelo

data class Usuario(
    val id: String? = null,
    val email: String = "",
    val contrasena: String = "",
    val rol: String = "Estudiante" // "Estudiante" o "Docente"
)