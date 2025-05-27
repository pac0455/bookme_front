package com.example.frontendapp.data.model

import com.google.gson.annotations.SerializedName

data class Categoria(

    @SerializedName("id")
    val id: Int=-1,

    @SerializedName("nombre")
    val nombre: String="",

)
