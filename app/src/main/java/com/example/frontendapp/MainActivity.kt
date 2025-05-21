package com.example.frontendapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.navigation.Navigator
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.viewmodels.BussinesMainViewModel
import com.example.frontendapp.ui.theme.viewmodels.LoginViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel


//https://www.youtube.com/watch?v=IX1GkpV71pw
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrontendappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Repos
                    val authRepo = remember { AuthRemoteDataResource(RetrofitInstance.userApi) }
                    val negocioRepo = remember { NegocioRemoteSource(RetrofitInstance.negocioApi) }

                    // Factory
                    val factory = remember { AppViewModelFactory(authRepo, negocioRepo) }

                    // ViewModels
                    val loginViewModel: LoginViewModel = viewModel(factory = factory)
                    val registerViewModel: RegisterViewModel = viewModel(factory = factory)
                    val negocioViewModel: NegocioViewModel = viewModel(factory = factory)
                    val bussinesMainViewModel: BussinesMainViewModel = viewModel(factory = factory)


                    // Navegación
                    Navigator(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        registerViewModel = registerViewModel,
                        negocioViewModel = negocioViewModel,
                        bussinesMainViewModel = bussinesMainViewModel
                    )
                }
            }
        }
    }
}



class AppViewModelFactory(
    private val authRepo: AuthRemoteDataResource,
    private val negocioRepo: NegocioRemoteSource,

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(authRepo) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(authRepo) as T

            modelClass.isAssignableFrom(NegocioViewModel::class.java) ->
                NegocioViewModel(negocioRepo) as T
            modelClass.isAssignableFrom(BussinesMainViewModel::class.java) ->
                BussinesMainViewModel(negocioRepo) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
