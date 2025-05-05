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

import com.example.frontendapp.data.remote.source.Resource

class AuthRemoteDataResourceTest {

    private val authRemoteDataResource = AuthRemoteDataResource(RetrofitInstance.api)


    //api/usuarios -> GET
    @Test
    fun getAllUsers() = runBlocking {
        // Simulamos un delay para que el estado "Loading" se pueda activar
        println("Iniciando test...")
        val result = authRemoteDataResource.getAll()

        when (result) {
            is Resource.Loading -> {
                println("Cargando...") // Aquí se debería imprimir si todo va bien
            }
            is Resource.Success -> {
                result.data?.forEach { println(it.username) }
            }
            is Resource.Error -> {
                println("Error: ${result.message}")
            }

        }

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `register user successfully`() = runBlocking {
        val usuario = Usuario(
            email = "test1@example.com",
            password = "12Aaaa",
            username = "Nombre",
            telefono = "12dasdsaa"
        )

        val result = authRemoteDataResource.registerUser(usuario)

        when (result) {
            is Resource.Success -> {
                println("✅ Test exitoso: ${result.data}")
            }
            is Resource.Error -> {
                println("❌ Error inesperado: ${result.message}")
            }            is Resource.Loading -> {
            print("cargando...")
        }


        }

        assertTrue(result is Resource.Success)
        assertEquals("Registro exitoso", (result as Resource.Success).data)
    }
    @Test
    fun login() = runBlocking {
        val usuario = Usuario(
            email = "test@example.com",
            password = "12Aaaa",
        )

        val result = authRemoteDataResource.login(usuario.email ?: "", usuario.password ?: "")

        when (result) {
            is Resource.Success -> {
                println("✅ Test exitoso: ${result.data}")
            }

            is Resource.Error -> {
                println("❌ Error inesperado: ${result.message}")
            }

            is Resource.Loading -> {
                print("cargando...")
            }

            null -> TODO()
        }
        assertTrue(result is Resource.Success)
    }

    @Test
    fun `register user with empty email returns error`() = runBlocking {
        val usuario = Usuario(
            email = "",
            password = "12Aaaa",
            username = "adasd"
        )

        val result = authRemoteDataResource.registerUser(usuario)
        assertTrue(result is Resource.Error)
        assertEquals("El correo electrónico no puede estar vacío.", (result as Resource.Error).message)
    }

}
