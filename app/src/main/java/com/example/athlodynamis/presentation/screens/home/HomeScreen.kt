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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.data.remote.dto.UserDto
import com.example.athlodynamis.data.repository.MatchRepository
import com.example.athlodynamis.data.repository.PlayerRepository
import com.example.athlodynamis.data.repository.StatsRepository
import com.example.athlodynamis.data.repository.TeamRepository
import com.example.athlodynamis.data.repository.TournamentRepository
import com.example.athlodynamis.data.repository.UserRepository
import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.domain.model.Team
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen

data class DashboardStat(
    val value: String,
    val label: String
)

@Composable
fun HomeScreen(
    navController: NavController,
    userRole: AthloUserRole,
    userName: String = "Utilizador",
    userId: String,
    playerTeamId: Int? = null
) {
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
                    AthloUserRole.ADMIN -> AdminHomeContent(
                        navController = navController,
                        userName = userName
                    )

                    AthloUserRole.ORGANIZER -> OrganizerHomeContent(
                        navController = navController,
                        userName = userName,
                        userId = userId
                    )

                    AthloUserRole.PLAYER -> PlayerHomeContent(
                        navController = navController,
                        userName = userName,
                        userId = userId,
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
    navController: NavController,
    userName: String
) {
    var users by remember {
        mutableStateOf<List<UserDto>>(emptyList())
    }

    var tournamentsCount by remember {
        mutableStateOf(0)
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
            users = UserRepository().getAllUsers()
            tournamentsCount = TournamentRepository().getTournaments().size
        } catch (e: Exception) {
            errorMessage = e.message ?: "Erro ao carregar dados do administrador."
        } finally {
            isLoading = false
        }
    }

    val organizersCount = users.count {
        it.role.equals("ORGANIZER", ignoreCase = true)
    }

    val playersCount = users.count {
        it.role.equals("PLAYER", ignoreCase = true)
    }

    val adminsCount = users.count {
        it.role.equals("ADMIN", ignoreCase = true)
    }

    val pendingRequestsCount = users.count {
        it.role.equals("ORGANIZER", ignoreCase = true) &&
                it.approvalStatus.equals("PENDING", ignoreCase = true)
    }

    val recentUsers = users
        .sortedByDescending { it.createdAt ?: "" }
        .take(4)

    val adminInitials = userName.initials().ifBlank { "AD" }

    DashboardHeader(
        name = userName.ifBlank { "Administrador" },
        initials = adminInitials,
        stats = listOf(
            DashboardStat(
                value = if (isLoading) "..." else tournamentsCount.toString(),
                label = "Eventos"
            ),
            DashboardStat(
                value = if (isLoading) "..." else users.size.toString(),
                label = "Utilizadores"
            ),
            DashboardStat(
                value = if (isLoading) "..." else organizersCount.toString(),
                label = "Organizadores"
            )
        ),
        showAdminBadge = true,
        onProfileClick = {
            navController.navigate(Screen.Profile.route)
        }
    )

    Spacer(modifier = Modifier.height(28.dp))

    if (errorMessage != null) {
        AdminErrorCard(
            text = errorMessage ?: "Erro ao carregar dados."
        )

        Spacer(modifier = Modifier.height(22.dp))
    }

    PendingRequestsCard(
        pendingCount = pendingRequestsCount,
        onClick = {
            navController.navigate(Screen.PendingRequests.route)
        }
    )

    Spacer(modifier = Modifier.height(26.dp))

    SectionTitle(title = "Resumo da plataforma")

    Spacer(modifier = Modifier.height(8.dp))

    AdminPlatformSummaryCard(
        playersCount = playersCount,
        organizersCount = organizersCount,
        adminsCount = adminsCount,
        tournamentsCount = tournamentsCount,
        isLoading = isLoading
    )

    Spacer(modifier = Modifier.height(26.dp))

    SectionTitle(title = "Últimos registos")

    Spacer(modifier = Modifier.height(8.dp))

    RecentRegistrationsCard(
        users = recentUsers,
        isLoading = isLoading
    )
}

