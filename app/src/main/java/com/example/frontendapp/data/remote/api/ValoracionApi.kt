package com.example.frontendapp.data.remote.api
import com.example.frontendapp.data.model.valoracion.ValoracionCreateDTO
import com.example.frontendapp.data.model.valoracion.ValoracionResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val controller = "api/valoraciones"

interface ValoracionApi {

    // GET: Obtener valoraciones de un negocio
    @GET("$controller/negocio/{negocioId}")
    suspend fun getValoracionesPorNegocio(
        @Path("negocioId") negocioId: Int
    ): Response<List<ValoracionResponseDTO>>

    // POST: Crear una nueva valoración
    @POST(controller)
    suspend fun crearValoracion(
        @Body dto: ValoracionCreateDTO
    ): Response<ValoracionResponseDTO>
}
