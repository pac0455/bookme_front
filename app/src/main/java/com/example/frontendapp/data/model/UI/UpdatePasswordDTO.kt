package com.example.frontendapp.data.model.UI

data class UpdatePasswordDTO (
    val userId: String,
    val oldPassword: String,
    val newPassword: String
)
