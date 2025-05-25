package com.example.frontendapp.ui.theme.composables.modal

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1

@Composable
fun ModalSelectorDeImagen(
    logoUrl: String?,
    imagenConfirmada: Uri? = null,
    onImagenSeleccionada: (Uri?) -> Unit = {},
    onCerrar: () -> Unit = {},
    onAccept: (Uri, () -> Unit) -> Unit = { _, onSuccess -> onSuccess() },
    startDialogVisible: Boolean = false
) {
    var showDialog by remember { mutableStateOf(startDialogVisible) }
    var imagenSeleccionada by remember { mutableStateOf<Uri?>(null) }

    // Imagen que se muestra fuera del modal: solo la confirmada (después de onSuccess)
    ServicioImagePicker(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape)
            .clickable { showDialog = true },
        size = 100.dp,
        clickable = false,
        imageUrl = logoUrl,
        imagenUriExterna = imagenConfirmada,
        onImageSelected = {}
    )

    if (showDialog) {
        Dialog(onDismissRequest = {
            showDialog = false
            onCerrar()
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
                                    // Llama a onAccept con la URI y callback que oculta modal tras éxito
                                    onAccept(uri) {
                                        showDialog = false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(16.dp))
                        BtnStyle1(
                            text = "Cancelar",
                            onClick = { showDialog = false },
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
    MaterialTheme {
        ModalSelectorDeImagen(
            logoUrl = "https://www.tooltyp.com/wp-content/uploads/2014/10/1900x920-8-beneficios-de-usar-imagenes-en-nuestros-sitios-web.jpg",
            startDialogVisible = false // Aquí forzamos a abrir el modal en la preview
        )
    }
}
