package com.example.frontendapp

import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.data.remote.source.NegocioRepo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests para la API de negocios.
 *
 * Esta clase contiene pruebas que validan las operaciones CRUD y de consulta
 * sobre el recurso "Negocio" a través del repositorio remoto `NegocioRepo`.
 * Además, realiza el login previo para obtener un token de autenticación.
 */
class NegocioApiTest {

    /**
     * Usuario de prueba con permisos para manejar negocios.
     */
    private val usuarioNegocio = Usuario(
        email = "negocio_test@bookme.com",
        password = "Negocio123!",
        username = "NegocioTest",
        phoneNumber = "600000000",
        isNegocio = true
    )

    private lateinit var authRemoteDataResource: AuthRepo
    private lateinit var negocioRemoteSource: NegocioRepo

    /**
     * ID del negocio creado durante el setup o las pruebas.
     */
    private var createdNegocioId: Int? = null

    /**
     * Setup que se ejecuta antes de cada test.
     *
     * Realiza login para obtener token y configura el repositorio de negocios.
     * Además, elimina un negocio con nombre "Negocio de prueba" si existe,
     * y crea uno nuevo para usarlo en las pruebas.
     */
    @Before
    fun setup() = runBlocking {
        authRemoteDataResource = AuthRepo(RetrofitInstance.userApi)

        val loginResult = authRemoteDataResource.login(LoginRequest(usuarioNegocio.email, usuarioNegocio.password))
        if (loginResult is Resource.Success) {
            val token = loginResult.data?.token
            assertNotNull("Token no puede ser nulo", token)
            RetrofitInstance.setToken(token!!)

            negocioRemoteSource = NegocioRepo(RetrofitInstance.negocioApi)

            // Eliminar negocio si ya existe
            val negociosExistentes = negocioRemoteSource.getAllNegocios()
            if (negociosExistentes is Resource.Success) {
                negociosExistentes.data?.find { it.nombre == "Negocio de prueba" }?.let { existente ->
                    negocioRemoteSource.deleteNegocio(existente.id)
                    println("Negocio existente eliminado: ${existente.id}")
                }
            }

            // Crear negocio nuevo para las pruebas
            val negocio = Negocio(
                nombre = "Negocio de prueba",
                descripcion = "Descripción de prueba",
                direccion = "Calle Prueba 123",
                latitud = 10.0,
                longitud = 20.0,
                categoriaId = 1,
                categoria = null
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

    /**
     * Test para crear un negocio con horarios de atención y verificar
     * que estos se persisten correctamente.
     *
     * - Elimina cualquier negocio con el nombre "Negocio con horarios" antes de crear.
     * - Crea un negocio con dos horarios específicos.
     * - Verifica que al obtener el negocio los horarios se mantienen.
     */
    @Test
    fun `crear negocio con horarios y verificar persistencia`() = runBlocking {
        val negociosExistentes = negocioRemoteSource.getAllNegocios()
        if (negociosExistentes is Resource.Success) {
            negociosExistentes.data?.find { it.nombre == "Negocio con horarios" }?.let { existente ->
                val deleteResult = negocioRemoteSource.deleteNegocio(existente.id)
                println("Negocio existente eliminado: ${existente.id} -> $deleteResult")
            }
        }

        val horarios = listOf(
            Horario(diaSemana = "Lunes", horaInicio = "08:00", horaFin = "12:00"),
            Horario(diaSemana = "Martes", horaInicio = "09:00", horaFin = "13:00")
        )

        val negocio = Negocio(
            nombre = "Negocio prueba",
            descripcion = "Descripción",
            direccion = "Dirección",
            latitud = 10.0,
            longitud = 20.0,
            categoriaId = 1,
            categoria = null,
            activo = true,
            horarioAtencion = horarios,
        )

        when (val result = negocioRemoteSource.addNegocio(negocio)) {
            is Resource.Success -> {
                val negocioId = result.data?.id
                assertNotNull("El ID del negocio no puede ser nulo", negocioId)

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

    /**
     * Test para crear un negocio básico y verificar que se crea correctamente.
     *
     * Elimina cualquier negocio con el mismo nombre antes de crear.
     */
    @Test
    fun `crear negocio`() = runBlocking {
        val negociosExistentes = negocioRemoteSource.getAllNegocios()
        if (negociosExistentes is Resource.Success) {
            negociosExistentes.data?.find { it.nombre == "Negocio de prueba" }?.let { existente ->
                negocioRemoteSource.deleteNegocio(existente.id)
                println("Negocio existente eliminado: ${existente.id}")
            }
        }

        val negocio = Negocio(
            nombre = "Negocio de prueba",
            descripcion = "Descripción de prueba",
            direccion = "Calle Prueba 123",
            latitud = 10.0,
            longitud = 20.0,
            categoriaId = 1,
            categoria = null
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

    /**
     * Test para obtener un negocio por su ID y verificar su existencia.
     */
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

    /**
     * Test para crear un negocio con estado inactivo y verificar
     * que el estado se guarda correctamente.
     */
    @Test
    fun `crear negocio con estado inactivo`() = runBlocking {
        val negociosExistentes = negocioRemoteSource.getAllNegocios()
        if (negociosExistentes is Resource.Success) {
            negociosExistentes.data?.find { it.nombre == "Negocio inactivo" }?.let { existente ->
                negocioRemoteSource.deleteNegocio(existente.id!!)
                println("Negocio existente eliminado: ${existente.id}")
            }
        }

        val negocioInactivo = Negocio(
            nombre = "Negocio inactivo",
            descripcion = "Negocio creado con estado inactivo",
            direccion = "Calle Desactivada 123",
            latitud = 10.0,
            longitud = 20.0,
            categoriaId = 1,
            categoria = null,
            activo = false,
        )

        val result = negocioRemoteSource.addNegocio(negocioInactivo)

        when (result) {
            is Resource.Success -> {
                val negocioId = result.data?.id
                assertNotNull("El ID del negocio no puede ser nulo", negocioId)

                val fetched = negocioRemoteSource.getNegocio(negocioId!!)
                assertTrue(fetched is Resource.Success)

                val negocioRecuperado = (fetched as Resource.Success).data
                println("Negocio recuperado: $negocioRecuperado")

                assertEquals(false, negocioRecuperado?.activo)
            }
            is Resource.Error -> {
                fail("Error al crear negocio con estado inactivo: ${result.message}")
            }
            else -> {}
        }
    }

    /**
     * Test para obtener la lista completa de negocios.
     */
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

    /**
     * Test para actualizar un negocio existente usando su nombre.
     */
    @Test
    fun `actualizar negocio`() = runBlocking {
        assertNotNull("Debe existir negocio creado", createdNegocioId)

        val negocioActualizado = Negocio(
            id = createdNegocioId!!,
            nombre = "Negocio de prueba",
            descripcion = "Descripción actualizada",
            direccion = "Calle Actualizada 456",
            categoriaId = 1,
            categoria = null,
            latitud = 11.0,
            longitud = 21.0
        )

        val result = negocioRemoteSource.updateNegocioByNombre(negocioActualizado)

        when (result) {
            is Resource.Success -> println("Negocio actualizado correctamente por nombre.")
            is Resource.Error -> println("Error al actualizar: ${result.message}")
            else -> {}
        }

        assertTrue(result is Resource.Success)
    }

    /**
     * Test para obtener las reservas asociadas a un negocio.
     */
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

    /**
     * Test para eliminar un negocio existente.
     */
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

    /**
     * Test para obtener los negocios asociados al usuario autenticado.
     */
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
