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

    private val user = Usuario(
        id = "13fe2a54-0499-4d62-8295-dde34fe3872a",
        email = "test@example.com",
        password = "12Aaaa",
        username = "Nombre",
        phoneNumber = "12dasdsaa"
    )
    @Test
    fun deleteUser() = runBlocking {
        val result = authRemoteDataResource.delete("13fe2a54-0499-4d62-8295-dde34fe3872a")

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
            is Resource.None ->{}
        }

        assertTrue(result is Resource.Success)
    }
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
            is Resource.None ->{}


        }

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `register user successfully`() = runBlocking {


        val result = authRemoteDataResource.registerUser(user)

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
            is Resource.None ->{}


        }

        assertTrue(result is Resource.Success)
        assertEquals("Registro exitoso", (result as Resource.Success).data)
    }
    @Test
    fun login() = runBlocking {


        val result = authRemoteDataResource.login(user)

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
            is Resource.None ->{}
        }
        assertTrue(result is Resource.Success)
    }

    @Test
    fun `register user with empty email returns error`() = runBlocking {
        val usuario = Usuario(
            email = "",
            password = user.password,
            username = user.username
        )

        val result = authRemoteDataResource.registerUser(usuario)
        assertTrue(result is Resource.Error)
        assertEquals("El correo electrónico no puede estar vacío.", (result as Resource.Error).message)
    }

}
