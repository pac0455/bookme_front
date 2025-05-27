package com.example.frontendapp.data.model.Usuario

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Usuario(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("userName")  // Asegúrate de usar el nombre correcto de la clave en la respuesta JSON
    var username: String? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,

    @SerializedName("password")
    val password: String? = null,

    val firebaseUid: String? = null,

    @SerializedName("rol")
    val rol: String? = null,

    @SerializedName("fecha_registro")
    val fechaRegistro: Date? = null,
    @SerializedName("IsNegocio")
    val isNegocio: Boolean = false
)
fun Usuario.toRegisterDTO(): RegisterDTO {
    return RegisterDTO(
        username = this.username?.trim() ?: "",
        email = this.email?.trim() ?: "",
        phoneNumber = this.phoneNumber?.trim() ?: "",
        password = this.password?.trim() ?: "",
        isNegocio = this.isNegocio
    )
}