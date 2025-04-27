package com.example.frontendapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import com.example.frontendapp.data.remote.api.ApiService
import com.example.frontendapp.data.remote.source.Resource

class AuthRemoteDataResourceTest {

    private val authRemoteDataResource = AuthRemoteDataResource(RetrofitInstance.api)

    @Test
    fun `register user successfully`() = runBlocking {
        val usuario = Usuario(
            email = "test@example.com",
            password = "123456",
            username = "Nombre",
            telefono = "12dasdsa"
        )

        val result = authRemoteDataResource.registerUser(usuario)

        when (result) {
            is Resource.Success -> {
                println("✅ Test exitoso: ${result.data}")
            }
            is Resource.Error -> {
                println("❌ Error inesperado: ${result.message}")
            }
            is Resource.Loading -> {
                println("${result.message}")
            }

        }

        assertTrue(result is Resource.Success)
        assertEquals("Registro exitoso", (result as Resource.Success).data)
    }


    @Test
    fun `register user with empty email returns error`() = runBlocking {
        val usuario = Usuario(
            email = "",
            password = "123456"
        )

        val result = authRemoteDataResource.registerUser(usuario)
        assertTrue(result is Resource.Error)
        assertEquals("El correo electrónico no puede estar vacío.", (result as Resource.Error).message)
    }

    @Test
    fun `register user with api failure`() = runBlocking {
        val usuario = Usuario(
            email = "error@example.com",
            password = "123456"
        )

        val result = authRemoteDataResource.registerUser(usuario)
        assertTrue(result is Resource.Error)
        assertTrue(result.message!!.contains("Simulación de error"))
    }
}
