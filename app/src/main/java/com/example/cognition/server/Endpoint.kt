package com.example.pfinal.server


import androidx.annotation.Keep
import retrofit2.Call
import retrofit2.http.*

/**
 * Requests que serão feitos à API
 */
@Keep
interface Endpoint {

    //POST Request para o url do servidor /ocorrenciasapi
    @Headers("Content-Type: application/json")
    @POST("ocorrenciasapi")
    fun addOcorrencia(@Body ocorrenciaData: Ocorrencia): Call<Ocorrencia>

    //GET Request para o url do servidor /ocorrenciasapi/{dispositivo}
    @GET("ocorrenciasapi/{dispositivo}")
    fun getDevice(@Path("dispositivo") dispositivo: String): Call<List<Ocorrencia>>


}