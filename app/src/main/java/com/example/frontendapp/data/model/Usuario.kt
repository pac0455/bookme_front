package com.example.frontendapp.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date
data class Usuario(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("nombre")
    val nombre: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("telefono")
    val telefono: String? = null,

    @SerializedName("contrasena_hash")
    val contrasenaHash: String? = null,

    @SerializedName("firebase_uid")
    val firebaseUid: String? = null,

    @SerializedName("rol")
    val rol: String? = null,

    @SerializedName("fecha_registro")
    val fechaRegistro: Date? = null
)
