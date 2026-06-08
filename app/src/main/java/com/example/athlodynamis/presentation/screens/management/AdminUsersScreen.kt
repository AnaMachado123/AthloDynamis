package com.example.athlodynamis.presentation.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.data.remote.dto.UserDto
import com.example.athlodynamis.data.repository.UserRepository
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.components.AthloBackButton
@Composable
fun AdminUsersScreen(
    navController: NavController,
    filter: String
) {
    var users by remember {
        mutableStateOf<List<UserDto>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val title = when (filter) {
        "organizers" -> "Organizadores"
        "players" -> "Jogadores"
        else -> "Utilizadores"
    }

    val subtitle = when (filter) {
        "organizers" -> "Lista de organizadores registados"
        "players" -> "Lista de jogadores registados"
        else -> "Todos os utilizadores da plataforma"
    }

    val icon = when (filter) {
        "organizers" -> Icons.Default.SupervisorAccount
        "players" -> Icons.Default.Groups
        else -> Icons.Default.Person
    }

    LaunchedEffect(filter) {
        isLoading = true
        errorMessage = null

        try {
            val allUsers = UserRepository().getAllUsers()

            users = when (filter) {
                "organizers" -> allUsers.filter {
                    it.role.equals("ORGANIZER", ignoreCase = true)
                }

                "players" -> allUsers.filter {
                    it.role.equals("PLAYER", ignoreCase = true)
                }

                else -> allUsers
            }.sortedByDescending {
                it.createdAt ?: ""
            }

        } catch (e: Exception) {
            errorMessage = e.message ?: "Erro ao carregar utilizadores."
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Management.route,
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                AdminUsersHeader(
                    title = title,
                    subtitle = subtitle,
                    count = users.size,
                    icon = icon,
                    isLoading = isLoading,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            if (errorMessage != null) {
                item {
                    InfoCard(
                        text = errorMessage ?: "Erro ao carregar utilizadores."
                    )
                }
            }

            item {
                Text(
                    text = "Listagem",
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            when {
                isLoading -> {
                    item {
                        EmptyStateCard(
                            text = "A carregar utilizadores..."
                        )
                    }
                }

                users.isEmpty() -> {
                    item {
                        EmptyStateCard(
                            text = "Não existem utilizadores para este filtro."
                        )
                    }
                }

                else -> {
                    items(users) { user ->
                        AdminUserCard(user = user)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersHeader(
    title: String,
    subtitle: String,
    count: Int,
    icon: ImageVector,
    isLoading: Boolean,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AthloColors.Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AthloBackButton(
                    text = "voltar",
                    onClick = onBackClick
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = subtitle,
                            color = Color(0xFFC8DCEF),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(AthloColors.Blue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .background(
                        color = Color(0xFF244A70),
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLoading) "..." else count.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "registos encontrados",
                        color = Color(0xFFC8DCEF),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminUserCard(user: UserDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(user.role.roleBackgroundColor(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.initials(),
                    color = user.role.roleTextColor(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = user.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = user.email,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserChip(
                        text = user.role.toRoleLabel(),
                        backgroundColor = user.role.roleBackgroundColor(),
                        textColor = user.role.roleTextColor()
                    )

                    UserChip(
                        text = user.approvalStatus.toApprovalLabel(),
                        backgroundColor = user.approvalStatus.approvalBackgroundColor(),
                        textColor = user.approvalStatus.approvalTextColor()
                    )
                }
            }
        }
    }
}

@Composable
private fun UserChip(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InfoCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFCC1F2F),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun EmptyStateCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Text(
            text = text,
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(20.dp)
        )
    }
}

private fun String.initials(): String {
    return split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") {
            it.first().uppercase()
        }
        .ifBlank { "U" }
}

private fun String.toRoleLabel(): String {
    return when {
        equals("ADMIN", ignoreCase = true) -> "Admin"
        equals("ORGANIZER", ignoreCase = true) -> "Organizador"
        equals("PLAYER", ignoreCase = true) -> "Jogador"
        else -> this
    }
}

private fun String.roleBackgroundColor(): Color {
    return when {
        equals("ADMIN", ignoreCase = true) -> AthloColors.WarningBg
        equals("ORGANIZER", ignoreCase = true) -> AthloColors.SuccessBg
        equals("PLAYER", ignoreCase = true) -> AthloColors.InfoBg
        else -> AthloColors.NeutralBg
    }
}

private fun String.roleTextColor(): Color {
    return when {
        equals("ADMIN", ignoreCase = true) -> Color(0xFF7A5B00)
        equals("ORGANIZER", ignoreCase = true) -> Color(0xFF4D8B4A)
        equals("PLAYER", ignoreCase = true) -> AthloColors.Blue
        else -> AthloColors.TextSecondary
    }
}

private fun String.toApprovalLabel(): String {
    return when {
        equals("APPROVED", ignoreCase = true) -> "Aprovado"
        equals("PENDING", ignoreCase = true) -> "Pendente"
        equals("REJECTED", ignoreCase = true) -> "Rejeitado"
        else -> this
    }
}

private fun String.approvalBackgroundColor(): Color {
    return when {
        equals("APPROVED", ignoreCase = true) -> AthloColors.SuccessBg
        equals("PENDING", ignoreCase = true) -> AthloColors.WarningBg
        equals("REJECTED", ignoreCase = true) -> AthloColors.DangerBg
        else -> AthloColors.NeutralBg
    }
}

private fun String.approvalTextColor(): Color {
    return when {
        equals("APPROVED", ignoreCase = true) -> Color(0xFF4D8B4A)
        equals("PENDING", ignoreCase = true) -> Color(0xFF7A5B00)
        equals("REJECTED", ignoreCase = true) -> Color(0xFFCC1F2F)
        else -> AthloColors.TextSecondary
    }
}