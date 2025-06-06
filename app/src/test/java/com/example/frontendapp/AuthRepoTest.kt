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
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

class AuthRepoTest {

    private val authRemoteDataResource = AuthRepo(RetrofitInstance.userApi)
    private val uniqueSuffix = (System.currentTimeMillis() % 1000000000).toString().padStart(9, '0')

    private val user = Usuario(
        email = "franhidalc@gmail.com",
        password = "12Aaa!a",
        username = "Nombre",
        phoneNumber = uniqueSuffix
    )

    /**
     * Antes de cada test, intentamos registrar el usuario.
     * Si ya existe, lo ignoramos.
     */
    @Before
    fun setup() = runBlocking {
        // Intentamos eliminar el usuario antes de registrarlo
        val deleteResult = authRemoteDataResource.delete(user.email ?: "")
        if (deleteResult is Resource.Success) {
            println("🧹 Usuario eliminado antes del registro.")
        } else {
            println("⚠️ No se pudo eliminar usuario previo (puede que no existiera): ${deleteResult.message}")
        }

        val result = authRemoteDataResource.registerUser(user)
        if (result is Resource.Success) {
            user.id = result.data?.usuario?.id ?: ""
        } else {
            println("❌ Error al registrar usuario en setup: ${result.message}")
        }
    }


    @Test
    fun `validate registration with invalid data`() = runBlocking {
        val invalidUser = Usuario(
            email = "", // Email vacío
            password = "short", // Contraseña demasiado corta
            username = "", // Nombre vacío
            phoneNumber = "123" // Número de teléfono inválido
        )

        val registerDTO = invalidUser.toRegisterDTO()
        val result = authRemoteDataResource.validateRegistration(registerDTO)

        when (result) {
            is Resource.Success -> {
                println("✅ Validación exitosa como se esperaba: ${result.data}")
                // Verifica que success sea false
                assertFalse(result.data?.success!!) // Asegúrate de que success sea false

                // Verifica que haya errores en el array
                val errors = result.data?.errors!!
                assertNotNull(errors) // Asegúrate de que no sea nulo
                assertTrue(errors.isNotEmpty()) // Asegúrate de que haya errores
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
            username = "usuarioUnicoParaTest${System.currentTimeMillis()}",
        )

        val result = authRemoteDataResource.registerUser(usuario)
        when(result){
            is Resource.Success -> {
                val deleteResult = authRemoteDataResource.delete(usuario.email ?: "")
                assertTrue(deleteResult is Resource.Success)
            }
            is Resource.Error -> {
                print(result.message)
            }
            else -> {}

        }

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
        val gson = Gson()

        val usuarioNegocio = Usuario(
            email = "negocio_test@bookme.com",
            password = "Negocio123!",
            username = "NegocioTest",
            phoneNumber = "600000000",
            isNegocio = true
        )

        // 1. Intentar borrar al usuario si existe
        val deleteResult = authRemoteDataResource.delete(usuarioNegocio.email ?: "")
        if (deleteResult is Resource.Success) {
            println("🧹 Usuario anterior eliminado antes del test.")
        } else if (deleteResult is Resource.Error) {
            println("⚠️ No se pudo eliminar usuario anterior (puede no existir): ${deleteResult.message}")
        }

        // 2. Registrar el nuevo negocio
        val result = authRemoteDataResource.registerNegocio(usuarioNegocio)

        // 3. Imprimir el resultado completo como JSON
        println("📦 Respuesta completa: ${gson.toJson(result)}")

        when (result) {
            is Resource.Success -> {
                val usuarioRegistrado = result.data?.usuario
                println("✅ Usuario registrado: ${gson.toJson(usuarioRegistrado)}")

                // Validaciones
                assertNotNull(usuarioRegistrado?.id, "El ID no debe ser null")
                assertTrue("El ID no debe estar vacío", usuarioRegistrado?.id!!.isNotBlank())

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

        // 1. Borrar si ya existe
        val deleteResponse = authRemoteDataResource.delete(usuarioCliente.email ?: "")
        if (deleteResponse is Resource.Success) {
            println("🧹 Cliente ${usuarioCliente.email} limpiado")
        } else if (deleteResponse is Resource.Error) {
            println("⚠️ No se pudo eliminar cliente anterior (puede no existir): ${deleteResponse.message}")
        }

        // 2. Registrar nuevo cliente
        val result = authRemoteDataResource.registerCliente(usuarioCliente)

        // 3. Mostrar respuesta completa
        println("📦 Respuesta completa: ${gson.toJson(result)}")

        when (result) {
            is Resource.Success -> {
                val usuarioRegistrado = result.data?.usuario
                println("✅ Cliente registrado: ${gson.toJson(usuarioRegistrado)}")

                // Validaciones
                assertNotNull(usuarioRegistrado?.id, "El ID no debe ser null")
                assertTrue("El ID no debe estar vacío", usuarioRegistrado?.id!!.isNotBlank())

                assertEquals(usuarioCliente.email, usuarioRegistrado.email)
            }
            is Resource.Error -> {
                println("❌ Error en registro cliente: ${result.message}")

                // Mostrar errores de validación si existen
                result.validationResponse?.errors?.forEach { (campo, mensaje) ->
                    println("🛑 Error [$campo]: $mensaje")
                }

                fail("Fallo en registro: ${result.message}")
            }
            else -> fail("Resultado inesperado del registro")
        }
    }

}