@Composable
private fun PendingRequestsCard(
    pendingCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = if (pendingCount == 1) {
                        "1 pedido pendente"
                    } else {
                        "$pendingCount pedidos pendentes"
                    },
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

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Abrir pedidos pendentes",
                tint = Color(0xFFB48A00),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun AdminPlatformSummaryCard(
    playersCount: Int,
    organizersCount: Int,
    adminsCount: Int,
    tournamentsCount: Int,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AdminSummaryRow(
                label = "Jogadores",
                value = if (isLoading) "..." else playersCount.toString(),
                color = Color(0xFFD7EBFF)
            )

            AdminSummaryRow(
                label = "Organizadores",
                value = if (isLoading) "..." else organizersCount.toString(),
                color = Color(0xFFDFF3D8)
            )

            AdminSummaryRow(
                label = "Administradores",
                value = if (isLoading) "..." else adminsCount.toString(),
                color = Color(0xFFFFF7CC)
            )

            AdminSummaryRow(
                label = "Torneios/Eventos",
                value = if (isLoading) "..." else tournamentsCount.toString(),
                color = Color(0xFFE3D7FF)
            )
        }
    }
}

@Composable
private fun AdminSummaryRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                color = AthloColors.Navy,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Text(
            text = label,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
private fun AdminErrorCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE5E5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFCC1F2F),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(18.dp)
        )
    }
}

