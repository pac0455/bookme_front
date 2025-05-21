package com.example.frontendapp.ui.theme.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.frontendapp.ui.theme.Principal_variacion3
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Horario
import com.example.frontendapp.data.model.Negocio
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.data.remote.source.NegocioRemoteSource
import com.example.frontendapp.ui.theme.Principal_variacion1
import com.example.frontendapp.ui.theme.Principal_variacion2
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewModelScope
import com.example.frontendapp.ui.theme.Principal_variacion6
import com.example.frontendapp.ui.theme.composables.QuickActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//https://www.youtube.com/watch?v=5dDHplq9rss
enum class ContentType {
    RESERVAS,
    CALENDARIO,
    SUBSCRIPTOR,
    OTRA1,
    OTRA2,
    OTRA3
}

@Composable
fun NegocioScreen(
    viewModel: NegocioViewModel,
    navController: NavController
) {
    val negocio by viewModel.negocioState.collectAsState()
    var selectedContent by remember { mutableStateOf<ContentType?>(ContentType.CALENDARIO) }


    Column(modifier = Modifier.fillMaxSize()) {

        // Cabecera con nombre e imagen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .clip(RoundedCornerShape(bottomEnd = 40.dp, bottomStart = 40.dp))
                .background(Principal_variacion3)
                .padding(16.dp)

                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Hello,", color = Color.White)
                Text(
                    text = negocio.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            negocio.logoUrl?.let { logoUrl ->
                AsyncImage(
                    model = logoUrl,
                    contentDescription = "Logo negocio",
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background),
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }


// Acciones rápidas
        QuickActionsExpandable(
            selectedContent = selectedContent,
            onContentSelected = { selectedContent = it }
        )



        // Buscar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Buscar...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )



        // Cargar contenido dinamico
        // Animación para mostrar el contenido
        AnimatedContent(selectedContent = selectedContent)


    }

}

@Composable
fun QuickActionsExpandable(
    selectedContent: ContentType?,
    onContentSelected: (ContentType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val allActions = listOf(
        Pair(ContentType.RESERVAS, Icons.Default.Notifications),
        Pair(ContentType.CALENDARIO, Icons.Default.CalendarMonth),
        Pair(ContentType.SUBSCRIPTOR, Icons.Filled.SupervisedUserCircle),
        Pair(ContentType.OTRA1, Icons.Default.Email),
        Pair(ContentType.OTRA2, Icons.Default.Phone),
        Pair(ContentType.OTRA3, Icons.Default.Settings)
    )

    // Caja contenedora única con fondo opaco
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .offset(y = (-32).dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(Principal_variacion6, RoundedCornerShape(24.dp))
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 300)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Primera fila (siempre visible)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            allActions.take(3).forEach { (contentType, icon) ->
                QuickActionButton(
                    onClick = { onContentSelected(contentType) },
                    icon = icon,
                    isSelected = contentType == selectedContent
                )
            }
        }

        // Botones adicionales dentro del mismo contenedor
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)),
            exit = fadeOut(tween(300)) + slideOutVertically(tween(300))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 0.dp, max = 220.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allActions.drop(3)) { (contentType, icon) ->
                        QuickActionButton(
                            onClick = { onContentSelected(contentType) },
                            icon = icon,
                            isSelected = contentType == selectedContent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Ver más / Ver menos
        TextButton(onClick = { expanded = !expanded }) {
            Text(if (expanded) "Ver menos" else "Ver más")
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedContent(selectedContent: ContentType?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(12.dp)) // Fondo opaco
            .shadow(4.dp, RoundedCornerShape(12.dp))             // Sombra para dar profundidad
            .padding(16.dp)
    ) {
        AnimatedContent(
            targetState = selectedContent,
            transitionSpec = {
                val direction = if ((targetState?.ordinal ?: 0) > (initialState?.ordinal ?: 0)) 1 else -1

                (slideInHorizontally(
                    animationSpec = tween(durationMillis = 500),
                    initialOffsetX = { fullWidth -> direction * fullWidth }
                ) + fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.9f)) togetherWith
                        (slideOutHorizontally(
                            animationSpec = tween(durationMillis = 500),
                            targetOffsetX = { fullWidth -> -direction * fullWidth }
                        ) + fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 1.1f))
            },
            label = "AdvancedContentAnimation"
        ) { targetContent ->
            when (targetContent) {
                ContentType.RESERVAS -> Text("Contenido de Reservas", style = MaterialTheme.typography.bodyLarge)
                ContentType.CALENDARIO -> Text("Contenido de Calendario", style = MaterialTheme.typography.bodyLarge)
                ContentType.SUBSCRIPTOR -> Text("Contenido de Subscriptores", style = MaterialTheme.typography.bodyLarge)
                else -> Text("Selecciona una sección", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}






class FakeNegocioViewModel : NegocioViewModel(NegocioRemoteSource(RetrofitInstance.negocioApi)) {

    override fun loadNegocioById(
        id: Int,
        onLoading: () -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        onLoading()

        // Simular pequeña demora para parecer real
        viewModelScope.launch {
            delay(500)  // simula loading de medio segundo

            // Luego asignas el negocio
            _negocioState.value = Negocio(
                id = id,
                nombre = "Centro Estético BellaVida",
                descripcion = "Ofrecemos servicios de estética avanzada, masajes y tratamientos faciales. Atención personalizada.",
                direccion = "Calle del Sol, 123 - Madrid",
                categoria = "Estética",
                horarioAtencion = listOf(
                    Horario(id = 1, idNegocio = id, diaSemana = "Lunes", horaInicio = "09:00", horaFin = "13:00"),
                    Horario(id = 2, idNegocio = id, diaSemana = "Lunes", horaInicio = "17:00", horaFin = "20:00"),
                    // más horarios...
                )
            )
            onSuccess()
        }
    }


}


@Preview(showBackground = true)
@Composable
fun PreviewNegocioScreen() {
    val viewModel = remember { FakeNegocioViewModel() }
    val navController = rememberNavController()

    NegocioScreen(viewModel = viewModel, navController = navController)
}


