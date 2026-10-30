package com.tuapp.aprendeapp.datos.modelo

data class Recurso(
    val id: String? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = "",
    val enlace: String = "",
    val imagen: String = "",
    val promedio: Double = 0.0,
    val votos: Int = 0
)