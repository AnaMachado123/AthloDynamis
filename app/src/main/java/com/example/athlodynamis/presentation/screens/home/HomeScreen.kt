package com.example.athlodynamis.presentation.screens.home

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.athlodynamis.data.repository.TeamRepository
import com.example.athlodynamis.domain.model.Team
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
data class DashboardStat(
    val value: String,
    val label: String
)

data class RecentUser(
    val position: Int,
    val initials: String,
    val name: String,
    val time: String,
    val color: Color
)

@Composable
fun HomeScreen(
    navController: NavController,
    userRole: AthloUserRole,
    userName: String = "Utilizador",
    playerTeamId: Int? = null
){
    Scaffold(
        containerColor = AthloColors.Background,
        floatingActionButton = {
            if (userRole == AthloUserRole.ORGANIZER) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.CreateTeam.route)
                    },
                    containerColor = AthloColors.Blue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Criar equipa"
                    )
                }
            }
        },
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Home.route,
                userRole = userRole
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
                Spacer(modifier = Modifier.height(6.dp))

                when (userRole) {
                    AthloUserRole.ADMIN -> AdminHomeContent(navController = navController)
                    AthloUserRole.ORGANIZER -> OrganizerHomeContent(navController = navController)
                    AthloUserRole.PLAYER -> PlayerHomeContent(
                        navController = navController,
                        userName = userName,
                        playerTeamId = playerTeamId
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

/* ---------------------------------------------------------
   ADMIN HOME
--------------------------------------------------------- */

@Composable
private fun AdminHomeContent(
    navController: NavController
) {
    DashboardHeader(
        name = "Gonçalo Magalhães",
        initials = "GM",
        stats = listOf(
            DashboardStat("24", "Eventos"),
            DashboardStat("1.2K", "Utilizadores"),
            DashboardStat("18", "Organizadores")
        ),
        showAdminBadge = true,
        onProfileClick = {
            navController.navigate(Screen.Profile.route)
        }
    )

    Spacer(modifier = Modifier.height(28.dp))

    PendingRequestsCard()

    Spacer(modifier = Modifier.height(26.dp))

    SectionTitle(title = "Últimos registos")

    Spacer(modifier = Modifier.height(8.dp))

    RecentRegistrationsCard()
}

@Composable
private fun PendingRequestsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7CC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFFFD928), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Pedidos pendentes",
                    tint = Color(0xFF6B5A00),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "3 pedidos pendentes",
                    color = Color(0xFF7A5B00),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Aprovação de organizadores",
                    color = Color(0xFFB48A00),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun RecentRegistrationsCard() {
    val users = listOf(
        RecentUser(1, "JS", "João Santos", "há 2h", Color(0xFFD7EBFF)),
        RecentUser(2, "CS", "Carlos Silva", "há 3h", Color(0xFFF8FFB0)),
        RecentUser(3, "MP", "Miguel Pinto", "há 8h", Color(0xFFDFF3D8)),
        RecentUser(4, "AF", "Ana Ferreira", "há 1d", Color(0xFFE3D7FF))
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            users.forEachIndexed { index, user ->
                RecentUserRow(user = user)

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

@Composable
private fun RecentUserRow(user: RecentUser) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.position.toString(),
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(28.dp)
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(user.color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.initials,
                color = AthloColors.Blue,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Column(
            modifier = Modifier.padding(start = 14.dp)
        ) {
            Text(
                text = user.name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = user.time,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

/* ---------------------------------------------------------
   ORGANIZER HOME
--------------------------------------------------------- */

@Composable
private fun OrganizerHomeContent(
    navController: NavController
) {
    DashboardHeader(
        name = "Gonçalo Magalhães",
        initials = "GM",
        stats = listOf(
            DashboardStat("6", "Torneios ativos"),
            DashboardStat("3", "Jogos hoje"),
            DashboardStat("348", "Atletas")
        ),
        showAdminBadge = false,
        onProfileClick = {
            navController.navigate(Screen.Profile.route)
        }
    )

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = "Ao vivo")

    Spacer(modifier = Modifier.height(8.dp))

    LiveMatchCard(
        time = "12:00",
        teamA = "Equipa 3",
        teamB = "Equipa 4",
        scoreA = "2",
        scoreB = "2",
        status = "A decorrer",
        minute = "33'",
        onClick = {
            navController.navigate(Screen.MatchDetail.createRoute("2"))
        }
    )

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = "Os meus eventos")

    Spacer(modifier = Modifier.height(8.dp))

    EventCard(
        date = "10 abr - 25 abr",
        title = "Torneio de Braga",
        tags = listOf("Futebol", "A decorrer", "Grupos"),
        onClick = {
            navController.navigate(Screen.TournamentDetail.createRoute("2"))
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    EventCard(
        date = "22 jul - 25 jul",
        title = "Torneio Regional Basquetebol",
        tags = listOf("Basquetebol", "Em preparação", "Eliminatórias"),
        onClick = {
            navController.navigate(Screen.TournamentDetail.createRoute("3"))
        }
    )
}

/* ---------------------------------------------------------
   PLAYER HOME
--------------------------------------------------------- */
@Composable
private fun PlayerHomeContent(
    navController: NavController,
    userName: String,
    playerTeamId: Int?
) {
    val initials = userName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
    var playerTeam by remember {
        mutableStateOf<Team?>(null)
    }

    LaunchedEffect(playerTeamId) {
        if (playerTeamId != null) {
            TeamRepository.fetchTeamsFromSupabase()
            playerTeam = TeamRepository.getTeamById(playerTeamId)
        } else {
            playerTeam = null
        }
    }

    DashboardHeader(
        name = userName,
        initials = initials.ifBlank { "J" },
        stats = listOf(
            DashboardStat("0", "Próximos jogos"),
            DashboardStat("0", "Golos"),
            DashboardStat("0", "Troféus")
        ),
        showAdminBadge = false,
        onProfileClick = {
            navController.navigate(Screen.Profile.route)
        }
    )

    Spacer(modifier = Modifier.height(22.dp))

    if (playerTeamId == null) {
        EmptyPlayerHomeCard()
    } else {
        SectionTitle(title = "Próximo jogo")

        Spacer(modifier = Modifier.height(8.dp))

        EmptyNextMatchCard()

        Spacer(modifier = Modifier.height(22.dp))

        SectionTitle(title = "As minhas equipas")

        Spacer(modifier = Modifier.height(8.dp))

        TeamCard(
            acronym = playerTeam?.acronym ?: "EQP",
            name = playerTeam?.name ?: "Equipa $playerTeamId",
            sport = playerTeam?.sport ?: "Equipa associada",
            status = "Inscrito",
            statusColor = AthloColors.SoftBlue,
            acronymColor = AthloColors.InfoBg
        )
    }
}

/* ---------------------------------------------------------
   SHARED COMPONENTS
--------------------------------------------------------- */
@Composable
private fun EmptyPlayerHomeCard() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionTitle(title = "O meu perfil desportivo")

        Spacer(modifier = Modifier.height(8.dp))

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
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Sem equipa",
                    tint = AthloColors.TextMuted,
                    modifier = Modifier.size(58.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sem equipa associada",
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ainda não foste adicionado a nenhuma equipa. Quando fores associado, os teus jogos, estatísticas e equipas aparecem aqui.",
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}
@Composable
private fun EmptyNextMatchCard() {
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
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Sem próximo jogo",
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Sem jogos agendados",
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Quando houver um jogo marcado para a tua equipa, ele aparece aqui.",
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
private fun EmptyStatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = AthloColors.Blue,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = label,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
private fun DashboardHeader(
    name: String,
    initials: String,
    stats: List<DashboardStat>,
    showAdminBadge: Boolean,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AthloColors.Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
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

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )

                        if (showAdminBadge) {
                            Spacer(modifier = Modifier.width(8.dp))

                            AdminBadge()
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(AthloColors.Blue, CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                stats.forEach { stat ->
                    StatBox(
                        stat = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminBadge() {
    Row(
        modifier = Modifier
            .background(Color(0xFFFFD928), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Admin",
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(13.dp)
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

@Composable
private fun StatBox(
    stat: DashboardStat,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(78.dp)
            .background(
                color = Color(0xFF244A70),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stat.value,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = stat.label,
                color = Color(0xFFC8DCEF),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AthloColors.TextSecondary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun LiveMatchCard(
    time: String,
    teamA: String,
    teamB: String,
    scoreA: String,
    scoreB: String,
    status: String,
    minute: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = time,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamMiniBadge(
                    label = "EQP",
                    color = Color(0xFFFFEFD7)
                )

                Text(
                    text = teamA,
                    fontWeight = FontWeight.Bold,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )

                ScoreBox(score = scoreA)

                Text(
                    text = "-",
                    color = AthloColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                ScoreBox(score = scoreB)

                Text(
                    text = teamB,
                    fontWeight = FontWeight.Bold,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusPill(
                    text = status,
                    background = if (status == "A decorrer") {
                        AthloColors.DangerBg
                    } else {
                        AthloColors.NeutralBg
                    },
                    textColor = if (status == "A decorrer") {
                        Color(0xFFC83755)
                    } else {
                        AthloColors.TextSecondary
                    }
                )

                if (minute.isNotBlank()) {
                    Text(
                        text = minute,
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    date: String,
    title: String,
    tags: List<String>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = date,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEachIndexed { index, tag ->
                        val background = when (index) {
                            0 -> AthloColors.SuccessBg
                            1 -> AthloColors.InfoBg
                            else -> AthloColors.NeutralBg
                        }

                        StatusPill(
                            text = tag,
                            background = background,
                            textColor = AthloColors.TextSecondary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(AthloColors.SoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Abrir evento",
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TeamCard(
    acronym: String,
    name: String,
    sport: String,
    status: String,
    statusColor: Color,
    acronymColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(acronymColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = acronym,
                    color = AthloColors.Blue,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = name,
                    color = AthloColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = sport,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            StatusPill(
                text = status,
                background = statusColor,
                textColor = if (status == "A decorrer") {
                    Color(0xFFC83755)
                } else {
                    AthloColors.Blue
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(AthloColors.SoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Abrir equipa",
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun TeamMiniBadge(
    label: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = AthloColors.Blue,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ScoreBox(score: String) {
    Box(
        modifier = Modifier
            .background(AthloColors.NeutralBg, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = score,
            color = AthloColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusPill(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}