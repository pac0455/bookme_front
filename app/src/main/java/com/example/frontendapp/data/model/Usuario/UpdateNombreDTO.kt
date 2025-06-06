package com.example.frontendapp.data.model.Usuario

data class UpdateNombreDTO(
    var id: String,
    var userName: String
){
    companion object{
        fun init(): UpdateNombreDTO{
            return UpdateNombreDTO(
                id = "",
                userName="",
            )
        }
    }
}