package com.example.frontendapp.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.frontendapp.data.model.Usuario
import com.example.frontendapp.data.remote.source.AuthRemoteDataSource
import com.example.frontendapp.data.remote.source.RetrofitInstance
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.sql.DriverManager

object GoogleAuthUiClient {

    // Reemplaza con tu CLIENT_ID de la consola de Google (el de tipo WEB)
    private const val WEB_CLIENT_ID = "753444284319-bodkccae65g8b8aevrri41mhe72mnhjh.apps.googleusercontent.com"

    // Puedes generar un nonce dinámico si quieres más seguridad (opcional)
    private const val NONCE = "nonce-aleatorio"

    private fun buildRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // false si quieres que muestre todas las cuentas
            .setServerClientId(WEB_CLIENT_ID)

            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun launchSignIn(
        context: Context,
        onSuccess: (idToken: String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val credentialManager = CredentialManager.create(context)
        val request = buildRequest()

        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            if (credential !is GoogleIdTokenCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                onError(Exception("Credential is not GoogleIdTokenCredential"))
                return
            }
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleTokenId = googleIdTokenCredential.idToken
            val authCredential = GoogleAuthProvider.getCredential(googleTokenId, null)

            val firebaseUser= Firebase.auth.signInWithCredential(authCredential).await().user
            firebaseUser?.let {
                // Aquí puedes manejar el usuario autenticado
                // Por ejemplo, guardar el UID o la información del usuario en tu base de datos
                if(it.isAnonymous.not()){
                    val usuario = Usuario(
                        id = "", // lo puedes dejar vacío si lo genera tu backend
                        nombre = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        firebaseUid = firebaseUser.uid ?: "",
                        telefono = firebaseUser.phoneNumber ?: "",
                        contrasenaHash = "" // puedes dejarlo vacío si solo usas Google login
                    )
                    val repository = AuthRemoteDataSource(RetrofitInstance.api)
                    repository.login(usuario)

                }
            }
            onSuccess(googleIdTokenCredential.idToken)

        } catch (e: GetCredentialException) {
            onError(e)
        }
    }
}
