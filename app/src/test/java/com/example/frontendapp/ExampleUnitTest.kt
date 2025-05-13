package com.example.frontendapp

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.source.Resource
import kotlinx.coroutines.runBlocking

class AuthRemoteDataResourceTest {

    private val authRemoteDataResource = AuthRemoteDataResource(RetrofitInstance.api)

    private val user = Usuario(
        email = "franhidalc@gmail.com",
        password = "12Aaaa",
        username = "Nombre",
        phoneNumber = "12dasdsaa"
    )

    /**
     * Antes de cada test, intentamos registrar el usuario.
     * Si ya existe, lo ignoramos.
     */
    @Before
    fun setup() = runBlocking {
        val result = authRemoteDataResource.registerUser(user)
        // Si ya está registrado, ignoramos el error
        if (result is Resource.Success) {
            user.id = result.data?.usuario?.id ?: ""
        }
    }
    @Test
    fun `register user successfully`() = runBlocking {
        // Usuario fijo
        val newUser = Usuario(
            email = "testuser@mail.com",
            password = "Test123!",
            username = "TestUserr",
            phoneNumber = "123456789"
        )

        // 1. Intentar borrar al usuario si existe
        val deleteResult = authRemoteDataResource.delete(newUser.email ?:"")
        if (deleteResult is Resource.Success) {
            println("🧹 Usuario anterior eliminado antes del test.")
        }

        // 2. Registrar el usuario limpio
        val registerResult = authRemoteDataResource.registerUser(newUser)

        when (registerResult) {
            is Resource.Success -> {
                println("✅ Usuario registrado correctamente: ${registerResult.data?.usuario}")
            }
            is Resource.Error -> {
                println("❌ Error al registrar usuario: ${registerResult.message}")
            }
            else -> {}
        }

        assertTrue(registerResult is Resource.Success)
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

    @Test
    fun `login user successfully`() = runBlocking {
        val result = authRemoteDataResource.login(LoginRequest(user.email, user.password))

        when (result) {
            is Resource.Success -> {
                val token = result.data?.token
                val usuario = result.data?.usuario
                println("✅ Login exitoso: token=${token}, usuario=${usuario}")
                assertNotNull(token) // Verifica que el token no sea nulo
            }
            is Resource.Error -> {
                println("❌ Error en login: ${result.message}")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }


    @Test
    fun `get all users`() = runBlocking {
        val result = authRemoteDataResource.getAll()

        when (result) {
            is Resource.Success -> result.data?.forEach { println(it.toString()) }
            is Resource.Error -> println("❌ Error al obtener usuarios: ${result.message}")
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `delete user successfully`() = runBlocking {
        val result = authRemoteDataResource.delete(user.email ?: "")

        when (result) {
            is Resource.Success -> println("✅ Usuario eliminado correctamente.")
            is Resource.Error -> {
                println("❌ Error al eliminar usuario (${user.email}): ${result.message}")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }
    @Test
    fun `register negocio successfully and delete`() = runBlocking {
        val usuarioNegocio = Usuario(
            email = "negocio_test@bookme.com",
            password = "Negocio123!",
            username = "NegocioTest",
            phoneNumber = "600000000"
        )

        val result = authRemoteDataResource.registerNegocio(usuarioNegocio)

        when (result) {
            is Resource.Success -> {
                val id = result.data?.usuario?.id
                println("✅ Negocio registrado con ID: $id")

                // Borrar usuario tras éxito
                val deleteResult = authRemoteDataResource.delete(usuarioNegocio.email ?: "")
                assertTrue(deleteResult is Resource.Success)
            }
            is Resource.Error -> {
                println("❌ Error en registro negocio: ${result.message}")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `register cliente successfully`() = runBlocking {
        val usuarioCliente = Usuario(
            email = "cliente_test@bookme.com",
            password = "Cliente123!",
            username = "ClienteTest",
            phoneNumber = "699999999"
        )
        val deleteResponse = authRemoteDataResource.delete(usuarioCliente.email ?: "")
        if(deleteResponse is Resource.Success){
            println("🧹 Cliente ${usuarioCliente.email} limpiado")
        }

        val result = authRemoteDataResource.registerCliente(usuarioCliente)

        when (result) {
            is Resource.Success -> {
                val id = result.data?.usuario?.id
                println("✅ Cliente registrado con ID: $id")


            }
            is Resource.Error -> {
                println("❌ Error en registro cliente: ${result.message}")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }

}
