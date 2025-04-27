package com.example.frontendapp.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Usuario(
    @SerializedName("id")
    val id: String? = null,

    val username: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("password")
    val password: String? = null,

    val firebaseUid: String? = null,

    @SerializedName("rol")
    val rol: String? = null,

    @SerializedName("fecha_registro")
    val fechaRegistro: Date? = null
)