@Composable
private fun RecentRegistrationsCard(
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

/* ---------------------------------------------------------
   ORGANIZER HOME
--------------------------------------------------------- */

@Composable
private fun OrganizerHomeContent(
    navController: NavController,
    userName: String,
    userId: String
) {
    var tournaments by remember { mutableStateOf<List<Tournament>>(emptyList()) }
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var athletesCount by remember { mutableStateOf(0) }

    LaunchedEffect(userId) {
        tournaments = TournamentRepository().getTournaments()
        matches = MatchRepository().getAllMatches()
        athletesCount = PlayerRepository().getAllPlayers().size
    }

    val myEvents = tournaments.filter { tournament ->
        tournament.organizerId == userId
    }

    val otherEvents = tournaments.filter { tournament ->
        tournament.organizerId != userId
    }

    val myEventIds = myEvents.map { it.id }.toSet()

    val myMatches = matches.filter { match ->
        match.tournamentId.toString() in myEventIds
    }

    val activeTournaments = myEvents.count {
        !it.status.equals("Terminado", ignoreCase = true)
    }

    val activeMatches = myMatches.count {
        it.status.equals("Agendado", ignoreCase = true) ||
                it.status.equals("A decorrer", ignoreCase = true)
    }

    val liveMatch = myMatches.firstOrNull {
        it.status.equals("A decorrer", ignoreCase = true)
    }

    val organizerInitials = userName.initials().ifBlank { "ORG" }

    DashboardHeader(
        name = userName.ifBlank { "Organizador" },
        initials = organizerInitials,
        stats = listOf(
            DashboardStat(activeTournaments.toString(), "Torneios ativos"),
            DashboardStat(activeMatches.toString(), "Jogos ativos"),
            DashboardStat(athletesCount.toString(), "Atletas")
        ),
        showAdminBadge = false,
        onProfileClick = {
            navController.navigate(Screen.Profile.route)
        }
    )

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = "Ao vivo")

    Spacer(modifier = Modifier.height(8.dp))

    if (liveMatch == null) {
        EmptyLiveMatchCard()
    } else {
        LiveMatchCard(
            time = liveMatch.matchTime ?: "Hora por definir",
            teamA = liveMatch.teamAName,
            teamB = liveMatch.teamBName,
            scoreA = liveMatch.scoreA.toString(),
            scoreB = liveMatch.scoreB.toString(),
            status = liveMatch.status,
            minute = "${liveMatch.minute ?: 0}'",
            onClick = {
                navController.navigate(
                    Screen.MatchDetail.createRoute(liveMatch.id.toString())
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = "Os meus eventos")

    Spacer(modifier = Modifier.height(8.dp))

    if (myEvents.isEmpty()) {
        EmptyOrganizerEventsCard()
    } else {
        myEvents.forEach { tournament ->
            EventCard(
                date = tournament.dateRange,
                title = tournament.name,
                tags = listOf(
                    tournament.sport,
                    tournament.status,
                    tournament.format
                ),
                onClick = {
                    navController.navigate(
                        Screen.TournamentDetail.createRoute(tournament.id)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    SectionTitle(title = "Outros eventos")

    Spacer(modifier = Modifier.height(8.dp))

    if (otherEvents.isEmpty()) {
        EmptyOtherEventsCard()
    } else {
        otherEvents.take(5).forEach { tournament ->
            EventCard(
                date = tournament.dateRange,
                title = tournament.name,
                tags = listOf(
                    tournament.sport,
                    tournament.status,
                    tournament.format
                ),
                onClick = {
                    navController.navigate(
                        Screen.TournamentDetail.createRoute(tournament.id)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/* ---------------------------------------------------------
   PLAYER HOME
--------------------------------------------------------- */

@Composable
private fun PlayerHomeContent(
    navController: NavController,
    userName: String,
    userId: String,
    playerTeamId: Int?
) {
    val initials = userName.initials().ifBlank { "J" }

    var playerTeam by remember {
        mutableStateOf<Team?>(null)
    }

    var playerMatches by remember {
        mutableStateOf<List<Match>>(emptyList())
    }

    var nextMatch by remember {
        mutableStateOf<Match?>(null)
    }

    var playerGoals by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(playerTeamId) {
        if (playerTeamId != null) {
            TeamRepository.fetchTeamsFromSupabase()
            playerTeam = TeamRepository.getTeamById(playerTeamId)

            val matches = MatchRepository().getMatchesByTeamId(playerTeamId)

            playerMatches = matches

            nextMatch = matches
                .filter { match ->
                    match.status.equals("Agendado", ignoreCase = true) ||
                            match.status.equals("A decorrer", ignoreCase = true)
                }
                .sortedBy { it.id }
                .firstOrNull()
        } else {
            playerTeam = null
            playerMatches = emptyList()
            nextMatch = null
        }

        if (userId.isNotBlank()) {
            val stats = StatsRepository().getPlayerStatsByUserId(userId)
            playerGoals = stats.goals
        }
    }

    DashboardHeader(
        name = userName,
        initials = initials,
        stats = listOf(
            DashboardStat(
                playerMatches.count {
                    it.status.equals("Agendado", ignoreCase = true) ||
                            it.status.equals("A decorrer", ignoreCase = true)
                }.toString(),
                "Próximos jogos"
            ),
            DashboardStat(playerGoals.toString(), "Golos"),
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

        if (nextMatch == null) {
            EmptyNextMatchCard()
        } else {
            PlayerNextMatchCard(
                match = nextMatch!!,
                onClick = {
                    navController.navigate(
                        Screen.MatchDetail.createRoute(nextMatch!!.id.toString())
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        SectionTitle(title = "As minhas equipas")

        Spacer(modifier = Modifier.height(8.dp))

        TeamCard(
            acronym = playerTeam?.acronym ?: "EQP",
            name = playerTeam?.name ?: "Equipa $playerTeamId",
            sport = playerTeam?.sport ?: "Equipa associada",
            status = "Inscrito",
            statusColor = AthloColors.SoftBlue,
            acronymColor = AthloColors.InfoBg,
            onClick = {
                playerTeamId?.let {
                    navController.navigate(Screen.TeamDetail.createRoute(it))
                }
            }
        )
    }
}

@Composable
private fun PlayerNextMatchCard(
    match: Match,
    onClick: () -> Unit
) {
    val timeText = match.matchTime?.ifBlank { null } ?: "Hora por definir"

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
                text = timeText,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamMiniBadge(
                    label = match.teamAName.toAcronym(),
                    color = Color(0xFFFFEFD7)
                )

                Text(
                    text = match.teamAName,
                    fontWeight = FontWeight.Bold,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )

                ScoreBox(score = match.scoreA.toString())

                Text(
                    text = "-",
                    color = AthloColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                ScoreBox(score = match.scoreB.toString())

                Text(
                    text = match.teamBName,
                    fontWeight = FontWeight.Bold,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            StatusPill(
                text = match.status,
                background = if (match.status.equals("A decorrer", ignoreCase = true)) {
                    AthloColors.DangerBg
                } else {
                    AthloColors.NeutralBg
                },
                textColor = if (match.status.equals("A decorrer", ignoreCase = true)) {
                    Color(0xFFC83755)
                } else {
                    AthloColors.TextSecondary
                }
            )
        }
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
    acronymColor: Color,
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

@Composable
private fun EmptyLiveMatchCard() {
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
                contentDescription = "Sem jogos ao vivo",
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sem jogos ao vivo",
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Quando algum jogo estiver a decorrer, aparece aqui.",
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyOrganizerEventsCard() {
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
                contentDescription = "Sem eventos",
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sem eventos criados",
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Quando criares torneios, eles aparecem aqui.",
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
private fun EmptyOtherEventsCard() {
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
                contentDescription = "Sem outros eventos",
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sem outros eventos",
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Quando existirem eventos criados por outros organizadores, eles aparecem aqui.",
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
private fun String.toAcronym(): String {
    val parts = trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        parts.isEmpty() -> "EQP"
        parts.size == 1 -> parts.first().take(3).uppercase()
        else -> parts.take(2).joinToString("") {
            it.first().uppercase()
        }
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