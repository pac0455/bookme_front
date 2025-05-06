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

    private var user = Usuario(
        email = "test@example.com",
        password = "12Aaaa",
        username = "Nombre",
        phoneNumber = "12dasdsaa"
    )
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
    @Test
    fun `register user successfully`() = runBlocking {


        val result = authRemoteDataResource.registerUser(user)

        when (result) {
            is Resource.Success -> {
                val usuarioResponse = result.data?.usuario
                if (usuarioResponse != null) {
                    user.id = usuarioResponse.id ?: ""
                }
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
    fun deleteUser() = runBlocking {
        val result = authRemoteDataResource.delete(user.email ?: "")
//        val result = authRemoteDataResource.delete("41b20836-d410-45b5-9bf5-0575cedb5df1")


        when (result) {
            is Resource.Success -> {
                println("✅ Test exitoso: ${result.data}")
            }
            is Resource.Error -> {
                println("El usuario es ${user.email}")
                println("❌ Error inesperado: ${result.message}")
            }
            is Resource.Loading -> {
                print("cargando...")
            }
            is Resource.None ->{}
        }

        assertTrue(result is Resource.Success)
    }
}
