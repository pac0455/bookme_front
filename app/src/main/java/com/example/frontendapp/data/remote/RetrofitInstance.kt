package com.example.frontendapp.data.remote

import com.example.frontendapp.data.remote.api.NegocioApi
import com.example.frontendapp.data.remote.api.UserApi
import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitInstance {

    private var jwtToken: String? = null
    private val server= "http://192.168.18.3:5000/"
    private val serverPracticas= "http://172.16.83.165:5000/"

    private val serverTest = "https://localhost:7211/"

    // Llama a esta función para actualizar el token cuando inicies sesión o refresques
    fun setToken(token: String) {
        jwtToken = token
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            jwtToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    val userApi: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(serverPracticas)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    val negocioApi: NegocioApi by lazy {
        Retrofit.Builder()
            .baseUrl(serverPracticas)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NegocioApi::class.java)
    }
}
