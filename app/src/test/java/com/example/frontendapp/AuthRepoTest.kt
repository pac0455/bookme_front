package com.example.frontendapp

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.data.model.Usuario.toRegisterDTO
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.reponses.SingleMessageResponse
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

class AuthRepoTest {

    private val authRemoteDataResource = AuthRepo(RetrofitInstance.userApi)
    private val uniqueSuffix = (System.currentTimeMillis() % 1000000000).toString().padStart(9, '0')

    private val userRegister = Usuario(
        email = "register_${uniqueSuffix}@mail.com",
        password = "12Aaa!a",
        username = "NombreRegister",
        phoneNumber = uniqueSuffix,
        isAutentificado = true
    )

    private val userLogin = Usuario(
        email = "admin@example.com",
        password = "AdminPass123!",
        username = "Admin",
        phoneNumber = (uniqueSuffix.toLong() + 1).toString(),
        isAutentificado = true
    )

    /**
     * Antes de cada test:
     * 1. Hacemos login con userLogin para obtener token y setearlo en RetrofitInstance.
     * 2. Eliminamos usuarios de prueba si existen para evitar conflictos.
     * 3. Registramos usuarios nuevos para pruebas.
     */
    @Before
    fun setup() = runBlocking {
        // Hacer login para obtener token válido
        val loginResult = authRemoteDataResource.login(LoginRequest(userLogin.email, userLogin.password))
        if (loginResult is Resource.Success) {
            loginResult.data?.token?.let { token ->
                RetrofitInstance.setToken(token)
                println("✅ Token seteado para tests")
            }
        } else {
            println("❌ No se pudo hacer login para obtener token: ${loginResult}")
            fail("No se pudo autenticar antes de los tests")
        }

        // NO eliminar admin
        if (userLogin.email != "admin@example.com") {
            authRemoteDataResource.delete(userLogin.email ?: "")
        }

        // Registrar usuario login si no existe
        val loginReg = authRemoteDataResource.registerUser(userLogin)
        if (loginReg is Resource.Success) {
            userLogin.id = loginReg.data?.usuario?.id ?: ""
        }

        // Eliminar usuario register si existe
        if (userRegister.email != "admin@example.com") {
            authRemoteDataResource.delete(userRegister.email ?: "")
        }

        // Registrar usuario register
        val registerReg = authRemoteDataResource.registerUser(userRegister)
        if (registerReg is Resource.Success) {
            userRegister.id = registerReg.data?.usuario?.id ?: ""
        }
    }

    @Test
    fun `validate registration with invalid data`() = runBlocking {
        val invalidUser = Usuario(
            email = "",
            password = "short",
            username = "",
            phoneNumber = "123"
        )

        val registerDTO = invalidUser.toRegisterDTO()
        val result = authRemoteDataResource.validateRegistration(registerDTO)

        when (result) {
            is Resource.Success -> {
                println("✅ Validación exitosa como se esperaba: ${result.data}")
                assertFalse(result.data?.success!!)
                val errors = result.data?.errors!!
                assertNotNull(errors)
                assertTrue(errors.isNotEmpty())
            }
            is Resource.Error -> {
                println("⚠️ Validación fallida cuando no debería: ${result.message}")
                fail("Se esperaba un éxito con errores de validación, pero se obtuvo un error.")
            }
            else -> {
                println("⚠️ Resultado inesperado.")
                fail("Resultado inesperado.")
            }
        }
    }

    @Test
    fun `register user successfully`() = runBlocking {
        val newUser = Usuario(
            email = "testuser@mail.com",
            password = "Test123!",
            username = "TestUserr",
            phoneNumber = "123456789"
        )

        authRemoteDataResource.delete(newUser.email ?: "")

        val registerResult = authRemoteDataResource.registerUser(newUser)

        when (registerResult) {
            is Resource.Success -> println("✅ Usuario registrado correctamente: ${registerResult.data?.usuario}")
            is Resource.Error -> println("❌ Error al registrar usuario: ${registerResult.message}")
            else -> {}
        }

        assertTrue(registerResult is Resource.Success)
    }

