package com.example.frontendapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frontendapp.data.model.Categoria
import com.example.frontendapp.data.model.Negocio.Negocio
import com.example.frontendapp.data.model.Servicio.Servicio
import com.example.frontendapp.data.model.Servicio.ServicioUpdateRequest
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.data.remote.request.LoginRequest
import com.example.frontendapp.data.remote.source.AuthRepo
import com.example.frontendapp.data.remote.source.NegocioRepo
import com.example.frontendapp.data.remote.source.ServicioRepo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ServicioApiTest {

    private lateinit var servicioRemoteSource: ServicioRepo
    private var createdServicioId: Int? = null
    private var negocioId: Int? = null

    @Before
    fun setup() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val authRemoteDataResource = AuthRepo(RetrofitInstance.userApi)
        val loginResult = authRemoteDataResource.login(LoginRequest("negocio_test@bookme.com", "Negocio123!"))
        if (loginResult is Resource.Success) {
            val token = loginResult.data?.token
            assertNotNull("Token no puede ser nulo", token)
            RetrofitInstance.setToken(token!!)

            servicioRemoteSource = ServicioRepo(RetrofitInstance.servicioApi)
            val negocioRemoteSource = NegocioRepo(RetrofitInstance.negocioApi)

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
                    categoria = Categoria()
                )
                val addNegocioResult = negocioRemoteSource.addNegocio(nuevoNegocio)
                if (addNegocioResult !is Resource.Success) {
                    fail("No se pudo crear negocio para test")
                }
                negocioId = addNegocioResult.data?.id
                if (negocioId == null) fail("No se pudo obtener ID del negocio creado")
            }

            val serviciosExistentes = servicioRemoteSource.getAllServicios()
            if (serviciosExistentes is Resource.Success) {
                serviciosExistentes.data?.find { it.nombre == "Servicio de prueba" }?.let { existente ->
                    servicioRemoteSource.deleteServicio(existente.id!!)
                    println("Servicio existente eliminado: ${existente.id}")
                }
            }

            val servicio = Servicio(
                nombre = "Servicio de prueba",
                descripcion = "Descripcion del servicio de prueba",
                duracionMinutos = 60,
                precio = 100.0,
                negocioId = negocioId!!,
                id = 0,
                imagen = null
            )

            val addServicioResult = servicioRemoteSource.addServicio(servicio, imagenUri = null, context = context)
            if (addServicioResult is Resource.Success) {
                createdServicioId = addServicioResult.data?.id
                if (createdServicioId == null) {
                    val errorMessage = (addServicioResult as? Resource.Error)?.message
                    fail(
                        "No se pudo crear el servicio en setup.\n" +
                                "Mensaje del servidor: $errorMessage"
                    )
                }
                println("Servicio creado con ID: $createdServicioId")
            } else {
                fail("Error al crear servicio en setup: ${(addServicioResult as? Resource.Error)?.message}")
            }

        } else {
            fail("Login fallido: ${(loginResult as? Resource.Error)?.message}")
        }
    }

    @Test
    fun crear_servicio_y_verificar_persistencia() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull("Debe existir negocio para asignar servicio", negocioId)

        val servicio = Servicio(
            nombre = "Servicio nuevo",
            descripcion = "Servicio con descripcion nueva",
            duracionMinutos = 45,
            precio = 150.0,
            negocioId = negocioId!!,
            id = 0,
            imagen = null
        )

        val result = servicioRemoteSource.addServicio(servicio, imagenUri = null, context = context)
        when (result) {
            is Resource.Success -> {
                val servicioId = result.data?.id
                assertNotNull("El ID del servicio no puede ser nulo", servicioId)
                val fetched = servicioRemoteSource.getServicio(servicioId!!)
                assertTrue(fetched is Resource.Success)
            }
            is Resource.Error -> fail("Error al crear servicio: ${result.message}")
            else -> fail("Resultado inesperado")
        }
    }

    @Test
    fun obtener_servicio_por_id() = runBlocking {
        assertNotNull("Debe existir servicio creado", createdServicioId)
        val result = servicioRemoteSource.getServicio(createdServicioId!!)
        assertTrue(result is Resource.Success)
        assertEquals(createdServicioId, result.data?.id)
    }

    @Test
    fun actualizar_servicio() = runBlocking {
        assertNotNull("El ID del servicio creado no debe ser nulo", createdServicioId)

        val id = createdServicioId ?: return@runBlocking // Prevención extra por seguridad

        // Crear el objeto ServicioUpdateRequest (sin id)
        val servicioActualizado = ServicioUpdateRequest(
            nombre = "Servicio actualizado",
            descripcion = "Descripcion actualizada",
            duracionMinutos = 90,
            precio = 200.0,
            negocioId = negocioId!!,
            id = 0
        )

        val updateResult = servicioRemoteSource.updateServicio(id, servicioActualizado)
        println("Resultado de actualizar servicio: $updateResult")

        if (updateResult is Resource.Error) {
            println("Error al actualizar servicio: ${updateResult.message}")
        }

        assertTrue("La actualizacion del servicio fallo: $updateResult", updateResult is Resource.Success)

        val fetchResult = servicioRemoteSource.getServicio(id)
        if (fetchResult is Resource.Success) {
            println("Servicio actualizado: ${fetchResult.data}")
        } else {
            println("Error al obtener servicio actualizado: $fetchResult")
        }
        assertTrue("Fallo al obtener el servicio actualizado: $fetchResult", fetchResult is Resource.Success)

        val servicioRecuperado = (fetchResult as Resource.Success).data
        assertNotNull("El servicio recuperado no debe ser nulo", servicioRecuperado)

        assertEquals("El nombre no coincide", "Servicio actualizado", servicioRecuperado?.nombre)
        assertEquals("La duracion no coincide", 90, servicioRecuperado?.duracionMinutos)
        assertEquals("El precio no coincide", 200.0, servicioRecuperado?.precio ?: -1.0, 0.0)
    }

    @Test
    fun eliminar_servicio() = runBlocking {
        assertNotNull("Debe existir servicio creado", createdServicioId)
        val deleteResult = servicioRemoteSource.deleteServicio(createdServicioId!!)
        assertTrue(deleteResult is Resource.Success)
        val fetchResult = servicioRemoteSource.getServicio(createdServicioId!!)
        assertTrue(fetchResult is Resource.Error)
    }

    @Test
    fun obtener_servicios_por_negocio() = runBlocking {
        assertNotNull("Debe existir negocio para consulta", negocioId)
        val result = servicioRemoteSource.getServiciosByNegocioId(negocioId!!)
        when (result) {
            is Resource.Success -> {
                assertTrue(result.data?.all { it.negocioId == negocioId } ?: false)
            }
            is Resource.Error -> fail("Error al obtener servicios por negocio: ${result.message}")
            else -> fail("Resultado inesperado")
        }
    }
}