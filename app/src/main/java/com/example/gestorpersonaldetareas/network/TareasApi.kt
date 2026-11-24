package com.example.gestorpersonaldetareas.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val URL_BASE = "https://jsonplaceholder.typicode.com/"

interface TareasApiServicio {
    @GET("todos")
    suspend fun obtenerTareas(@Query("userId") userId: Int = 1): List<TareaRemota>
}

object TareasApi {
    val servicio: TareasApiServicio by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TareasApiServicio::class.java)
    }
}
