package com.example.frontendapp.data.remote.api

import com.example.frontendapp.data.model.Api.ApiResponse
import com.example.frontendapp.data.model.Api.ValidationErrorResponse
import com.example.frontendapp.data.model.UI.UpdatePasswordDTO
import com.example.frontendapp.data.model.Usuario.LoginRegisterResultDTO
import com.example.frontendapp.data.model.Usuario.RegisterDTO
import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.data.model.Usuario.UpdateNombreDTO
import com.example.frontendapp.data.remote.reponses.SingleMessageResponse
import com.example.frontendapp.data.remote.request.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


private const val controller = "api/usuario"
interface UserApi {
    // Login tradicional con email/contraseña
    @POST("$controller/login")
    suspend fun login(@Body usuario: LoginRequest): Response<LoginRegisterResultDTO>

    // Registro tradicional
    @POST("$controller/register")
    suspend fun signup(@Body usuario: RegisterDTO): Response<ApiResponse<LoginRegisterResultDTO>>
    //GetAll
    @GET(controller)
    suspend fun getAll(): Response <List<Usuario>>
    @DELETE(controller)
    suspend fun delete(@Query("email") email: String): Response<SingleMessageResponse>
    @POST("$controller/validar-registro")
    suspend fun validateRegistration(@Body registerDTO: RegisterDTO): Response<ValidationErrorResponse>

    @PUT("$controller/update-nombre")
    suspend fun updateNombre(@Body dto: UpdateNombreDTO): Response<UpdateNombreDTO>

    @PUT("$controller/update-password")
    suspend fun updatePassword(@Body dto: UpdatePasswordDTO): Response<UpdatePasswordDTO>
    @PUT("$controller/{id}/bloquear")
    suspend fun bloquearUsuario(@Path("id") id: String): Response<SingleMessageResponse>

    @PUT("$controller/{id}/desbloquear")
    suspend fun desbloquearUsuario(@Path("id") id: String): Response<SingleMessageResponse>

    @DELETE(controller)
    suspend fun deleteUser(@Query("id") id: String): Response<SingleMessageResponse>



}