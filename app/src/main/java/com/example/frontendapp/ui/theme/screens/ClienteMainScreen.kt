import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.UI.TabItem
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.composables.modals.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.composables.navigation.TabAnimatedScaffold
import com.example.frontendapp.ui.theme.composables.tab.NegocioTabContent
import com.example.frontendapp.ui.theme.composables.tab.ReservaTabContent
import com.example.frontendapp.ui.theme.composables.tab.ServicioTabContent
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.screens.PreferenciasScreenMejorada
import com.example.frontendapp.ui.theme.viewmodels.CategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.HorariosViewModel
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ValoracionViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeCategoriaViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeHorariosViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeReservasViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeValoracionViewModel

@Composable
fun ClienteMainScreen(
    navController: NavController,
    servicioViewModel: ServicioViewModel,
    reservasViewModel: ReservasViewModel,
    negocioViewModel: NegocioViewModel,
) {
    var show by remember { mutableStateOf(false) }
    LogoutConfirmationDialog(
        showDialog = show,
        onDismiss = {show=false},
        onConfirmLogout = {
            // Limpiar token y roles
            RetrofitInstance.setToken("")
            RetrofitInstance.setRoles(listOf())

            // Navegar al login limpiando completamente el back stack
            navController.navigate(NavigationItem.LOGIN.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }

    )
    val tabs = listOf(

        TabItem(
            title = "Negocio",
            unSelectedIcon = Icons.Outlined.Storefront,
            selectedIcon = Icons.Filled.Store,
            content = {
                NegocioTabContent(
                negocioViewModel,
                navController=navController
            )},
            index = 0
        ),
        TabItem(
            title = "Servicios",
            unSelectedIcon = Icons.Outlined.Event,
            selectedIcon = Icons.Filled.EventAvailable,
            content = {
                ServicioTabContent(
                    servicioViewModel= servicioViewModel,
                    navController = navController,
                )},
            index = 1
        ),
        TabItem(
            title = "Mis reservas",
            unSelectedIcon = Icons.Outlined.Book,
            selectedIcon = Icons.Filled.Bookmark,
            content = { ReservaTabContent(
                reservaViewModel = reservasViewModel,
                navController = navController
            ) },
            index = 2
        ),
        TabItem(
            title = "Configuración",
            unSelectedIcon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings,
            content = {
                PreferenciasScreenMejorada(
                    onNavigateToEditProfile = {
                        navController.navigate(NavigationItem.EDIT_PROFILE.route)
                    },
                    onNavigateToChangePassword = { navController.navigate(NavigationItem.EDIT_PROFILE.route) }
                )
            },
            index = 3
        )
    )
    TabAnimatedScaffold(
        tabs = tabs
    )
}




@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ClienteMainScreenPreview() {
    // Usamos ViewModels simulados para el preview
    val navController = rememberNavController()
    val fakeServicio = FakeServicioViewModel()
    val fakeReservas = FakeReservasViewModel()
    val fakeNegocio = FakeNegocioViewModel()


    ClienteMainScreen(
        navController = navController,
        servicioViewModel = fakeServicio,
        reservasViewModel = fakeReservas,
        negocioViewModel = fakeNegocio,
    )
}
