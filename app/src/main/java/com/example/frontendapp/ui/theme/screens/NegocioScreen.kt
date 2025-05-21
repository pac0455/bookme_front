package com.example.frontendapp.ui.theme.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource


@Composable
fun NegocioScreen(
    viewModel: NegocioViewModel,
    navController: NavController
) {
    val negocio by viewModel.negocioState.collectAsState()

    Scaffold(
        topBar = {
            TopBarBack(title = negocio.nombre ?: "Negocio") {
                navController.popBackStack()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Imagen/Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.default_bussines_picture), // Tu imagen
                    contentDescription = "Imagen del negocio",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dirección
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Principal_variacion3)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = negocio.direccion ?: "Sin dirección")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción
            Text(
                text = negocio.descripcion ?: "Sin descripción.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categoría
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Principal_variacion3)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Categoría: ${negocio.categoria ?: "No especificada"}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horarios
            Text(
                text = "Horario de atención:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (negocio.horarioAtencion.isNullOrEmpty()) {
                Text("No hay horarios configurados", color = Color.Gray)
            } else {
                negocio.horarioAtencion!!
                    .groupBy { it.diaSemana }
                    .forEach { (dia, horarios) ->
                        val bloques = horarios.joinToString(" / ") { "${it.horaInicio} - ${it.horaFin}" }
                        Text("- $dia: $bloques")
                    }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Galería simple (simulada)
            Text(
                text = "Galería",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(5) { // Ejemplo estático
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    ) {
                        // Aquí irían tus imágenes reales del negocio
                        Text(
                            text = "Imagen",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarBack(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, color = Color.White) },
        navigationIcon = {
            IconButton (onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Principal_variacion3 // Tu color principal
        )
    )
}



class FakeNegocioViewModel : NegocioViewModel(NegocioRemoteSource(RetrofitInstance.negocioApi)) {
    init {
        setNombre("Centro Estético BellaVida")
        setDescripcion("Ofrecemos servicios de estética avanzada, masajes y tratamientos faciales. Atención personalizada.")
        setDireccion("Calle del Sol, 123 - Madrid")
        setcategoria("Estética")
        setHorarios(
            listOf(
                Horario(id = 1, idNegocio = 1, diaSemana = "Lunes", horaInicio = "09:00", horaFin = "13:00"),
                Horario(id = 2, idNegocio = 1, diaSemana = "Lunes", horaInicio = "17:00", horaFin = "20:00"),
                Horario(id = 3, idNegocio = 1, diaSemana = "Martes", horaInicio = "09:00", horaFin = "13:00"),
                Horario(id = 4, idNegocio = 1, diaSemana = "Martes", horaInicio = "17:00", horaFin = "20:00"),
                Horario(id = 5, idNegocio = 1, diaSemana = "Miércoles", horaInicio = "09:00", horaFin = "13:00"),
                Horario(id = 6, idNegocio = 1, diaSemana = "Miércoles", horaInicio = "17:00", horaFin = "20:00"),
                Horario(id = 7, idNegocio = 1, diaSemana = "Jueves", horaInicio = "09:00", horaFin = "13:00"),
                Horario(id = 8, idNegocio = 1, diaSemana = "Jueves", horaInicio = "17:00", horaFin = "20:00"),
                Horario(id = 9, idNegocio = 1, diaSemana = "Viernes", horaInicio = "09:00", horaFin = "13:00"),
                Horario(id = 10, idNegocio = 1, diaSemana = "Viernes", horaInicio = "17:00", horaFin = "20:00")
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewNegocioScreen() {
    val viewModel = remember { FakeNegocioViewModel() }
    val navController = rememberNavController()

    NegocioScreen(viewModel = viewModel, navController = navController)
}


