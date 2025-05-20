package com.example.frontendapp.data.remote

import com.example.frontendapp.data.remote.api.UserApi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//Singleton
object RetrofitInstance {
    val userApi: UserApi by lazy {
        Retrofit.Builder()
            //.baseUrl("http://localhost:5000/")
            .baseUrl("http://172.16.83.165:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }
    val negocioApi: UserApi by lazy {
        Retrofit.Builder()
            //.baseUrl("http://localhost:5000/")
            .baseUrl("http://172.16.83.165:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }
}