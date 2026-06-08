package com.example.athlodynamis.presentation.screens.management

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch

@Composable
fun PendingRequestsScreen(
    navController: NavController
) {
    val repository = remember { UserRepository() }
    val coroutineScope = rememberCoroutineScope()

    var pendingRequests by remember {
        mutableStateOf<List<UserDto>>(emptyList())
    }

    var historyRequests by remember {
        mutableStateOf<List<UserDto>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    fun refreshRequests() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val users = repository.getAllUsers()

                pendingRequests = users
                    .filter {
                        it.role.equals("ORGANIZER", ignoreCase = true) &&
                                it.approvalStatus.equals("PENDING", ignoreCase = true)
                    }
                    .sortedBy { it.createdAt ?: "" }

                historyRequests = users
                    .filter {
                        it.role.equals("ORGANIZER", ignoreCase = true) &&
                                (
                                        it.approvalStatus.equals("APPROVED", ignoreCase = true) ||
                                                it.approvalStatus.equals("REJECTED", ignoreCase = true)
                                        )
                    }
                    .sortedByDescending { it.createdAt ?: "" }

            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro ao carregar pedidos."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshRequests()
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                PendingRequestsHeader(
                    pendingCount = pendingRequests.size,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            when {
                isLoading -> {
                    item {
                        InfoCard(
                            text = "A carregar pedidos..."
                        )
                    }
                }

                errorMessage != null -> {
                    item {
                        InfoCard(
                            text = errorMessage ?: "Erro ao carregar pedidos."
                        )
                    }
                }

                else -> {
                    if (pendingRequests.isEmpty()) {
                        item {
                            EmptyPendingRequestsCard()
                        }
                    } else {
                        item {
                            OldestRequestWarning(
                                pendingCount = pendingRequests.size
                            )
                        }

                        items(pendingRequests.size) { index ->
                            val request = pendingRequests[index]

                            PendingRequestCard(
                                request = request,
                                highlighted = index == 0,
                                onApprove = {
                                    coroutineScope.launch {
                                        isLoading = true
                                        errorMessage = null

                                        try {
                                            repository.updateOrganizerApproval(
                                                userId = request.id,
                                                approvalStatus = "APPROVED"
                                            )

                                            refreshRequests()
                                        } catch (e: Exception) {
                                            errorMessage = e.message ?: "Erro ao aprovar pedido."
                                            isLoading = false
                                        }
                                    }
                                },
                                onReject = {
                                    coroutineScope.launch {
                                        isLoading = true
                                        errorMessage = null

                                        try {
                                            repository.updateOrganizerApproval(
                                                userId = request.id,
                                                approvalStatus = "REJECTED"
                                            )

                                            refreshRequests()
                                        } catch (e: Exception) {
                                            errorMessage = e.message ?: "Erro ao rejeitar pedido."
                                            isLoading = false
                                        }
                                    }
                                }
                            )
                        }
                    }

                    item {
                        SectionTitle(
                            title = "Histórico"
                        )
                    }

                    if (historyRequests.isEmpty()) {
                        item {
                            EmptyHistoryCard()
                        }
                    } else {
                        items(historyRequests.size) { index ->
                            val request = historyRequests[index]

                            HistoryRequestCard(
                                request = request
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingRequestsHeader(
    pendingCount: Int,
    onBackClick: () -> Unit
) {
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
                .padding(horizontal = 22.dp, vertical = 26.dp)
        ) {
            Column {
                Text(
                    text = "‹ voltar",
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onBackClick()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pedidos Pendentes",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (pendingCount) {
                        0 -> "Nenhum pedido a aguardar"
                        1 -> "1 pedido a aguardar revisão"
                        else -> "$pendingCount pedidos a aguardar revisão"
                    },
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
private fun InfoCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun EmptyPendingRequestsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(AthloColors.SuccessBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sem pedidos",
                    tint = Color(0xFF3F7A28),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sem pedidos pendentes",
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Quando alguém pedir para ser organizador, o pedido aparece aqui.",
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun EmptyHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Text(
            text = "Ainda não existe histórico de pedidos aprovados ou rejeitados.",
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun OldestRequestWarning(
    pendingCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7CC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFFFD928), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Aviso",
                    tint = Color(0xFF7A5B00),
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = if (pendingCount == 1) {
                        "Existe 1 pedido por rever"
                    } else {
                        "Existem $pendingCount pedidos por rever"
                    },
                    color = Color(0xFF7A5B00),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Aprova ou rejeita para manter a plataforma organizada",
                    color = Color(0xFFB48A00),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun PendingRequestCard(
    request: UserDto,
    highlighted: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val borderColor = if (highlighted) {
        Color(0xFFFFD928)
    } else {
        Color.Transparent
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            width = if (highlighted) 1.5.dp else 0.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(request.role.roleColor(), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = request.name.initials(),
                        color = AthloColors.Navy,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp)
                ) {
                    Text(
                        text = request.name,
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = request.email,
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Box(
                    modifier = Modifier
                        .background(AthloColors.NeutralBg, RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Pendente",
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEAF4FF), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Mensagem do candidato",
                        color = AthloColors.Navy,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (request.organizerRequestMessage.isNullOrBlank()) {
                            "Sem descrição enviada."
                        } else {
                            "\"${request.organizerRequestMessage}\""
                        },
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFD01E1E)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Rejeitar",
                        tint = Color(0xFFD01E1E),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Rejeitar",
                        color = Color(0xFFD01E1E),
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AthloColors.Blue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Aprovar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Aprovar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRequestCard(
    request: UserDto
) {
    val approved = request.approvalStatus.equals("APPROVED", ignoreCase = true)

    val statusText = if (approved) {
        "Aprovado"
    } else {
        "Rejeitado"
    }

    val statusBackground = if (approved) {
        AthloColors.SuccessBg
    } else {
        Color(0xFFFFE5E5)
    }

    val statusColor = if (approved) {
        Color(0xFF3F7A28)
    } else {
        Color(0xFFD01E1E)
    }

    val statusIcon = if (approved) {
        Icons.Default.Check
    } else {
        Icons.Default.Close
    }

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
                    .size(46.dp)
                    .background(statusBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusText,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = request.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = request.email,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Box(
                modifier = Modifier
                    .background(statusBackground, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String
) {
    Text(
        text = title,
        color = AthloColors.TextSecondary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    )
}

@Composable
private fun AdminBadge(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color(0xFFFFD928), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Admin",
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "ADMIN",
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
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