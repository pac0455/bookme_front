package com.example.frontendapp.data.model.Usuario

data class UpdateDataUserDTO(
    var id: String,
    var userName: String,
    var telefono: String,
){
    companion object{
        fun init(): UpdateDataUserDTO{
            return UpdateDataUserDTO(
                id = "",
                userName="",
                telefono = ""
            )
        }
    }
}