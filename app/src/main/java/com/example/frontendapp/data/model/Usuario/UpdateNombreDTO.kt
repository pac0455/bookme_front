package com.example.frontendapp.data.model.Usuario

data class UpdateNombreDTO(
    var id: String,
    var userName: String,
    var telefono: String,
){
    companion object{
        fun init(): UpdateNombreDTO{
            return UpdateNombreDTO(
                id = "",
                userName="",
                telefono = ""
            )
        }
    }
}