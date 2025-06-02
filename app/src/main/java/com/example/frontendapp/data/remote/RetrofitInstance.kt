package com.example.frontendapp.data.remote

import com.example.frontendapp.data.remote.api.CategoriaApi
import com.example.frontendapp.data.remote.api.HorarioApi
import com.example.frontendapp.data.remote.api.NegocioApi
import com.example.frontendapp.data.remote.api.ReservaApi
import com.example.frontendapp.data.remote.api.ServicioApi
import com.example.frontendapp.data.remote.api.UserApi
import com.example.frontendapp.data.remote.api.ValoracionApi
import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private var jwtToken: String? = null
    private val server = "http://192.168.18.3:5000/"
    private val serverPracticas = "http://172.16.83.165:5000/"
    private val localhost = "https://localhost:7211"

    private val serverTest = "https://localhost:7211/"

    private val ip = serverPracticas
    private var roles: List<String> = emptyList()
    private lateinit var userId: String

    fun getToken(): String? = jwtToken
    fun setRoles(rolesList: List<String>) {
        roles = rolesList
    }

    fun getRoles(): List<String> = roles

    // Llama a esta función para actualizar el token cuando inicies sesión o refresques
    fun setToken(token: String) {
        jwtToken = token
    }

    fun getIp(): String {
        return ip
    }

    fun getUserId(): String {
        return userId
    }

    fun setUserId(userId: String) {
        this.userId = userId
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

    val valoracionApi: ValoracionApi by lazy {
        Retrofit.Builder()
            .baseUrl(ip)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ValoracionApi::class.java)
    }

    val userApi: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl(ip)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    val negocioApi: NegocioApi by lazy {
        Retrofit.Builder()
            .baseUrl(ip)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NegocioApi::class.java)
    }
    val servicioApi: ServicioApi by lazy {
        Retrofit.Builder()
            .baseUrl(ip)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioApi::class.java)
    }
    val categoriaApi: CategoriaApi by lazy {
        Retrofit.Builder()
            .baseUrl(ip)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CategoriaApi::class.java)
    }
    val horarioApi: HorarioApi by lazy {
        Retrofit.Builder()
            .baseUrl(ip)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HorarioApi::class.java)
    }
    val reservaApi: ReservaApi by lazy {
        Retrofit.Builder()
            .baseUrl(ip)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReservaApi::class.java)
    }
}
