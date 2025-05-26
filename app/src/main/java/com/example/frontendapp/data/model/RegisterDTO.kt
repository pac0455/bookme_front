package com.example.frontendapp.data.model

import com.google.gson.annotations.SerializedName

data class RegisterDTO(
    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("isNegocio")
    val isNegocio: Boolean
) {
    companion object {

//        Crea un RegisterDTO a partir de un objeto Usuario

        fun fromUsuario(usuario: Usuario): RegisterDTO {
            return RegisterDTO(
                username = usuario.username ?: "",
                email = usuario.email ?: "",
                phoneNumber = usuario.phoneNumber ?: "",
                password = usuario.password ?: "",
                isNegocio = usuario.isNegocio ?: false
            )
        }
    }
}
