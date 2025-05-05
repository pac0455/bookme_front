package com.example.frontendapp.data.remote

import com.example.frontendapp.data.remote.api.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//Singleton
object RetrofitInstance {
    val api: UserApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://localhost:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }
}