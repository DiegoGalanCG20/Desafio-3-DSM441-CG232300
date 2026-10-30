package com.tuapp.aprendeapp.utilidades

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.mostrar() { visibility = View.VISIBLE }
fun View.ocultar() { visibility = View.GONE }
fun Context.mostrarToast(mensaje: String) {
    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
}