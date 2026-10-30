package com.tuapp.aprendeapp.datos.remoto

import com.tuapp.aprendeapp.datos.modelo.Recurso
import com.tuapp.aprendeapp.datos.modelo.Usuario
import retrofit2.http.*

interface ServicioApi {
    // --- Autenticación ---
    @GET("usuarios")
    suspend fun obtenerUsuarios(@Query("email") email: String): List<Usuario>

    @POST("usuarios")
    suspend fun crearUsuario(@Body usuario: Usuario): Usuario

    // --- CRUD Recursos ---
    @GET("recursos")
    suspend fun obtenerRecursos(): List<Recurso>

    // 👇 ESTA ES LA QUE FALTABA 👇
    @GET("recursos/{id}")
    suspend fun obtenerRecursoPorId(@Path("id") id: String): Recurso

    @POST("recursos")
    suspend fun crearRecurso(@Body recurso: Recurso): Recurso

    @PUT("recursos/{id}")
    suspend fun actualizarRecurso(@Path("id") id: String, @Body recurso: Recurso): Recurso

    @DELETE("recursos/{id}")
    suspend fun eliminarRecurso(@Path("id") id: String)
}