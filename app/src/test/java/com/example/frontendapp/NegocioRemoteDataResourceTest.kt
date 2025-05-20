package com.example.frontendapp

import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NegocioApiTest {

    private val usuarioNegocio = Usuario(
        email = "negocio_test@bookme.com",
        password = "Negocio123!",
        username = "NegocioTest",
        phoneNumber = "600000000",
        isNegocio = true
    )

    private lateinit var authRemoteDataResource: AuthRemoteDataResource
    private lateinit var negocioRemoteSource: NegocioRemoteSource
    private var createdNegocioId: Int? = null

    @Before
    fun setup() = runBlocking {
        authRemoteDataResource = AuthRemoteDataResource(RetrofitInstance.userApi)

        // Login para obtener token y setearlo en RetrofitInstance (interceptor lo usará)
        val loginResult = authRemoteDataResource.login(LoginRequest(usuarioNegocio.email, usuarioNegocio.password))
        if (loginResult is Resource.Success) {
            val token = loginResult.data?.token
            assertNotNull("Token no puede ser nulo", token)
            RetrofitInstance.setToken(token!!)

            negocioRemoteSource = NegocioRemoteSource(RetrofitInstance.negocioApi)
        } else {
            fail("Login fallido: ${(loginResult as? Resource.Error)?.message}")
        }
    }

    @Test
    fun `crear negocio`() = runBlocking {
        val horario = Horario(
            diaSemana = "Lunes",
            horaInicio = "09:00",
            horaFin = "18:00",
        )

        val negocio = Negocio(
            nombre = "Negocio de prueba",
            descripcion = "Descripción de prueba",
            direccion = "Calle Prueba 123",
            latitud = 10.0,
            categoria = "Prueba",
            longitud = 20.0
        )

        val result = negocioRemoteSource.addNegocio(negocio)

        when (result) {
            is Resource.Success -> {
                createdNegocioId = result.data?.id

                println("Negocio creado con ID: $createdNegocioId")
            }
            is Resource.Error -> {
                println("Error al crear negocio: ${result.message}")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success)
        assertNotNull(createdNegocioId)
    }


    @Test
    fun `obtener negocio por id`() = runBlocking {
        assertNotNull("Debe existir negocio creado", createdNegocioId)

        val result = negocioRemoteSource.getNegocio(createdNegocioId!!)

        when (result) {
            is Resource.Success -> println("Negocio obtenido: ${result.data}")
            is Resource.Error -> println("Error al obtener negocio: ${result.message}")
            else -> {}
        }

        assertTrue(result is Resource.Success)
        assertEquals(createdNegocioId, result.data?.id)
    }

    @Test
    fun `obtener todos los negocios`() = runBlocking {
        val result = negocioRemoteSource.getAllNegocios()

        when (result) {
            is Resource.Success -> {
                println("Lista de negocios:")
                result.data?.forEach { println(it) }
            }
            is Resource.Error -> println("Error al obtener negocios: ${result.message}")
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `actualizar negocio`() = runBlocking {
        assertNotNull("Debe existir negocio creado", createdNegocioId)

        val negocioActualizado = Negocio(
            id = createdNegocioId!!,
            nombre = "Negocio actualizado",
            descripcion = "Descripción actualizada",
            direccion = "Calle Actualizada 456",
            latitud = 11.0,
            longitud = 21.0
        )

        val result = negocioRemoteSource.updateNegocio(createdNegocioId!!, negocioActualizado)

        when (result) {
            is Resource.Success -> println("Negocio actualizado")
            is Resource.Error -> println("Error al actualizar: ${result.message}")
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `eliminar negocio`() = runBlocking {
        assertNotNull("Debe existir negocio creado", createdNegocioId)

        val result = negocioRemoteSource.deleteNegocio(createdNegocioId!!)

        when (result) {
            is Resource.Success -> println("Negocio eliminado")
            is Resource.Error -> println("Error al eliminar: ${result.message}")
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }
}