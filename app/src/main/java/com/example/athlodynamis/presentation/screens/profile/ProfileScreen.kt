package com.example.athlodynamis.presentation.screens.profile

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.data.repository.MatchRepository
import com.example.athlodynamis.data.repository.OrganizerStatsRepository
import com.example.athlodynamis.data.repository.PlayerRepository
import com.example.athlodynamis.data.repository.StatsRepository
import com.example.athlodynamis.data.repository.TeamRepository
import com.example.athlodynamis.data.repository.TournamentRepository
import com.example.athlodynamis.domain.model.OrganizerStatsData
import com.example.athlodynamis.domain.model.PlayerStatsData
import com.example.athlodynamis.domain.model.Team
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.presentation.components.AthloBackButton
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloLogoutButton
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.OfflineViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    userRole: AthloUserRole,
    userName: String,
    userEmail: String,
    playerTeamId: Int?,
    userId: String,
    onLogoutClick: () -> Unit
) {
    val offlineViewModel: OfflineViewModel = viewModel()
    val isOnline by offlineViewModel.isOnline.collectAsState()

    var organizerTournaments by remember {
        mutableStateOf<List<Tournament>>(emptyList())
    }

    var organizerStats by remember {
        mutableStateOf<OrganizerStatsData?>(null)
    }

    var playerStats by remember {
        mutableStateOf<PlayerStatsData?>(null)
    }

    var playerTeam by remember {
        mutableStateOf<Team?>(null)
    }

    var adminTournaments by remember {
        mutableStateOf<List<Tournament>>(emptyList())
    }

    var adminMatchesCount by remember {
        mutableStateOf(0)
    }

    var adminPlayersCount by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(userId, playerTeamId, userRole, isOnline) {
        organizerTournaments = emptyList()
        organizerStats = null
        playerStats = null
        playerTeam = null
        adminTournaments = emptyList()
        adminMatchesCount = 0
        adminPlayersCount = 0

        if (!isOnline) {
            return@LaunchedEffect
        }

        when (userRole) {
            AthloUserRole.PLAYER -> {
                if (userId.isNotBlank()) {
                    runCatching {
                        StatsRepository().getPlayerStatsByUserId(userId)
                    }.onSuccess { stats ->
                        playerStats = stats
                    }.onFailure {
                        playerStats = null
                    }

                    if (playerTeamId != null) {
                        runCatching {
                            TeamRepository.fetchTeamsFromSupabase()
                            TeamRepository.getTeamById(playerTeamId)
                        }.onSuccess { team ->
                            playerTeam = team
                        }.onFailure {
                            playerTeam = null
                        }
                    }
                }
            }

            AthloUserRole.ORGANIZER -> {
                runCatching {
                    OrganizerStatsRepository().getOrganizerStats()
                }.onSuccess { stats ->
                    organizerStats = stats
                }.onFailure {
                    organizerStats = null
                }

                runCatching {
                    TournamentRepository().getTournaments()
                }.onSuccess { tournaments ->
                    organizerTournaments = tournaments
                }.onFailure {
                    organizerTournaments = emptyList()
                }
            }

            AthloUserRole.ADMIN -> {
                runCatching {
                    TournamentRepository().getTournaments()
                }.onSuccess { tournaments ->
                    adminTournaments = tournaments
                }.onFailure {
                    adminTournaments = emptyList()
                }

                runCatching {
                    MatchRepository().getAllMatches().size
                }.onSuccess { count ->
                    adminMatchesCount = count
                }.onFailure {
                    adminMatchesCount = 0
                }

                runCatching {
                    PlayerRepository().getAllPlayers().size
                }.onSuccess { count ->
                    adminPlayersCount = count
                }.onFailure {
                    adminPlayersCount = 0
                }
            }
        }
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Profile.route,
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                ProfileHeader(
                    userRole = userRole,
                    userName = userName,
                    playerTeamId = playerTeamId,
                    playerStats = playerStats,
                    organizerStats = organizerStats,
                    adminEventsCount = adminTournaments.size,
                    adminMatchesCount = adminMatchesCount,
                    adminPlayersCount = adminPlayersCount,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditClick = {
                        navController.navigate(Screen.EditProfile.route)
                    },
                    onLogoutClick = onLogoutClick
                )
            }

            if (!isOnline) {
                item {
                    OfflineProfileCard()
                }
            }

            when (userRole) {
                AthloUserRole.PLAYER -> {
                    item {
                        PlayerProfileTabs(
                            playerTeamId = playerTeamId,
                            playerStats = playerStats,
                            playerTeam = playerTeam,
                            onLogoutClick = onLogoutClick
                        )
                    }
                }

                AthloUserRole.ORGANIZER -> {
                    item {
                        SectionTitle("Contacto")
                    }

                    item {
                        ContactCard(
                            email = userEmail
                        )
                    }

                    item {
                        SectionTitle("Eventos associados")
                    }

                    item {
                        if (organizerTournaments.isEmpty()) {
                            EmptyAssociatedEventsCard()
                        } else {
                            organizerTournaments.take(3).forEach { tournament ->
                                AssociatedEventCard(
                                    tournament = tournament,
                                    onClick = {
                                        navController.navigate(
                                            Screen.TournamentDetail.createRoute(tournament.id)
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    item {
                        LogoutButton(
                            onLogoutClick = onLogoutClick
                        )
                    }
                }

                AthloUserRole.ADMIN -> {
                    item {
                        SectionTitle("Contacto")
                    }

                    item {
                        ContactCard(
                            email = userEmail
                        )
                    }

                    item {
                        SectionTitle("Eventos associados")
                    }

                    item {
                        if (adminTournaments.isEmpty()) {
                            EmptyAssociatedEventsCard()
                        } else {
                            adminTournaments.take(3).forEach { tournament ->
                                AssociatedEventCard(
                                    tournament = tournament,
                                    onClick = {
                                        navController.navigate(
                                            Screen.TournamentDetail.createRoute(tournament.id)
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    item {
                        SuspendOrganizerButton(
                            onClick = {
                                navController.navigate(Screen.SuspendOrganizer.route)
                            }
                        )
                    }

                    item {
                        LogoutButton(
                            onLogoutClick = onLogoutClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userRole: AthloUserRole,
    userName: String,
    playerTeamId: Int?,
    playerStats: PlayerStatsData?,
    organizerStats: OrganizerStatsData?,
    adminEventsCount: Int,
    adminMatchesCount: Int,
    adminPlayersCount: Int,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val isAdmin = userRole == AthloUserRole.ADMIN
    val initials = userName
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "U" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .background(AthloColors.Navy)
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                AthloBackButton(
                    onClick = {
                        onBackClick()
                    }
                )

                if (isAdmin) {
                    AdminBadge()
                } else {
                    AthloLogoutButton(
                        color = Color(0xFF9CC8F2),
                        onClick = {
                            onLogoutClick()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Perfil",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(AthloColors.Blue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.White, CircleShape)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar perfil",
                        tint = AthloColors.Blue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            RolePill(userRole = userRole)

            Spacer(modifier = Modifier.height(22.dp))

            ProfileStatsRow(
                userRole = userRole,
                playerTeamId = playerTeamId,
                playerStats = playerStats,
                organizerStats = organizerStats,
                adminEventsCount = adminEventsCount,
                adminMatchesCount = adminMatchesCount,
                adminPlayersCount = adminPlayersCount
            )
        }
    }
}

@Composable
private fun ProfileStatsRow(
    userRole: AthloUserRole,
    playerTeamId: Int?,
    playerStats: PlayerStatsData?,
    organizerStats: OrganizerStatsData?,
    adminEventsCount: Int,
    adminMatchesCount: Int,
    adminPlayersCount: Int
) {
    val stats = when (userRole) {
        AthloUserRole.PLAYER -> listOf(
            (playerStats?.totalMatches ?: 0).toString() to "Jogos",
            (playerStats?.trophies ?: 0).toString() to "Troféus",
            (playerStats?.teams ?: if (playerTeamId != null) 1 else 0).toString() to "Equipas"
        )

        AthloUserRole.ORGANIZER -> listOf(
            (organizerStats?.tournaments ?: 0).toString() to "Eventos",
            (organizerStats?.matches ?: 0).toString() to "Jogos",
            (organizerStats?.athletes ?: 0).toString() to "Atletas"
        )

        AthloUserRole.ADMIN -> listOf(
            adminEventsCount.toString() to "Eventos",
            adminMatchesCount.toString() to "Jogos",
            adminPlayersCount.toString() to "Atletas"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF132A42), RoundedCornerShape(18.dp))
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        stats.forEachIndexed { index, stat ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stat.first,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = stat.second,
                    color = Color(0xFFC8DCEF),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (index < stats.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .width(1.dp)
                        .background(Color(0xFF365675))
                )
            }
        }
    }
}

@Composable
private fun RolePill(userRole: AthloUserRole) {
    val text = when (userRole) {
        AthloUserRole.PLAYER -> "Jogador"
        AthloUserRole.ORGANIZER -> "Organizador"
        AthloUserRole.ADMIN -> "Administrador"
    }

    Box(
        modifier = Modifier
            .background(Color(0xFF294F76), RoundedCornerShape(999.dp))
            .padding(horizontal = 24.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFC8DCEF),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PlayerProfileTabs(
    playerTeamId: Int?,
    playerStats: PlayerStatsData?,
    playerTeam: Team?,
    onLogoutClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Estatísticas") }
    val hasTeam = playerTeamId != null
    val hasMatches = !playerStats?.recentGames.isNullOrEmpty()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(18.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ProfileTabButton(
                text = "Estatísticas",
                selected = selectedTab == "Estatísticas",
                onClick = { selectedTab = "Estatísticas" },
                modifier = Modifier.weight(1f)
            )

            ProfileTabButton(
                text = "Equipas",
                selected = selectedTab == "Equipas",
                onClick = { selectedTab = "Equipas" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (selectedTab == "Estatísticas") {
            if (!hasMatches) {
                EmptyMatchesCard()
            } else {
                LastGamesCard(
                    games = playerStats?.recentGames ?: emptyList()
                )
            }
        } else {
            if (!hasTeam) {
                EmptyTeamsCard()
            } else {
                TeamsCard(
                    team = playerTeam
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        LogoutButton(
            onLogoutClick = onLogoutClick
        )
    }
}

@Composable
private fun ProfileTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (selected) AthloColors.SoftBlue else Color.White,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) AthloColors.Blue else AthloColors.TextPrimary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ContactCard(
    email: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(AthloColors.SoftBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Email",
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = email,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LastGamesCard(
    games: List<com.example.athlodynamis.domain.model.RecentGameData>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            SectionSmallTitle("ÚLTIMOS JOGOS")

            Spacer(modifier = Modifier.height(16.dp))

            games.forEach { game ->
                val colors = when (game.result) {
                    "V" -> AthloColors.SuccessBg to Color(0xFF3F7A28)
                    "D" -> AthloColors.DangerBg to Color(0xFFC83755)
                    else -> AthloColors.NeutralBg to AthloColors.TextSecondary
                }

                GameRow(
                    result = game.result,
                    opponent = game.matchTitle,
                    score = game.score,
                    subtitle = game.subtitle,
                    resultColor = colors.first,
                    textColor = colors.second
                )
            }
        }
    }
}

@Composable
private fun TeamsCard(
    team: Team?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            SectionSmallTitle("EQUIPAS INSCRITAS")

            Spacer(modifier = Modifier.height(16.dp))

            if (team == null) {
                Text(
                    text = "Equipa não encontrada.",
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                TeamProfileRow(
                    acronym = team.acronym,
                    name = team.name,
                    sport = team.sport,
                    status = "Inscrito",
                    statusColor = AthloColors.SuccessBg
                )
            }
        }
    }
}

@Composable
private fun TeamProfileRow(
    acronym: String,
    name: String,
    sport: String,
    status: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(AthloColors.SoftBlue, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = acronym,
                color = AthloColors.Blue,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = sport,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Box(
            modifier = Modifier
                .background(statusColor, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = status,
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GameRow(
    result: String,
    opponent: String,
    score: String,
    subtitle: String,
    resultColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(resultColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = result,
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = opponent,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Text(
            text = score,
            color = AthloColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AssociatedEventCard(
    tournament: Tournament,
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
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tournament.dateRange,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = tournament.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallBadge(tournament.sport, AthloColors.SuccessBg)
                    SmallBadge(tournament.status, AthloColors.SuccessBg)
                    SmallBadge(tournament.format, AthloColors.NeutralBg)
                }
            }
        }
    }
}

@Composable
private fun EmptyAssociatedEventsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sem eventos associados",
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OfflineProfileCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7CC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SignalWifiOff,
                contentDescription = "Sem internet",
                tint = Color(0xFF7A5B00),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "SEM LIGAÇÃO À INTERNET",
                    color = Color(0xFF7A5B00),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Alguns dados do perfil podem não ser carregados. Ainda podes editar o perfil e guardar alterações offline.",
                    color = Color(0xFF9A7800),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SmallBadge(
    text: String,
    background: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AthloColors.TextSecondary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SectionSmallTitle(title: String) {
    Text(
        text = title,
        color = AthloColors.TextMuted,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SuspendOrganizerButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE84D4D)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Suspender organizador",
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Suspender organizador",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LogoutButton(
    onLogoutClick: () -> Unit
) {
    Button(
        onClick = onLogoutClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AthloColors.Navy
        )
    ) {
        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = "Terminar sessão",
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))

        AthloLogoutButton(
            onClick = {
                onLogoutClick()
            }
        )
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

@Composable
private fun EmptyMatchesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = AthloColors.CardWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionSmallTitle("ÚLTIMOS JOGOS")

            Spacer(modifier = Modifier.height(40.dp))

            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SEM INFORMAÇÃO",
                color = Color.LightGray,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EmptyTeamsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = AthloColors.CardWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionSmallTitle("EQUIPAS INSCRITAS")

            Spacer(modifier = Modifier.height(40.dp))

            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SEM INFORMAÇÃO",
                color = Color.LightGray,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}