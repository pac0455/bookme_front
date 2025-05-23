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

        val loginResult = authRemoteDataResource.login(LoginRequest(usuarioNegocio.email, usuarioNegocio.password))
        if (loginResult is Resource.Success) {
            val token = loginResult.data?.token
            assertNotNull("Token no puede ser nulo", token)
            RetrofitInstance.setToken(token!!)

            negocioRemoteSource = NegocioRemoteSource(RetrofitInstance.negocioApi)

            // Eliminar negocio si ya existe
            val negociosExistentes = negocioRemoteSource.getAllNegocios()
            if (negociosExistentes is Resource.Success) {
                negociosExistentes.data?.find { it.nombre == "Negocio de prueba" }?.let { existente ->
                    negocioRemoteSource.deleteNegocio(existente.id!!)
                    println("Negocio existente eliminado: ${existente.id}")
                }
            }

            // Crear negocio nuevo
            val negocio = Negocio(
                nombre = "Negocio de prueba",
                descripcion = "Descripción de prueba",
                direccion = "Calle Prueba 123",
                latitud = 10.0,
                longitud = 20.0,
                categoria = "Prueba"
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
    fun `crear negocio con horarios y verificar persistencia`() = runBlocking {
        // Autenticación previa si es necesario (ya lo tienes en setup)

        // 1. Eliminar negocio existente con el mismo nombre
        val negociosExistentes = negocioRemoteSource.getAllNegocios()
        if (negociosExistentes is Resource.Success) {
            negociosExistentes.data?.find { it.nombre == "Negocio con horarios" }?.let { existente ->
                val deleteResult = negocioRemoteSource.deleteNegocio(existente.id!!)
                println("Negocio existente eliminado: ${existente.id} -> $deleteResult")
            }
        }

        // 2. Preparar horarios
        val horarios = listOf(
            Horario(diaSemana = "Lunes", horaInicio = "08:00", horaFin = "12:00"),
            Horario(diaSemana = "Martes", horaInicio = "09:00", horaFin = "13:00")
        )

        // 3. Crear negocio con horarios
        val negocio = Negocio(
            nombre = "Negocio con horarios",
            descripcion = "Negocio con horarios incluidos",
            direccion = "Av. de los Horarios 1",
            latitud = 40.0,
            longitud = -3.0,
            categoria = "Oficina",
            horarioAtencion = horarios
        )

        val result = negocioRemoteSource.addNegocio(negocio)

        when (result) {
            is Resource.Success -> {
                val negocioId = result.data?.id
                assertNotNull("El ID del negocio no puede ser nulo", negocioId)

                // Obtener negocio desde la API
                val fetched = negocioRemoteSource.getNegocio(negocioId!!)
                assertTrue(fetched is Resource.Success)

                val negocioRecuperado = (fetched as Resource.Success).data
                println("Negocio recuperado: $negocioRecuperado")

                assertEquals(2, negocioRecuperado?.horarioAtencion?.size)
                assertTrue(negocioRecuperado!!.horarioAtencion.any { it.diaSemana == "Lunes" })
                assertTrue(negocioRecuperado.horarioAtencion.any { it.diaSemana == "Martes" })
            }

            is Resource.Error -> {
                fail("Error al crear negocio con horarios: ${result.message}")
            }

            else -> {}
        }
    }


    @Test
    fun `crear negocio`() = runBlocking {
        // Eliminar si ya existe
        val negociosExistentes = negocioRemoteSource.getAllNegocios()
        if (negociosExistentes is Resource.Success) {
            negociosExistentes.data?.find { it.nombre == "Negocio de prueba" }?.let { existente ->
                negocioRemoteSource.deleteNegocio(existente.id!!)
                println("Negocio existente eliminado: ${existente.id}")
            }
        }

        // Crear el nuevo negocio
        val negocio = Negocio(
            nombre = "Negocio de prueba",
            descripcion = "Descripción de prueba",
            direccion = "Calle Prueba 123",
            latitud = 10.0,
            longitud = 20.0,
            categoria = "Prueba"
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
    fun `crear negocio con estado inactivo`() = runBlocking {
        // Eliminar si ya existe negocio con el mismo nombre
        val negociosExistentes = negocioRemoteSource.getAllNegocios()
        if (negociosExistentes is Resource.Success) {
            negociosExistentes.data?.find { it.nombre == "Negocio inactivo" }?.let { existente ->
                negocioRemoteSource.deleteNegocio(existente.id!!)
                println("Negocio existente eliminado: ${existente.id}")
            }
        }

        // Crear negocio con activo = false
        val negocioInactivo = Negocio(
            nombre = "Negocio inactivo",
            descripcion = "Negocio creado con estado inactivo",
            direccion = "Calle Desactivada 123",
            latitud = 10.0,
            longitud = 20.0,
            categoria = "Prueba",
            activo = false
        )

        val result = negocioRemoteSource.addNegocio(negocioInactivo)

        when (result) {
            is Resource.Success -> {
                val negocioId = result.data?.id
                assertNotNull("El ID del negocio no puede ser nulo", negocioId)

                // Obtener negocio desde la API
                val fetched = negocioRemoteSource.getNegocio(negocioId!!)
                assertTrue(fetched is Resource.Success)

                val negocioRecuperado = (fetched as Resource.Success).data
                println("Negocio recuperado: $negocioRecuperado")

                // Comprobar que el estado es inactivo
                assertEquals(false, negocioRecuperado?.activo)
            }
            is Resource.Error -> {
                fail("Error al crear negocio con estado inactivo: ${result.message}")
            }
            else -> {}
        }
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
            id = createdNegocioId!!, // aún se usa internamente
            nombre = "Negocio de prueba", // usa el nombre del setup
            descripcion = "Descripción actualizada",
            direccion = "Calle Actualizada 456",
            categoria = "Hola",
            latitud = 11.0,
            longitud = 21.0
        )

        // USO DEL MÉTODO NUEVO
        val result = negocioRemoteSource.updateNegocioByNombre(negocioActualizado)

        when (result) {
            is Resource.Success -> println("Negocio actualizado correctamente por nombre.")
            is Resource.Error -> println("Error al actualizar: ${result.message}")
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }
    @Test
    fun `obtener reservas de negocio`() = runBlocking {
        assertNotNull("Debe existir negocio creado", createdNegocioId)

        val result = negocioRemoteSource.getReservasByNegocioId(createdNegocioId!!)

        when (result) {
            is Resource.Success -> {
                println("Reservas del negocio:")
                result.data?.forEach { println(it) }
            }
            is Resource.Error -> {
                fail("Error al obtener reservas: ${result.message}")
            }
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
    @Test
    fun `obtener reservas detalladas de negocio`() = runBlocking {
        assertNotNull("Debe existir negocio creado", createdNegocioId)

        val result = negocioRemoteSource.getReservasDetalladasByNegocioId(createdNegocioId!!)

        when (result) {
            is Resource.Success -> {
                println("Reservas detalladas del negocio:")
                result.data?.forEach { println(it) }
            }
            is Resource.Error -> {
                fail("Error al obtener reservas detalladas: ${result.message}")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `obtener negocios por usuario`() = runBlocking {
        val result = negocioRemoteSource.getNegociosByUserId()

        when (result) {
            is Resource.Success -> {
                println("Negocios del usuario:")
                result.data?.forEach { println(it) }
            }
            is Resource.Error -> {
                println("Error al obtener negocios por usuario: ${result.message}")
            }
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }

}