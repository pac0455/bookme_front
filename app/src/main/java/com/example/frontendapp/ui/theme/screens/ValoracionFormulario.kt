package com.example.frontendapp.ui.theme.screens
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.frontendapp.R
import com.example.frontendapp.data.remote.RetrofitInstance
import com.example.frontendapp.ui.theme.composables.CustomMultilineTextField
import com.example.frontendapp.ui.theme.viewmodels.ValoracionViewModel
import com.example.frontendapp.ui.theme.viewmodels.fakeViewModel.FakeValoracionViewModel


private val TAG="ValoracionFormulario"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValoracionFormulario(
    valoracionesViewModel: ValoracionViewModel,
    navController: NavController,
    negocioId: Int,
) {
    val valoracionStateUI by valoracionesViewModel.valoracionStateUi.collectAsState()
    val valoracionValidationState by valoracionesViewModel.valoracionValidationState.collectAsState()
    val errors = valoracionValidationState.errors
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        valoracionesViewModel.resetUIState()
        valoracionesViewModel.clearErrors()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.new_review_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = stringResource(id = R.string.back_button_description))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(id = R.string.leave_your_review), fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // Rating Bar
            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= valoracionStateUI.puntuacion) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = stringResource(id = R.string.star_description, i),
                        tint = Color(0xFFFFD700),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { valoracionesViewModel.setPuntuacion(i.toDouble()) }
                    )
                }
            }

            // Comentario
            CustomMultilineTextField(
                value = valoracionStateUI.comentario,
                onValueChange = { valoracionesViewModel.setComentario(it) },
                label = stringResource(id = R.string.comment_label),
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                maxLines = 4,
                errorMessage = errors["comentario"]
            )

            // Botón de enviar
            Button(
                onClick = {
                    // Establecer los valores necesarios
                    valoracionesViewModel.setNegocioId(negocioId)
                    valoracionesViewModel.setUsuarioId(RetrofitInstance.getUserId())

                    // Validar los campos
                    valoracionesViewModel.validateUI()

                    // IMPORTANTE: Usar valoración actualizada después de setNegocioId/UsuarioId
                    val valoracionState = valoracionesViewModel.valoracionStateUi.value
                    val errores = valoracionValidationState.errors

                    if (errores.isEmpty()) {
                        Log.d("ValoracionFormulario", "Subiendo la valoración: $valoracionState")

                        valoracionesViewModel.addValoracion(
                            onLoading = {
                                // opcional: mostrar loading
                            },
                            onSuccess = {
                                Toast.makeText(context, context.getString(R.string.comment_uploaded_message), Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = { mensaje ->
                                Toast.makeText(context, context.getString(R.string.error_uploading_comment_message), Toast.LENGTH_SHORT).show()
                                Log.d("ValoracionFormulario", mensaje)
                            }
                        )
                    } else {
                        Toast.makeText(context, context.getString(R.string.form_errors_message), Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = true,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(id = R.string.send_button_text))
            }

        }
    }
}
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ValoracionFormularioPreview() {
    MaterialTheme {
        ValoracionFormulario(
            FakeValoracionViewModel(),
            rememberNavController(),
            -1)
    }
}
