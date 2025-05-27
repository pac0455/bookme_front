package com.example.frontendapp.data.model.UI

enum class ERol {
    CLIENTE,
    NEGOCIO,
    ADMIN;

    override fun toString(): String {
        return name
    }
}