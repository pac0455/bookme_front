package com.example.frontendapp.ui.theme.composables.modals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.composables.Btn.BtnStyle1
import com.example.frontendapp.ui.theme.composables.Btn.IconPosition

@Composable
fun LogoutConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmLogout: () -> Unit,
    imageRes: Int? = null,
) {
    AnimatedVisibility(
        visible = showDialog,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { it / 2 }
        ),
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it / 2 }
        )
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp)),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.logout_dialog_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        modifier = Modifier.width(40.dp),
                        thickness = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (imageRes != null) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = stringResource(R.string.logout_icon_desc),
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = stringResource(R.string.logout_icon_desc),
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.logout_dialog_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BtnStyle1(
                        iconPosition = IconPosition.START,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        text = stringResource(R.string.logout_cancel_button),
                        icon = Icons.Default.Cancel,
                        onClick = onDismiss
                    )

                    BtnStyle1(
                        iconPosition = IconPosition.START,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        text = stringResource(R.string.logout_confirm_button),
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        onClick = onConfirmLogout
                    )
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLogoutConfirmationDialog() {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        LogoutConfirmationDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onConfirmLogout = {
                showDialog = false
                // Acción simulada en el preview
            }
        )
    }
}

