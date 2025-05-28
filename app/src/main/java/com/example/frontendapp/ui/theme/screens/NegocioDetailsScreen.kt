package com.example.frontendapp.ui.theme.screens
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio.NegocioCardCliente
import com.example.frontendapp.data.model.UI.TabItem
import com.example.frontendapp.ui.theme.composables.ListItems.EstadoEtiqueta
import com.example.frontendapp.ui.theme.composables.ListItems.RatingStars
import com.example.frontendapp.ui.theme.composables.modal.ServicioImagePicker
import com.example.frontendapp.ui.theme.composables.navigation.TabPagerScaffold
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.ServicioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeNegocioViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeServicioViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioDetailScreen(
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    servicioViewModel: ServicioViewModel,
    modifier: Modifier = Modifier
) {

    val negocioCard by negocioViewModel.tempNegocioCard.collectAsState()
    val imagenUrl by remember { mutableStateOf(negocioViewModel.getNegocioImageUrl(negocioCard.id)) }

    Column(modifier = modifier.fillMaxSize()) {
        ServicioImagePicker(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            imageModifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            id = negocioCard.id,
            clickable = false,
            showIconEdit = false,
            borderColor = MaterialTheme.colorScheme.primary,
            borderWidth = 1.dp,
            imageUrl = imagenUrl
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = negocioCard.nombre, style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = negocioCard.descripcion,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EstadoEtiqueta(isOpen = negocioCard.isOpen)

                if (negocioCard.reviewCount > 0) {
                    RatingStars(rating = negocioCard.rating)
                } else {
                    Text(
                        text = "Sin reseñas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = negocioCard.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Dirección: ${negocioCard.direccion}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = negocioCard.distancia?.let { "Distancia: %.2f km".format(it) }
                    ?: "No se puede calcular la distancia desde tu ubicación",
                style = MaterialTheme.typography.bodySmall
            )
        }
        val tabItems = listOf(
            TabItem(
                title = NegocioDetailTab.SERVICIOS.title,
                selectedIcon = Icons.Default.Build,
                unSelectedIcon = Icons.Default.Build,
                content = {
                    Text("adfasd")
                }
            ),
            TabItem(
                title = NegocioDetailTab.HORARIOS.title,
                selectedIcon = Icons.Default.Schedule,
                unSelectedIcon = Icons.Default.Schedule,
                content = {
                    Text("adfasd")
                }
            ),
            TabItem(
                title = NegocioDetailTab.VALORACIONES.title,
                selectedIcon = Icons.Default.Star,
                unSelectedIcon = Icons.Default.StarBorder,
                content = {
                    Text("adfasd")
                }
            )
        )

        TabPagerScaffold(tabItems = tabItems)
    }
}


enum class NegocioDetailTab(val title: String) {
    HORARIOS("Horarios"),
    VALORACIONES("Valoraciones"),

    SERVICIOS("Servicios");

    companion object {
        fun fromTitle(title: String): NegocioDetailTab =
            entries.firstOrNull { it.title.equals(title, ignoreCase = true) } ?: SERVICIOS
    }
}

@Composable
fun HorariosTab(horarios: List<Horario>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(horarios) {
            Text("${it.diaSemana}: ${it.horaInicio} - ${it.horaFin}")
        }
    }
}

@Composable
fun ServiciosTab(servicios: List<String>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(servicios) { servicio ->
            Text("• $servicio")
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewNegocioDetailsScreen() {
    val negocio = NegocioCardCliente(
        id = 1,
        nombre = "Peluquería Bella",
        descripcion = "Cortes modernos y tratamientos capilares",
        categoria = "Belleza",
        direccion = "Calle Falsa 123",
        rating = 4.5f,
        reviewCount = 34,
        isActive = true,
        isOpen = true,
        distancia = 1350.0,
        latitud = 40.4168,
        longitud = -3.7038
    )

    val horarios = listOf(
        Horario(1, 1, "Lunes", "09:00", "17:00"),
        Horario(2, 1, "Martes", "09:00", "17:00"),
        Horario(3, 1, "Miércoles", "10:00", "18:00"),
    )

    NegocioDetailScreen(
        negocioViewModel =FakeNegocioViewModel(),
        servicioViewModel = FakeServicioViewModel(),
        navController = rememberNavController()
    )
}
