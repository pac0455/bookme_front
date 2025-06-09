package com.example.frontendapp.ui.theme.composables.Items
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.frontendapp.data.model.Usuario.Usuario
import com.example.frontendapp.ui.theme.ThemeColors
import com.example.frontendapp.ui.theme.composables.Btn.ActionButton


@Composable
fun UsuarioCardExpandable(
    usuario: Usuario,
    onDeleteClick: (Usuario) -> Unit,
    onBlockClick: (Usuario) -> Unit,
    onDesbloquear: (Usuario) -> Unit,
    onBack: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            UsuarioCardHeader(usuario = usuario, expanded = expanded)
            UsuarioCardExpandableContent(
                usuario = usuario,
                expanded = expanded,
                onDeleteClick = onDeleteClick,
                onBlockClick = onBlockClick,
                onDesbloquear = onDesbloquear
            )
        }
    }
}

@Composable
private fun UsuarioCardExpandableContent(
    usuario: Usuario,
    expanded: Boolean,
    onDeleteClick: (Usuario) -> Unit,
    onBlockClick: (Usuario) -> Unit,
    onDesbloquear: (Usuario) -> Unit,

) {
    UsuarioStatusBar(
        bloqueado = usuario.Bloqueado
    )
    AnimatedVisibility(visible = expanded) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Acciones disponibles",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            UsuarioActionButtonsRow(
                usuario = usuario,
                onDeleteClick = onDeleteClick,
                onBlockClick = onBlockClick,
                onDesbloquear = onDesbloquear
            )
        }
    }
}
@Composable
fun UsuarioStatusBar(
    bloqueado: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color =
            if(bloqueado) MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            else ThemeColors.success.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector =
                        if(bloqueado) Icons.Default.Lock
                        else Icons.Default.LockOpen,
                    contentDescription = if(bloqueado) "Estado del usuario bloqueado"
                        else "Estado del usuario desbloqueado",
                    modifier = Modifier.size(20.dp),
                    tint =   if(bloqueado) ThemeColors.error
                        else ThemeColors.success,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Estado del negocio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if(bloqueado) ThemeColors.error.copy(alpha = 0.2f)
                        else ThemeColors.success.copy(alpha = 0.2f),
            ) {
                Text(
                    text =  if(bloqueado) "Bloqueado"
                            else "Activo",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = if(bloqueado) ThemeColors.error
                            else ThemeColors.success,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}


@Composable
private fun UsuarioCardHeader(usuario: Usuario, expanded: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Ícono de usuario",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = usuario.username ?: "Sin nombre",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = usuario.email ?: "Sin email",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (expanded) "Ocultar acciones" else "Mostrar acciones",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun UsuarioActionButtonsRow(
    usuario: Usuario,
    onDeleteClick: (Usuario) -> Unit,
    onBlockClick: (Usuario) -> Unit,
    onDesbloquear: (Usuario) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionButton(
            icon = Icons.Default.Delete,
            text = "Eliminar",
            contentDescription = "Eliminar usuario ${usuario.username}",
            onClick = { onDeleteClick(usuario) },
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.weight(1f)
        )
        if(!usuario.Bloqueado){
            ActionButton(
                icon = Icons.Default.Block,
                text = "Bloquear",
                contentDescription = "Bloquear usuario ${usuario.username}",
                onClick = { onBlockClick(usuario) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }else{
            ActionButton(
                icon = Icons.Default.LockOpen,
                text = "Desbloquear",
                contentDescription = "Bloquear usuario ${usuario.username}",
                onClick = { onDesbloquear(usuario) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun UsuarioCardExpandablePreview() {
    val usuarioEjemplo = Usuario(
        id = "123",
        username = "juan_perez",
        email = "juan.perez@example.com",
        phoneNumber = "555-1234",
        password = "password123",
        firebaseUid = "firebase-uid-123",
        fechaRegistro = null,
        isNegocio = true,
        Bloqueado = true
    )

    MaterialTheme {
        UsuarioCardExpandable(
            usuario = usuarioEjemplo,
            onDeleteClick = {},
            onBlockClick = {},
            onBack = {},
            onDesbloquear = {}
        )
    }
}
