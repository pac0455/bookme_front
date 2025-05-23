package com.example.frontendapp

import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.model.Servicio
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.data.remote.source.ServicioRemoteSource

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ServicioApiTest {

    private lateinit var servicioRemoteSource: ServicioRemoteSource
    private var createdServicioId: Int? = null
    private var negocioId: Int? = null

    @Before
    fun setup() = runBlocking {
        val authRemoteDataResource = AuthRemoteDataResource(RetrofitInstance.userApi)
        val loginResult = authRemoteDataResource.login(LoginRequest("negocio_test@bookme.com", "Negocio123!"))
        if (loginResult is Resource.Success) {
            val token = loginResult.data?.token
            assertNotNull("Token no puede ser nulo", token)
            RetrofitInstance.setToken(token!!)

            servicioRemoteSource = ServicioRemoteSource(RetrofitInstance.servicioApi)
            val negocioRemoteSource = NegocioRemoteSource(RetrofitInstance.negocioApi)

            val negociosResult = negocioRemoteSource.getAllNegocios()
            if (negociosResult !is Resource.Success) {
                fail("No se pudo obtener lista de negocios")
            }

            negocioId = negociosResult.data?.firstOrNull()?.id

            if (negocioId == null) {
                val nuevoNegocio = Negocio(
                    nombre = "Negocio para servicio",
                    descripcion = "Negocio temporal para test",
                    direccion = "Calle Test 1",
                    latitud = 0.0,
                    longitud = 0.0,
                    categoria = "Test"
                )
                val addNegocioResult = negocioRemoteSource.addNegocio(nuevoNegocio)
                if (addNegocioResult !is Resource.Success) {
                    fail("No se pudo crear negocio para test")
                }
                negocioId = addNegocioResult.data?.id
                if (negocioId == null) fail("No se pudo obtener ID del negocio creado")
            }

            // Eliminar servicio existente con nombre "Servicio de prueba"
            val serviciosExistentes = servicioRemoteSource.getAllServicios()
            if (serviciosExistentes is Resource.Success) {
                serviciosExistentes.data?.find { it.nombre == "Servicio de prueba" }?.let { existente ->
                    servicioRemoteSource.deleteServicio(existente.id!!)
                    println("Servicio existente eliminado: ${existente.id}")
                }
            }

            // Crear servicio nuevo y guardar su ID
            val servicio = Servicio(
                nombre = "Servicio de prueba",
                descripcion = "Descripción del servicio de prueba",
                duracionMinutos = 60,
                precio = 100.0,
                negocioId = negocioId!!
            )

            val addServicioResult = servicioRemoteSource.addServicio(servicio)
            if (addServicioResult is Resource.Success) {
                createdServicioId = addServicioResult.data?.id
                assertNotNull("No se pudo crear el servicio en setup", createdServicioId)
                println("Servicio creado con ID: $createdServicioId")
            } else {
                fail("Error al crear servicio en setup: ${(addServicioResult as? Resource.Error)?.message}")
            }

        } else {
            fail("Login fallido: ${(loginResult as? Resource.Error)?.message}")
        }
    }

    @Test
    fun `crear servicio y verificar persistencia`() = runBlocking {
        assertNotNull("Debe existir negocio para asignar servicio", negocioId)

        // Eliminar si ya existe servicio con ese nombre
        val serviciosExistentes = servicioRemoteSource.getAllServicios()
        if (serviciosExistentes is Resource.Success) {
            serviciosExistentes.data?.find { it.nombre == "Servicio nuevo" }?.let { existente ->
                servicioRemoteSource.deleteServicio(existente.id!!)
                println("Servicio existente eliminado: ${existente.id}")
            }
        }

        val servicio = Servicio(
            nombre = "Servicio nuevo",
            descripcion = "Servicio con descripción nueva",
            duracionMinutos = 45,
            precio = 150.0,
            negocioId = negocioId!!
        )

        val result = servicioRemoteSource.addServicio(servicio)
        when (result) {
            is Resource.Success -> {
                val servicioId = result.data?.id
                assertNotNull("El ID del servicio no puede ser nulo", servicioId)

                val fetched = servicioRemoteSource.getServicio(servicioId!!)
                assertTrue(fetched is Resource.Success)

                val servicioRecuperado = (fetched as Resource.Success).data
                println("Servicio recuperado: $servicioRecuperado")

                assertEquals("Servicio nuevo", servicioRecuperado?.nombre)
                assertEquals(45, servicioRecuperado?.duracionMinutos)
                assertEquals(negocioId, servicioRecuperado?.negocioId)
            }
            is Resource.Error -> fail("Error al crear servicio: ${result.message}")
            else -> fail("Resultado inesperado")
        }
    }

    @Test
    fun `obtener servicio por id`() = runBlocking {
        assertNotNull("Debe existir servicio creado", createdServicioId)

        val result = servicioRemoteSource.getServicio(createdServicioId!!)

        when (result) {
            is Resource.Success -> println("Servicio obtenido: ${result.data}")
            is Resource.Error -> println("Error al obtener servicio: ${result.message}")
            else -> {}
        }

        assertTrue(result is Resource.Success)
        assertEquals(createdServicioId, result.data?.id)
    }

    @Test
    fun `actualizar servicio`() = runBlocking {
        assertNotNull("Debe existir servicio creado", createdServicioId)

        val servicioActualizado = Servicio(
            id = createdServicioId,
            nombre = "Servicio actualizado",
            descripcion = "Descripción actualizada",
            duracionMinutos = 90,
            precio = 200.0,
            negocioId = negocioId!!
        )

        val updateResult = servicioRemoteSource.updateServicio(createdServicioId!!, servicioActualizado)
        when (updateResult) {
            is Resource.Success -> println("Servicio actualizado correctamente")
            is Resource.Error -> fail("Error al actualizar servicio: ${updateResult.message}")
            else -> fail("Resultado inesperado")
        }

        val fetchResult = servicioRemoteSource.getServicio(createdServicioId!!)
        assertTrue(fetchResult is Resource.Success)
        val servicioRecuperado = (fetchResult as Resource.Success).data
        assertEquals("Servicio actualizado", servicioRecuperado?.nombre)
        assertEquals(90, servicioRecuperado?.duracionMinutos)
        assertNotNull("El precio no debe ser nulo", servicioRecuperado?.precio)
        assertEquals(200.0, servicioRecuperado?.precio!!, 0.0)

    }

    @Test
    fun `eliminar servicio`() = runBlocking {
        assertNotNull("Debe existir servicio creado", createdServicioId)

        val deleteResult = servicioRemoteSource.deleteServicio(createdServicioId!!)
        when (deleteResult) {
            is Resource.Success -> println("Servicio eliminado correctamente")
            is Resource.Error -> fail("Error al eliminar servicio: ${deleteResult.message}")
            else -> fail("Resultado inesperado")
        }

        // Confirmar que ya no existe
        val fetchResult = servicioRemoteSource.getServicio(createdServicioId!!)
        assertTrue(fetchResult is Resource.Error)
    }

    @Test
    fun `obtener servicios por negocio`() = runBlocking {
        assertNotNull("Debe existir negocio para consulta", negocioId)

        val result = servicioRemoteSource.getServiciosByNegocioId(negocioId!!)
        when (result) {
            is Resource.Success -> {
                println("Servicios obtenidos por negocio: ${result.data?.size}")
                assertTrue(result.data?.all { it.negocioId == negocioId } ?: false)
            }
            is Resource.Error -> fail("Error al obtener servicios por negocio: ${result.message}")
            else -> fail("Resultado inesperado")
        }
    }
}
