package com.example.frontendapp.data.model.Api

data class ValidationValidateState(
    var errors: MutableMap<String, String> = mutableMapOf()
)