package com.example.frontendapp.data.model

enum class ERol {
    CLIENTE,
    NEGOCIO,
    ADMIN;

    override fun toString(): String {
        return name
    }
}