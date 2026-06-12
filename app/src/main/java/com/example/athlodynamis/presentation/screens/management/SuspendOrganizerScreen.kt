package com.example.athlodynamis.presentation.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.data.remote.dto.UserDto
import com.example.athlodynamis.data.repository.UserRepository
import com.example.athlodynamis.presentation.components.*
import com.example.athlodynamis.presentation.navigation.Screen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun SuspendOrganizerScreen(navController: NavController) {
    var organizers by remember { mutableStateOf<List<UserDto>>(emptyList()) }
    var selectedOrganizer by remember { mutableStateOf<UserDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    val loadErrorText = stringResource(R.string.suspend_organizer_load_error)
    val successText = stringResource(R.string.suspend_organizer_success)
    val suspendErrorText = stringResource(R.string.suspend_organizer_error)

    LaunchedEffect(Unit) {
        try {
            organizers = UserRepository().getAllUsers()
                .filter {
                    it.role.equals("ORGANIZER", true) &&
                            !it.approvalStatus.equals("REJECTED", true)
                }
        } catch (e: Exception) {
            message = e.message ?: loadErrorText
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Profile.route,
                userRole = AthloUserRole.ADMIN
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SuspendOrganizerHeader { navController.popBackStack() }
            }

            item {
                Column {
                    Text(
                        text = stringResource(R.string.suspend_organizer_select_title),
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.suspend_organizer_select_subtitle),
                        color = AthloColors.TextSecondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            when {
                isLoading -> item { InfoCard(stringResource(R.string.suspend_organizer_loading)) }

                organizers.isEmpty() -> item {
                    InfoCard(stringResource(R.string.suspend_organizer_empty))
                }

                else -> {
                    items(organizers) { organizer ->
                        OrganizerSelectionRow(
                            organizer = organizer,
                            selected = selectedOrganizer?.id == organizer.id,
                            onClick = {
                                selectedOrganizer = organizer
                                message = null
                            }
                        )
                    }
                }
            }

            message?.let {
                item { InfoCard(it) }
            }

            item {
                Button(
                    onClick = {
                        val organizer = selectedOrganizer ?: return@Button
                        isLoading = true

                        MainScope().launch {
                            try {
                                UserRepository().updateOrganizerApproval(
                                    userId = organizer.id,
                                    approvalStatus = "REJECTED"
                                )

                                organizers = organizers.filter { it.id != organizer.id }
                                selectedOrganizer = null
                                message = successText
                            } catch (e: Exception) {
                                message = e.message ?: suspendErrorText
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = selectedOrganizer != null && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE84D4D),
                        disabledContainerColor = Color(0xFFAEB7C3)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = stringResource(R.string.suspend_organizer_suspend_cd),
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.suspend_organizer_button),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SuspendOrganizerHeader(onBackClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthloColors.Navy)
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(end = 90.dp)
            ) {
                AthloBackButton(onClick = onBackClick)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.suspend_organizer_header_title),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.suspend_organizer_header_subtitle),
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            AdminBadge(
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun OrganizerSelectionRow(
    organizer: UserDto,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFFFF3D6) else AthloColors.CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFFE84D4D),
                    unselectedColor = AthloColors.TextSecondary
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            ) {
                Text(
                    text = organizer.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = organizer.email,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = organizer.approvalStatus,
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(AthloColors.Navy, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.suspend_organizer_organizer_cd),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Text(
            text = text,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun AdminBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Color(0xFFFFD928), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = stringResource(R.string.admin_badge),
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.admin_badge),
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}