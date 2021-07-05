package com.example.pfinal.server


import androidx.annotation.Keep
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 *  Efetua a ligação com o servidor
 */
@Keep
object RetrofitClient {

    // url do servidor
    private const val URL = "http://jointvlab.ipt.pt/~cognition"

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    private val okHttp = OkHttpClient.Builder().addInterceptor(logger)

    private val builder = Retrofit.Builder().baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())

    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}