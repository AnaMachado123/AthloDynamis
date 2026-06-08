package com.example.athlodynamis.presentation.screens.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
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
import com.example.athlodynamis.data.repository.TournamentRepository
import com.example.athlodynamis.data.repository.UserRepository
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen

data class AdminManagementStats(
    val eventsCount: Int = 0,
    val usersCount: Int = 0,
    val organizersCount: Int = 0,
    val playersCount: Int = 0,
    val adminsCount: Int = 0,
    val pendingRequestsCount: Int = 0
)

@Composable
fun ManagementScreen(navController: NavController) {
    var stats by remember {
        mutableStateOf(AdminManagementStats())
    }

    var recentUsers by remember {
        mutableStateOf<List<UserDto>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null

        try {
            val users = UserRepository().getAllUsers()
            val tournaments = TournamentRepository().getTournaments()

            val organizers = users.count {
                it.role.equals("ORGANIZER", ignoreCase = true)
            }

            val players = users.count {
                it.role.equals("PLAYER", ignoreCase = true)
            }

            val admins = users.count {
                it.role.equals("ADMIN", ignoreCase = true)
            }

            val pendingRequests = users.count {
                it.role.equals("ORGANIZER", ignoreCase = true) &&
                        it.approvalStatus.equals("PENDING", ignoreCase = true)
            }

            stats = AdminManagementStats(
                eventsCount = tournaments.size,
                usersCount = users.size,
                organizersCount = organizers,
                playersCount = players,
                adminsCount = admins,
                pendingRequestsCount = pendingRequests
            )

            recentUsers = users
                .sortedByDescending { it.createdAt ?: "" }
                .take(4)

        } catch (e: Exception) {
            errorMessage = e.message ?: "Erro ao carregar dados da gestão."
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
            verticalArrangement = Arrangement.spacedBy(22.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                AdminManagementHeader(
                    stats = stats,
                    isLoading = isLoading
                )
            }

            if (errorMessage != null) {
                item {
                    InfoCard(
                        text = errorMessage ?: "Erro ao carregar dados."
                    )
                }
            }

            item {
                SectionTitle("Acessos rápidos")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    QuickAccessCard(
                        icon = Icons.Default.PersonAdd,
                        iconBackground = Color(0xFFFFD928),
                        iconTint = Color(0xFF7A5B00),
                        title = "Pedidos pendentes",
                        subtitle = "${stats.pendingRequestsCount} a aguardar",
                        counterText = stats.pendingRequestsCount.toString(),
                        showCounter = stats.pendingRequestsCount > 0,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Screen.PendingRequests.route)
                        }
                    )

                    QuickAccessCard(
                        icon = Icons.Default.Groups,
                        iconBackground = Color(0xFFD7EBFF),
                        iconTint = AthloColors.Blue,
                        title = "Utilizadores",
                        subtitle = "${stats.usersCount} registados",
                        counterText = stats.usersCount.toString(),
                        showCounter = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    QuickAccessCard(
                        icon = Icons.Default.SupervisorAccount,
                        iconBackground = Color(0xFFDFF3D8),
                        iconTint = Color(0xFF4D8B4A),
                        title = "Organizadores",
                        subtitle = "${stats.organizersCount} registados",
                        counterText = stats.organizersCount.toString(),
                        showCounter = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )

                    QuickAccessCard(
                        icon = Icons.Default.Groups,
                        iconBackground = Color(0xFFE3D7FF),
                        iconTint = Color(0xFF6A3FCB),
                        title = "Jogadores",
                        subtitle = "${stats.playersCount} registados",
                        counterText = stats.playersCount.toString(),
                        showCounter = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }
            }

            item {
                SectionTitle("Eventos")
            }

            item {
                EventsAccessCard(
                    eventsCount = stats.eventsCount,
                    onClick = {
                        navController.navigate(Screen.Events.route)
                    }
                )
            }

            item {
                SectionTitle("Últimos utilizadores")
            }

            item {
                RecentUsersCard(
                    users = recentUsers,
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
private fun AdminManagementHeader(
    stats: AdminManagementStats,
    isLoading: Boolean
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Bom dia",
                        color = Color(0xFFBBD7EF),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Administrador",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminBadge()

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(AthloColors.Blue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AD",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatBox(
                    value = if (isLoading) "..." else stats.eventsCount.toString(),
                    label = "Eventos",
                    modifier = Modifier.weight(1f)
                )

                AdminStatBox(
                    value = if (isLoading) "..." else stats.usersCount.toString(),
                    label = "Utilizadores",
                    modifier = Modifier.weight(1f)
                )

                AdminStatBox(
                    value = if (isLoading) "..." else stats.organizersCount.toString(),
                    label = "Organizadores",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AdminStatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(86.dp)
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
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = Color(0xFFC8DCEF),
                style = MaterialTheme.typography.labelSmall
            )
        }
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
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AthloColors.TextSecondary,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun QuickAccessCard(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    counterText: String,
    showCounter: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(128.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(iconBackground, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (showCounter) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color(0xFFE64545), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = counterText,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun EventsAccessCard(
    eventsCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(Color(0xFFE3D7FF), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "Eventos",
                    tint = Color(0xFF6A3FCB),
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Ver todos os eventos",
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$eventsCount eventos",
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Abrir eventos",
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun RecentUsersCard(
    users: List<UserDto>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            when {
                isLoading -> {
                    Text(
                        text = "A carregar utilizadores...",
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                users.isEmpty() -> {
                    Text(
                        text = "Ainda não existem utilizadores registados.",
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    users.forEachIndexed { index, user ->
                        RecentUserRow(
                            position = index + 1,
                            user = user
                        )

                        if (index < users.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFE5E7EB))
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentUserRow(
    position: Int,
    user: UserDto
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = position.toString(),
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(28.dp)
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(user.role.roleColor(), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.initials(),
                color = AthloColors.Blue,
                style = MaterialTheme.typography.labelMedium,
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
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = user.email,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Box(
            modifier = Modifier
                .background(AthloColors.NeutralBg, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.role,
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color(0xFFFFD928), RoundedCornerShape(999.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Admin",
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(12.dp)
        )

        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = "ADMIN",
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
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

private fun String.roleColor(): Color {
    return when {
        equals("ADMIN", ignoreCase = true) -> Color(0xFFFFD928)
        equals("ORGANIZER", ignoreCase = true) -> Color(0xFFDFF3D8)
        equals("PLAYER", ignoreCase = true) -> Color(0xFFD7EBFF)
        else -> Color(0xFFE3D7FF)
    }
}