    @Test
    fun `register user with empty email returns error`() = runBlocking {
        val usuario = Usuario(
            email = "",
            password = "Test123!",
            username = "usuarioUnicoParaTest${System.currentTimeMillis()}",
            phoneNumber = "123456789"
        )

        val result = authRemoteDataResource.registerUser(usuario)

        when(result){
            is Resource.Success -> {
                val deleteResult = authRemoteDataResource.delete(usuario.email ?: "")
                assertTrue(deleteResult is Resource.Success)
            }
            is Resource.Error -> {
                println(result.message)
            }
            else -> {}
        }

        assertTrue(result is Resource.Error)
        assertEquals("El correo electrónico no puede estar vacío.", (result as Resource.Error).message)
    }

    @Test
    fun `login user successfully`() = runBlocking {
        val result = authRemoteDataResource.login(LoginRequest(userLogin.email, userLogin.password))

        when (result) {
            is Resource.Success -> {
                val token = result.data?.token
                val usuario = result.data?.usuario
                println("✅ Login exitoso: token=$token, usuario=$usuario")
                assertNotNull(token)
            }
            is Resource.Error -> println("❌ Error en login: ${result.message}")
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
    fun `register negocio successfully and delete`() = runBlocking {
        val gson = Gson()

        val usuarioNegocio = Usuario(
            email = "negocio_test@bookme.com",
            password = "Negocio123!",
            username = "NegocioTest",
            phoneNumber = "600000000",
            isNegocio = true
        )

        authRemoteDataResource.delete(usuarioNegocio.email ?: "")

        val result = authRemoteDataResource.registerNegocio(usuarioNegocio)

        println("📦 Respuesta completa: ${gson.toJson(result)}")

        when (result) {
            is Resource.Success -> {
                val usuarioRegistrado = result.data?.usuario
                println("✅ Usuario registrado: ${gson.toJson(usuarioRegistrado)}")

                assertNotNull(usuarioRegistrado?.id)
                assertTrue(usuarioRegistrado?.id!!.isNotBlank())
                assertEquals(usuarioNegocio.email, usuarioRegistrado.email)
            }
            is Resource.Error -> {
                println("❌ Error en registro negocio: ${result.message}")
                fail("Fallo en registro: ${result.message}")
            }
            else -> fail("Resultado inesperado del registro")
        }
    }

    @Test
    fun `register cliente successfully`() = runBlocking {
        val gson = Gson()

        val usuarioCliente = Usuario(
            email = "cliente_test@bookme.com",
            password = "Cliente123!",
            username = "Cliente",
            phoneNumber = "722643458",
            isNegocio = false
        )



        // Eliminar usuario si existe
        val deleteResult = authRemoteDataResource.delete(usuarioCliente.email ?: "")
        if (deleteResult is Resource.Success) {
            println("🧹 Cliente ${usuarioCliente.email} eliminado antes del test")
        } else {
            println("⚠️ No se pudo eliminar cliente anterior: ${deleteResult.message}")
        }

        val result = authRemoteDataResource.registerCliente(usuarioCliente)

        println("📦 Respuesta completa: ${gson.toJson(result)}")

        when (result) {
            is Resource.Success -> {
                val usuarioRegistrado = result.data?.usuario
                println("✅ Cliente registrado: ${gson.toJson(usuarioRegistrado)}")

                assertNotNull(usuarioRegistrado?.id)
                assertTrue(usuarioRegistrado?.id!!.isNotBlank())
                assertEquals(usuarioCliente.email, usuarioRegistrado.email)
            }
            is Resource.Error -> {
                println("❌ Error en registro cliente: ${result.message}")
                result.validationResponse?.errors?.forEach { (campo, mensaje) ->
                    println("🛑 Error [$campo]: $mensaje")
                }
                fail("Fallo en registro: ${result.message}")
            }
            else -> fail("Resultado inesperado del registro")
        }
    }

}
