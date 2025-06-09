package com.example.frontendapp.ui.theme.composables.tab.adminPanel

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.frontendapp.data.model.UI.ResusableModalDTO
import com.example.frontendapp.data.remote.reponses.Resource
import com.example.frontendapp.ui.theme.FrontendappTheme
import com.example.frontendapp.ui.theme.composables.Items.NegocioListAdminItem
import com.example.frontendapp.ui.theme.composables.loadPages.TripleOrbitLoadingAnimation
import com.example.frontendapp.ui.theme.composables.modals.LogoutConfirmationDialog
import com.example.frontendapp.ui.theme.composables.modals.ModalConfig
import com.example.frontendapp.ui.theme.composables.modals.ModalType
import com.example.frontendapp.ui.theme.composables.modals.ReusableModal
import com.example.frontendapp.ui.theme.composables.section.HeaderSeccion
import com.example.frontendapp.ui.theme.composables.section.IconConfig
import com.example.frontendapp.ui.theme.navigation.NavigationItem
import com.example.frontendapp.ui.theme.viewmodels.NegocioViewModel


private val TAG = "NegociosPanelAdminContent"
@Composable
fun NegociosPanelAdminContent(
    navController: NavController,
    negocioViewModel: NegocioViewModel,
    onBack: () -> Unit,
){


    val isVisible = remember { mutableStateOf(false) }
    var isVisibleLogOut by remember { mutableStateOf(false) }
    val negociosListaState by negocioViewModel.negociosForAdmin.collectAsState()

    val modalState = remember { mutableStateOf(ResusableModalDTO()) }

    fun showModal(title: String, msg: String, type: ModalType) {
        modalState.value = ResusableModalDTO(
            title = title,
            msg = msg,
            type = type,
            show = true
        )
    }

    ReusableModal(
        isVisible = modalState.value.show,
        onDismiss = { modalState.value = modalState.value.copy(show = false) },
        onConfirm = { modalState.value = modalState.value.copy(show = false) },
        onCancel = { modalState.value = modalState.value.copy(show = false) },
        config = ModalConfig(
            title = modalState.value.title,
            message = modalState.value.msg,
            type = modalState.value.type,
        )
    )


    //Modal filtro
    FilterPanelModal(
        isVisible = isVisible.value,
        onDismiss = {isVisible.value=false}
    )





    LaunchedEffect(Unit) {
        negocioViewModel.getNegociosForAdmin()
    }

    Column(
        Modifier.systemBarsPadding())
    {
        HeaderSeccion(
            titulo = "Bienvenido \n Administrador",
            onFilterClick = { isVisible.value = true },
            iconConfig = IconConfig(
                isVisible = true,
                onClick = onBack,
            )
        )

        when(negociosListaState){
            is Resource.Success -> {

                //Suponemos que ha salido bien entonces cogemos la lista directamente
                val listaNegocios=  negociosListaState.data?.data ?: emptyList()

                listaNegocios.forEach{
                    //Lista todos los negocios que me lleguen
                    Log.d(TAG, it.toString())
                }
                if(listaNegocios.isEmpty()){
                    EmptyList()
                }else{
                    LazyColumn {
                        items(listaNegocios, key = { it.id }) { negocio ->
                            NegocioListAdminItem(
                                negocio = negocio,
                                show = true,
                                viewModel = negocioViewModel,
                                onDelete = {
                                    negocioViewModel.deleteNegocio(
                                        negocio.id,
                                        onSuccess = {
                                            showModal("Éxito", "Negocio eliminado con éxito", ModalType.SUCCESS)
                                            negocioViewModel.getNegociosForAdmin()
                                        },
                                        onError = {
                                            showModal("Error", it, ModalType.ERROR)
                                        }
                                    )
                                },
                                onBloquear = {
                                    negocioViewModel.bloquearNegocio(
                                        negocio.id,
                                        onSuccess = {
                                            showModal("Éxito", "Negocio bloqueado con éxito", ModalType.SUCCESS)
                                            negocioViewModel.getNegociosForAdmin()
                                        },
                                        onError = {
                                            showModal("Error", it, ModalType.ERROR)
                                        }
                                    )
                                },
                                onDesBloquear = {
                                    negocioViewModel.desbloquearNegocio(
                                        negocio.id,
                                        onSuccess = {
                                            showModal("Éxito", "Negocio desbloqueado con éxito", ModalType.SUCCESS)
                                            negocioViewModel.getNegociosForAdmin()
                                        },
                                        onError = {
                                            showModal("Error", it, ModalType.ERROR)
                                        }
                                    )
                                }
                            )
                        }

                    }
                }


            }

            is Resource.Error -> {
                Text("eror")

            }
            is Resource.Loading -> {
                Box(
                    Modifier.fillMaxSize()
                ){
                    TripleOrbitLoadingAnimation()
                }
            }
            is Resource.None -> {}
        }
    }
}
@Composable
fun EmptyList() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = "Lista vacía",
                modifier = Modifier
                    .size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay elementos disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}




@Composable
fun FilterPanelModal(
    isVisible: Boolean,
    onDismiss: () -> Unit
){
    if(isVisible){
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            FiltrosContentAdminPanel(
                onDismiss= onDismiss,
            )
        }
    }
}

@Composable
fun FiltrosContentAdminPanel(
    onDismiss: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            Modifier
            .fillMaxSize()
            .padding(24.dp)) {
            //  Header del modal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            //Filtro para las fechas creadas
        }
    }
}


@Preview
@Composable
fun FiltroModalPreview(){
    FrontendappTheme {
        FilterPanelModal(
            isVisible = true,
            onDismiss = {}
        )
    }
}
