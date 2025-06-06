package com.example.frontendapp

import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.data.remote.source.HorarioRepo
import com.example.frontendapp.data.remote.source.NegocioRepo
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HorarioApiTest {

    private val usuarioNegocio = Usuario(
        email = "negocio_test@bookme.com",
        password = "Negocio123!",
        username = "ClienteTest",
        phoneNumber = "600000000",
        isNegocio = true
    )

    private lateinit var authRemoteDataResource: AuthRepo
    private lateinit var negocioRemoteSource: NegocioRepo
    private lateinit var horarioRemoteDataSource: HorarioRepo
    private var createdNegocioId: Int? = null

    @Before
    fun setup() = runBlocking {
        authRemoteDataResource = AuthRepo(RetrofitInstance.userApi)

        val loginResult = authRemoteDataResource.login(LoginRequest(usuarioNegocio.email, usuarioNegocio.password))
        if (loginResult is Resource.Success) {
            val token = loginResult.data?.token
            assertNotNull("Token no puede ser nulo", token)
            RetrofitInstance.setToken(token!!)

            negocioRemoteSource = NegocioRepo(RetrofitInstance.negocioApi)
            horarioRemoteDataSource = HorarioRepo(RetrofitInstance.horarioApi)

            // Eliminar si ya existe un negocio con ese nombre
            val negociosExistentes = negocioRemoteSource.getAllNegocios()
            if (negociosExistentes is Resource.Success) {
                negociosExistentes.data?.find { it.nombre == "Negocio Horarios Test" }?.let {
                    negocioRemoteSource.deleteNegocio(it.id!!)
                    println("Negocio existente eliminado: ${it.id}")
                }
            }

            // Crear negocio con horarios
            val horarios = listOf(
                Horario(diaSemana = "Lunes", horaInicio = "08:00", horaFin = "12:00"),
                Horario(diaSemana = "Martes", horaInicio = "10:00", horaFin = "14:00")
            )

            val negocio = Negocio(
                nombre = "Negocio Horarios Test",
                descripcion = "Negocio con horarios para test",
                direccion = "Calle Test 456",
                latitud = 10.0,
                longitud = 20.0,
                categoriaId = 1,
                categoria = Categoria(),
                activo = true,
                horarioAtencion = horarios
            )

            val result = negocioRemoteSource.addNegocio(negocio)
            if (result is Resource.Success) {
                createdNegocioId = result.data?.id
                assertNotNull("No se pudo crear el negocio en setup", createdNegocioId)
                println("Negocio creado con ID: $createdNegocioId")
            } else {
                fail("Error al crear negocio: ${(result as? Resource.Error)?.message}")
            }
        } else {
            fail("Login fallido: ${(loginResult as? Resource.Error)?.message}")
        }
    }

    @Test
    fun `obtener horarios del negocio creado`() = runBlocking {
        assertNotNull("Debe existir negocio creado", createdNegocioId)

        val result = horarioRemoteDataSource.getHorariosByNegocioId(createdNegocioId!!)

        when (result) {
            is Resource.Success -> {
                println("✅ Horarios obtenidos del negocio $createdNegocioId:")
                result.data?.forEach { println(it) }
                assertEquals(2, result.data?.size)
            }
            is Resource.Error -> {
                fail("❌ Error al obtener horarios: ${result.message}")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }


    @After
    fun tearDown() = runBlocking {
        if (createdNegocioId != null) {
            val result = negocioRemoteSource.deleteNegocio(createdNegocioId!!)
            when (result) {
                is Resource.Success -> println("✅ Negocio eliminado correctamente en @After.")
                is Resource.Error -> println("❌ Error al eliminar negocio en @After: ${result.message}")
                else -> {}
            }
            createdNegocioId = null
        }
    }

}
