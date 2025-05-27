package com.example.frontendapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.AuthRemoteDataResource
import com.example.frontendapp.data.remote.source.CategoriaRemoteDataSource
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.data.remote.source.ServicioRemoteSource
import com.example.frontendapp.ui.theme.navigation.Navigator
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.viewmodels.BussinesMainViewModel
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.LoginViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.RegisterViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.utils.UbicacionHelper
import java.util.jar.Manifest


//https://www.youtube.com/watch?v=IX1GkpV71pw
//https://medium.com/@kiwi47/create-a-flexible-and-customizable-calendar-view-in-android-with-jetpack-compose-56dfb911c2ab
//https://www.youtube.com/watch?v=9r4st6dmyNE -> tabItems
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Guardamos en remember si el permiso ya fue concedido
           UbicacionHelper.solicitarPermisoUbicacionDesde(this)


            FrontendappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Repos
                    val authRepo = remember { AuthRemoteDataResource(RetrofitInstance.userApi) }
                    val negocioRepo = remember { NegocioRemoteSource(RetrofitInstance.negocioApi) }
                    val servicioRepo = remember { ServicioRemoteSource(RetrofitInstance.servicioApi) }
                    val categoriaRepo = remember { CategoriaRemoteDataSource(RetrofitInstance.categoriaApi) }

                    // Factory
                    val factory = remember { AppViewModelFactory(authRepo, negocioRepo,servicioRepo, categoriaRepo) }

                    // ViewModels renombrados según su pantalla
                    val loginScreenViewModel: LoginViewModel = viewModel(factory = factory)
                    val registerScreenViewModel: RegisterViewModel = viewModel(factory = factory)
                    val negocioFormViewModel: NegocioViewModel = viewModel(factory = factory)
                    /*val usuarioNegocioMainViewModel: BussinesMainViewModel = viewModel(factory = factory)*/
                    val usuarioNegocioMainViewModel: NegocioViewModel = viewModel(factory = factory)

                    val reservasNegocioScreenViewModel: ReservasViewModel = viewModel(factory = factory)
                    val serviciosNegocioScreenViewModel: ServicioViewModel = viewModel(factory = factory)
                    val servicioViewModeServicioForm: ServicioViewModel = viewModel(factory = factory)
                    //ViewModel para pantallas de cliente
                    val servicioViewModel_ClienteMain: ServicioViewModel = viewModel(factory=factory) //Para listar y reservar servicios
                    val reservaViewModel_ClienteMain: ReservasViewModel = viewModel(factory=factory) //Para reservas
                    val negocioViewModel_ClienteMain: NegocioViewModel = viewModel(factory = factory) //Para listar y ver detaller de los negocios


                    //Uso solo una instancia de categorias ya que solo las listaré
                    val categoriasViewModel: CategoriaViewModel= viewModel(factory=factory)


                    // Navegación
                    Navigator(
                        navController = navController,
                        loginScreenViewModel = loginScreenViewModel,
                        registerScreenViewModel = registerScreenViewModel,
                        negocioFormViewModel = negocioFormViewModel,
                        usuarioNegocioMainViewModel = usuarioNegocioMainViewModel,
                        reservasNegocioScreenViewModel = reservasNegocioScreenViewModel,
                        serviciosNegocioScreenViewModel = serviciosNegocioScreenViewModel,
                        servicioViewModel_ClienteMain = servicioViewModel_ClienteMain,
                        reservaViewModel_ClienteMain = reservaViewModel_ClienteMain,
                        negocioViewModel_ClienteMain = negocioViewModel_ClienteMain,
                        categoriasViewModel = categoriasViewModel
                    )
                }
            }
        }
    }

}




// Esta clase implementa la interfaz ViewModelProvider.Factory
// y nos permite crear ViewModels con sus dependencias ya inyectadas.
class AppViewModelFactory(

    // Dependencias que usaremos para inyectar en los distintos ViewModels
    private val authRepo: AuthRemoteDataResource,
    private val negocioRepo: NegocioRemoteSource,
    private val servicioApi: ServicioRemoteSource,
    private val categoriaSource: CategoriaRemoteDataSource

) : ViewModelProvider.Factory {

    // El método create se llama automáticamente por Android para obtener un ViewModel
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Usamos when para decidir qué ViewModel se está solicitando
        return when {

            // Si se solicita LoginViewModel, se crea pasándole authRepo
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(authRepo) as T

            // Si se solicita RegisterViewModel, también necesita authRepo
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(authRepo) as T

            // NegocioViewModel requiere negocioRepo
            modelClass.isAssignableFrom(NegocioViewModel::class.java) ->
                NegocioViewModel(negocioRepo) as T

            // BussinesMainViewModel también requiere negocioRepo
            modelClass.isAssignableFrom(BussinesMainViewModel::class.java) ->
                BussinesMainViewModel(negocioRepo) as T

            // ReservasViewModel necesita negocioRepo igualmente
            modelClass.isAssignableFrom(ReservasViewModel::class.java) ->
                ReservasViewModel(negocioRepo) as T

            // ServicioViewModel necesita servicioApi
            modelClass.isAssignableFrom(ServicioViewModel::class.java) ->
                ServicioViewModel(servicioApi) as T

            // CategoriaViewModel requiere categoriaSource como dependencia
            modelClass.isAssignableFrom(CategoriaViewModel::class.java) ->
                CategoriaViewModel(categoriaSource) as T

            // Si se pide un ViewModel que no está soportado, se lanza una excepción
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

