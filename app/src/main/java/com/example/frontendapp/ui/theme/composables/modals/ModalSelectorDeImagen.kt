package com.example.frontendapp.ui.theme.composables.modals

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1

@Composable
fun ModalSelectorDeImagen(
    logoUrl: String?,
    visible: Boolean=false,
    imagenConfirmada: Uri? = null,
    onImagenSeleccionada: (Uri?) -> Unit = {},
    onCerrar: () -> Unit = {},
    onAccept: (Uri, () -> Unit) -> Unit = { _, onSuccess -> onSuccess() },
) {
    var imagenSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var showModal by remember { mutableStateOf(visible) }

    ServicioImagePicker(
        size = 100.dp,
        shape = RoundedCornerShape(40.dp),
        iconEdit = Icons.Default.Edit,
        clickable = false,
        imageUrl = logoUrl,
        icon = Icons.Default.Edit,
        imagenUriExterna = imagenConfirmada,
        onImageSelected = {},
        showIconEdit = true,
        borderColor = Color.White,
        borderWidth = 2.dp,
        modifier = Modifier.clickable {
            showModal = true
        }

    )

    if (showModal) {
        Dialog(onDismissRequest = {
            showModal=false
        }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Seleccionar imagen", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    ServicioImagePicker(

                        size = 200.dp,
                        clickable = true,
                        onImageSelected = {
                            imagenSeleccionada = it
                            onImagenSeleccionada(it)
                        }
                    )

                    Spacer(Modifier.height(24.dp))

                    Row {
                        BtnStyle1(
                            text = "Aceptar",
                            onClick = {
                                imagenSeleccionada?.let { uri ->
                                    onAccept(uri) {
                                        showModal=false
                                        onCerrar()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        BtnStyle1(
                            text = "Cancelar",
                            onClick = {
                                showModal=false
                                onCerrar()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewModalSelectorDeImagen() {
    var visible by remember { mutableStateOf(false) }
    MaterialTheme {
        ModalSelectorDeImagen(
            visible=true,
            logoUrl = "https://www.tooltyp.com/wp-content/uploads/2014/10/1900x920-8-beneficios-de-usar-imagenes-en-nuestros-sitios-web.jpg",
        )
    }
}
