package com.example.frontendapp.ui.theme.composables.ListItems

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontendapp.R
import com.example.frontendapp.data.model.Negocio


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NegocioListItem(
    negocio: Negocio,
    onEditClick: (Negocio) -> Unit = {},
    onDeleteClick: (Negocio) -> Unit = {},
    onCLickVer:(Negocio) -> Unit = {},
    show: Boolean = false,

) {
    val estadoActivo = negocio.activo == true
    val colorEstado = if (estadoActivo) Color(0xFF4CAF50) else Color(0xFFF44336) // Verde o rojo
    var expanded by remember { mutableStateOf(show) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // imagen genérica
            contentDescription = "Imagen del negocio",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = negocio.nombre, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${negocio.categoria})",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = negocio.direccion,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )
        }

        IconButton (
            onClick = { expanded = !expanded },
            modifier = Modifier.semantics {
                contentDescription = if (expanded) "Cerrar opciones" else "Abrir opciones"
            }
        ) {
            AnimatedContent(
                targetState = expanded,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut())
                }
            ) { targetExpanded ->
                if (targetExpanded) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Más opciones"
                    )
                }
            }
        }


    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Estado", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(
            text = if (estadoActivo) "Activo" else "Inactivo",
            style = MaterialTheme.typography.bodySmall,
            color = colorEstado
        )
    }

    AnimatedVisibility (
        visible = expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onEditClick(negocio) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Editar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar")
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(onClick = { onDeleteClick(negocio) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Ir",
                            modifier = Modifier.size(18.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar", color = Color.Red)

                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(onClick = { onCLickVer(negocio) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ir",
                            modifier = Modifier.size(18.dp),
                            tint = Color.Blue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Configurar", color = Color.Blue)

                    }
                }
            }
        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun NegocioListItemPreview() {
    val negocio = Negocio(
        nombre = "Negocio con horarios",
        descripcion = "Negocio con horarios incluidos",
        direccion = "Av. de los Horarios 1",
        latitud = 40.0,
        longitud = -3.0,
        categoria = "Oficina"
    )
    Scaffold {innerPadding ->
        var myPadding = innerPadding
        myPadding = PaddingValues(top = 8.dp, start = 1.dp)
        Box(modifier = Modifier.padding(myPadding)) {
            NegocioListItem(negocio, show = true)
        }
    }
}