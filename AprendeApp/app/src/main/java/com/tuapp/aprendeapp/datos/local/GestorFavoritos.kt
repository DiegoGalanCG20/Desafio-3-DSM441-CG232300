package com.tuapp.aprendeapp.datos.local

import android.content.Context

object GestorFavoritos {
    private const val PREF_NAME = "favoritos_prefs"
    private const val KEY_FAVORITOS = "ids_favoritos"

    // Obtiene el conjunto de IDs guardados
    fun obtenerFavoritos(contexto: Context): Set<String> {
        val prefs = contexto.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FAVORITOS, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    }

    // Agrega o quita un favorito, devuelve true si se agregó, false si se quitó
    fun alternarFavorito(contexto: Context, idRecurso: String): Boolean {
        val prefs = contexto.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // 👇 AQUÍ ESTÁ EL CAMBIO 👇
        val favoritos = obtenerFavoritos(contexto).toMutableSet()

        val esFavoritoAhora: Boolean
        if (favoritos.contains(idRecurso)) {
            favoritos.remove(idRecurso)
            esFavoritoAhora = false
        } else {
            favoritos.add(idRecurso)
            esFavoritoAhora = true
        }

        // Guardamos el set actualizado
        prefs.edit().putStringSet(KEY_FAVORITOS, favoritos).apply()
        return esFavoritoAhora
    }

    // Verifica si un recurso específico está en favoritos
    fun esFavorito(contexto: Context, idRecurso: String): Boolean {
        return obtenerFavoritos(contexto).contains(idRecurso)
    }
}