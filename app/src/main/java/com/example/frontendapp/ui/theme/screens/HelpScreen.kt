package com.example.frontendapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontendapp.R
import com.example.frontendapp.ui.theme.AppColors
import com.example.frontendapp.ui.theme.FrontendappTheme

data class FAQItem(
    val question: String,
    val answer: String,
    val category: String
)

data class HelpSection(
    val title: String,
    val icon: ImageVector,
    val items: List<HelpItem>
)

data class HelpItem(
    val title: String,
    val description: String
)

@Composable
fun WelcomeHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.welcome_greeting),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.welcome_subtitle),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HelpSectionCard(section: HelpSection) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                Surface(
                    color = AppColors.GreenBackground,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = AppColors.GreenPrimary,
                        modifier = Modifier.padding(8.dp).size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                section.items.forEach { item ->
                    HelpItemRow(item = item)
                }
            }
        }
    }
}

@Composable
fun HelpItemRow(item: HelpItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FAQSection(faqItems: List<FAQItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.faq_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            faqItems.forEach { faq ->
                FAQItemRow(faq = faq)
            }
        }
    }
}

@Composable
fun FAQItemRow(faq: FAQItem) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = faq.question,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = faq.answer,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ContactSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Surface(
                    color = AppColors.GreenBackground,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AppColors.GreenPrimary,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.contact_info_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContactInfoRow(
                    icon = Icons.Default.Email,
                    title = stringResource(R.string.email_title),
                    description = stringResource(R.string.email_value)
                )

                ContactInfoRow(
                    icon = Icons.Default.Schedule,
                    title = stringResource(R.string.support_hours_title),
                    description = stringResource(R.string.support_hours_value)
                )

                ContactInfoRow(
                    icon = Icons.Default.LocationOn,
                    title = stringResource(R.string.location_title),
                    description = stringResource(R.string.location_value)
                )
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = AppColors.GreenBackground.copy(alpha = 0.5f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.GreenPrimary,
                modifier = Modifier
                    .padding(8.dp)
                    .size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen() {
    val context = LocalContext.current

    // Datos de ejemplo para FAQs (usando context.getString() en lugar de stringResource())
    val faqItems = remember {
        listOf(
            FAQItem(
                question = context.getString(R.string.faq_reserve_question),
                answer = context.getString(R.string.faq_reserve_answer),
                category = context.getString(R.string.category_reservations)
            ),
            FAQItem(
                question = context.getString(R.string.faq_cancel_question),
                answer = context.getString(R.string.faq_cancel_answer),
                category = context.getString(R.string.category_reservations)
            ),
            FAQItem(
                question = context.getString(R.string.faq_profile_update_question),
                answer = context.getString(R.string.faq_profile_update_answer),
                category = context.getString(R.string.category_account)
            ),
            FAQItem(
                question = context.getString(R.string.faq_profile_update_question),
                answer = context.getString(R.string.faq_email_change_answer),
                category = context.getString(R.string.category_account)
            ),
            FAQItem(
                question = context.getString(R.string.faq_payment_methods_question),
                answer = context.getString(R.string.faq_payment_methods_answer),
                category = context.getString(R.string.category_payments)
            ),
            FAQItem(
                question = context.getString(R.string.faq_refunds_question),
                answer = context.getString(R.string.faq_refunds_answer),
                category = context.getString(R.string.category_payments)
            ),
            FAQItem(
                question = context.getString(R.string.faq_contact_support_question),
                answer = context.getString(R.string.faq_contact_support_answer),
                category = context.getString(R.string.category_support)
            ),
            FAQItem(
                question = context.getString(R.string.faq_offline_question),
                answer = context.getString(R.string.faq_offline_answer),
                category = context.getString(R.string.category_technical)
            )
        )
    }


    // Secciones de ayuda
    val helpSections = remember {
        listOf(
            HelpSection(
                title = context.getString(R.string.section_getting_started),
                icon = Icons.Default.PlayArrow,
                items = listOf(
                    HelpItem(
                        title = context.getString(R.string.help_configure_profile_title),
                        description = context.getString(R.string.help_configure_profile_desc)
                    ),
                    HelpItem(
                        title = context.getString(R.string.help_first_reservation_title),
                        description = context.getString(R.string.help_first_reservation_desc)
                    ),
                    HelpItem(
                        title = context.getString(R.string.help_create_business_title),
                        description = context.getString(R.string.help_create_business_desc)
                    )
                )
            ),
            HelpSection(
                title = context.getString(R.string.section_manage_reservations),
                icon = Icons.AutoMirrored.Filled.EventNote,
                items = listOf(
                    HelpItem(
                        title = context.getString(R.string.help_view_reservations_title),
                        description = context.getString(R.string.help_view_reservations_desc)
                    ),
                    HelpItem(
                        title = context.getString(R.string.help_modify_reservation_title),
                        description = context.getString(R.string.help_modify_reservation_desc)
                    ),
                    HelpItem(
                        title = context.getString(R.string.help_cancel_reservation_title),
                        description = context.getString(R.string.help_cancel_reservation_desc)
                    ),
                    HelpItem(
                        title = context.getString(R.string.help_reservation_states_title),
                        description = context.getString(R.string.help_reservation_states_desc)
                    )
                )
            ),
            HelpSection(
                title = context.getString(R.string.section_payments),
                icon = Icons.Default.Payment,
                items = listOf(
                    HelpItem(
                        title = context.getString(R.string.help_payment_methods_title),
                        description = context.getString(R.string.help_payment_methods_desc)
                    ),
                    HelpItem(
                        title = context.getString(R.string.help_payment_history_title),
                        description = context.getString(R.string.help_payment_history_desc)
                    ),
                    HelpItem(
                        title = context.getString(R.string.help_refund_policy_title),
                        description = context.getString(R.string.help_refund_policy_desc)
                    )
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            tint = AppColors.GreenSecondary,
                            contentDescription = stringResource(R.string.back_arrow_description)
                        )
                        Spacer(Modifier.width(20.dp))
                        Text(
                            stringResource(R.string.help_center_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.GreenSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con bienvenida
            item {
                WelcomeHeader()
            }

            // Secciones de ayuda
            items(helpSections) { section ->
                HelpSectionCard(section = section)
            }

            // Preguntas frecuentes
            item {
                FAQSection(faqItems = faqItems)
            }

            // Información de contacto
            item {
                ContactSection()
            }

            // Espaciado final
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview
@Composable
fun HelpPreview() {
    FrontendappTheme {
        HelpScreen()
    }
}