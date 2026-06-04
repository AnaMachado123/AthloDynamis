package com.example.athlodynamis.presentation.screens.events

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
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
import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.MatchesViewModel
import com.example.athlodynamis.presentation.viewmodel.TournamentsViewModel

private data class TournamentTeamStats(
    val teamId: Long?,
    val teamName: String,
    val sport: String,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int,
    val form: List<Boolean>
) {
    val goalDifference: Int
        get() = goalsFor - goalsAgainst
}

@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    navController: NavController,
    userRole: AthloUserRole
) {
    val tournamentsViewModel: TournamentsViewModel = viewModel()
    val matchesViewModel: MatchesViewModel = viewModel()

    val tournament by tournamentsViewModel.selectedTournament.collectAsState()
    val isLoading by tournamentsViewModel.isLoading.collectAsState()
    val error by tournamentsViewModel.error.collectAsState()

    val matches by matchesViewModel.matches.collectAsState()
    val matchesError by matchesViewModel.error.collectAsState()

    var selectedTab by remember { mutableStateOf("Equipas") }

    val canManageEvent = userRole == AthloUserRole.ADMIN ||
            userRole == AthloUserRole.ORGANIZER

    val tournamentIdLong = tournamentId.toLongOrNull()

    LaunchedEffect(tournamentId) {
        tournamentsViewModel.loadTournamentById(tournamentId)

        tournamentIdLong?.let {
            matchesViewModel.loadMatches(it)
        }
    }

    val currentTournament = tournament

    val teamStats = remember(matches, currentTournament) {
        calculateTeamStats(
            matches = matches,
            sport = currentTournament?.sport ?: "Modalidade"
        )
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Events.route,
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

                TournamentHeader(
                    tournament = currentTournament,
                    userRole = userRole,
                    canManageEvent = canManageEvent,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditClick = {
                        navController.navigate(
                            Screen.EditEvent.createRoute(tournamentId)
                        )
                    }
                )
            }

            when {
                tournamentIdLong == null -> {
                    item {
                        InfoCard(text = "ID do torneio inválido.")
                    }
                }

                isLoading -> {
                    item {
                        InfoCard(text = "A carregar torneio...")
                    }
                }

                error != null -> {
                    item {
                        InfoCard(text = error ?: "Erro ao carregar torneio")
                    }
                }

                currentTournament == null -> {
                    item {
                        InfoCard(text = "Torneio não encontrado.")
                    }
                }

                else -> {
                    item {
                        TournamentInfoCard(tournament = currentTournament)
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Jogos do torneio",
                                color = AthloColors.TextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (canManageEvent) {
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            Screen.AddMatch.createRoute(tournamentId)
                                        )
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AthloColors.Blue
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = 14.dp,
                                        vertical = 8.dp
                                    )
                                ) {
                                    Text(
                                        text = "Adicionar Jogo",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Icon(
                                        imageVector = Icons.Default.AddCircle,
                                        contentDescription = "Adicionar jogo",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (matchesError != null) {
                        item {
                            InfoCard(text = matchesError ?: "Erro ao carregar jogos")
                        }
                    } else if (matches.isEmpty()) {
                        item {
                            InfoCard(text = "Ainda não existem jogos neste torneio.")
                        }
                    } else {
                        items(
                            items = matches,
                            key = { match -> match.id }
                        ) { match ->
                            MatchCard(
                                match = match,
                                onClick = {
                                    navController.navigate(
                                        Screen.MatchDetail.createRoute(match.id.toString())
                                    )
                                }
                            )
                        }
                    }

                    item {
                        TournamentTabs(
                            selectedTab = selectedTab,
                            onTabSelected = {
                                selectedTab = it
                            }
                        )
                    }

                    item {
                        if (teamStats.isEmpty()) {
                            EmptyTournamentTable(
                                text = "Ainda não existem equipas associadas a este torneio."
                            )
                        } else {
                            if (selectedTab == "Equipas") {
                                TeamsTable(
                                    teams = teamStats
                                )
                            } else {
                                StandingsTable(
                                    teams = teamStats
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TournamentHeader(
    tournament: Tournament?,
    userRole: AthloUserRole,
    canManageEvent: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val isAdmin = userRole == AthloUserRole.ADMIN
    val subtitle = tournament?.let { "${it.name} · ${it.sport}" } ?: "A carregar torneio"

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
                .padding(horizontal = 22.dp, vertical = 22.dp)
        ) {
            Column(modifier = Modifier.padding(end = 90.dp)) {
                Text(
                    text = "‹ voltar",
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Calendário",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isAdmin) {
                    AdminBadge()
                } else {
                    StatusPill(
                        text = tournament?.status ?: "A carregar",
                        background = AthloColors.SuccessBg,
                        textColor = Color(0xFF4D8B4A)
                    )
                }

                if (canManageEvent && tournament != null) {
                    StatusPill(
                        text = "Editar",
                        background = Color(0xFF76B982),
                        textColor = Color.White,
                        modifier = Modifier.clickable { onEditClick() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TournamentInfoCard(tournament: Tournament) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = tournament.name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = tournament.dateRange,
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusPill(
                    text = tournament.sport,
                    background = AthloColors.InfoBg,
                    textColor = AthloColors.Blue
                )

                StatusPill(
                    text = tournament.status,
                    background = AthloColors.WarningBg,
                    textColor = Color(0xFF9A6B22)
                )

                StatusPill(
                    text = tournament.format,
                    background = AthloColors.NeutralBg,
                    textColor = AthloColors.TextSecondary
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
private fun MatchCard(
    match: Match,
    onClick: () -> Unit
) {
    val minuteText = match.minute?.let { "${it}'" } ?: ""
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
                    color = Color(0xFFD7EBFF),
                    textColor = AthloColors.Blue
                )

                Text(
                    text = match.teamAName,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
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
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                )

                TeamMiniBadge(
                    label = match.teamBName.toAcronym(),
                    color = Color(0xFFDFF3D8),
                    textColor = Color(0xFF4D8B4A)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                if (minuteText.isNotBlank()) {
                    Text(
                        text = minuteText,
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
private fun TournamentTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthloColors.Navy, RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TabButton(
                text = "Equipas",
                selected = selectedTab == "Equipas",
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected("Equipas") }
            )

            TabButton(
                text = "Classificação",
                selected = selectedTab == "Classificação",
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected("Classificação") }
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (selected) Color(0xFF8EC5F4) else Color(0xFFB9C7D4),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    if (selected) Color(0xFF8EC5F4) else Color.Transparent
                )
        )
    }
}

@Composable
private fun TeamsTable(
    teams: List<TournamentTeamStats>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            bottomStart = AthloRadius.Large,
            bottomEnd = AthloRadius.Large
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5EE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            teams.forEachIndexed { index, team ->
                val colors = standingColors(index)

                TeamStandingRow(
                    name = team.teamName,
                    subtitle = team.sport,
                    points = "${team.points} pts",
                    badgeColor = colors.first,
                    badgeTextColor = colors.second
                )

                if (index < teams.lastIndex) {
                    Separator()
                }
            }
        }
    }
}

@Composable
private fun TeamStandingRow(
    name: String,
    subtitle: String,
    points: String,
    badgeColor: Color,
    badgeTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TeamMiniBadge(
            label = name.toAcronym(),
            color = badgeColor,
            textColor = badgeTextColor
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                text = name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Text(
            text = points,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Abrir equipa",
            tint = Color(0xFFC7C7C7),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun StandingsTable(
    teams: List<TournamentTeamStats>
) {
    val sortedTeams = teams.sortedWith(
        compareByDescending<TournamentTeamStats> { it.points }
            .thenByDescending { it.goalDifference }
            .thenByDescending { it.goalsFor }
            .thenBy { it.teamName }
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            bottomStart = AthloRadius.Large,
            bottomEnd = AthloRadius.Large
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5EE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", modifier = Modifier.width(24.dp), color = AthloColors.TextSecondary)
                Text("Equipa", modifier = Modifier.weight(1f), color = AthloColors.TextSecondary)
                Text("J", modifier = Modifier.width(28.dp), color = AthloColors.TextSecondary)
                Text("V", modifier = Modifier.width(28.dp), color = AthloColors.TextSecondary)
                Text("Pts", modifier = Modifier.width(42.dp), color = AthloColors.TextSecondary)
                Text("Forma", color = AthloColors.TextSecondary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            sortedTeams.forEachIndexed { index, team ->
                val colors = standingColors(index)

                StandingRow(
                    position = (index + 1).toString(),
                    team = team.teamName,
                    games = team.played.toString(),
                    wins = team.wins.toString(),
                    points = team.points.toString(),
                    form = team.form,
                    badgeColor = colors.first,
                    badgeTextColor = colors.second
                )

                if (index < sortedTeams.lastIndex) {
                    Separator()
                }
            }
        }
    }
}

@Composable
private fun StandingRow(
    position: String,
    team: String,
    games: String,
    wins: String,
    points: String,
    form: List<Boolean>,
    badgeColor: Color,
    badgeTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(position, modifier = Modifier.width(24.dp), color = AthloColors.TextPrimary)

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamMiniBadge(
                label = team.toAcronym(),
                color = badgeColor,
                textColor = badgeTextColor
            )

            Text(
                text = team,
                color = AthloColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(games, modifier = Modifier.width(28.dp), color = AthloColors.TextPrimary)
        Text(wins, modifier = Modifier.width(28.dp), color = AthloColors.TextPrimary)
        Text(points, modifier = Modifier.width(42.dp), color = AthloColors.TextPrimary)

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (form.isEmpty()) {
                Text(
                    text = "-",
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            } else {
                form.takeLast(3).forEach { win ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                if (win) Color(0xFF67A978) else Color(0xFFD32626),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTournamentTable(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            bottomStart = AthloRadius.Large,
            bottomEnd = AthloRadius.Large
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5EE)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
private fun TeamMiniBadge(
    label: String,
    color: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
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
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
private fun AdminBadge() {
    Row(
        modifier = Modifier
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

@Composable
private fun Separator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE0DED6))
    )
}

private fun calculateTeamStats(
    matches: List<Match>,
    sport: String
): List<TournamentTeamStats> {
    val teams = mutableMapOf<String, TournamentTeamStats>()

    fun teamKey(teamId: Long?, teamName: String): String {
        return teamId?.toString() ?: teamName.trim().lowercase()
    }

    fun ensureTeam(teamId: Long?, teamName: String) {
        val key = teamKey(teamId, teamName)

        if (!teams.containsKey(key)) {
            teams[key] = TournamentTeamStats(
                teamId = teamId,
                teamName = teamName,
                sport = sport,
                played = 0,
                wins = 0,
                draws = 0,
                losses = 0,
                goalsFor = 0,
                goalsAgainst = 0,
                points = 0,
                form = emptyList()
            )
        }
    }

    matches.forEach { match ->
        ensureTeam(
            teamId = match.teamAId,
            teamName = match.teamAName
        )

        ensureTeam(
            teamId = match.teamBId,
            teamName = match.teamBName
        )

        if (!match.status.equals("Terminado", ignoreCase = true)) {
            return@forEach
        }

        val teamAKey = teamKey(match.teamAId, match.teamAName)
        val teamBKey = teamKey(match.teamBId, match.teamBName)

        val teamA = teams[teamAKey] ?: return@forEach
        val teamB = teams[teamBKey] ?: return@forEach

        val scoreA = match.scoreA
        val scoreB = match.scoreB

        val teamAWon = scoreA > scoreB
        val teamBWon = scoreB > scoreA
        val draw = scoreA == scoreB

        teams[teamAKey] = teamA.copy(
            played = teamA.played + 1,
            wins = teamA.wins + if (teamAWon) 1 else 0,
            draws = teamA.draws + if (draw) 1 else 0,
            losses = teamA.losses + if (teamBWon) 1 else 0,
            goalsFor = teamA.goalsFor + scoreA,
            goalsAgainst = teamA.goalsAgainst + scoreB,
            points = teamA.points + when {
                teamAWon -> 3
                draw -> 1
                else -> 0
            },
            form = teamA.form + teamAWon
        )

        teams[teamBKey] = teamB.copy(
            played = teamB.played + 1,
            wins = teamB.wins + if (teamBWon) 1 else 0,
            draws = teamB.draws + if (draw) 1 else 0,
            losses = teamB.losses + if (teamAWon) 1 else 0,
            goalsFor = teamB.goalsFor + scoreB,
            goalsAgainst = teamB.goalsAgainst + scoreA,
            points = teamB.points + when {
                teamBWon -> 3
                draw -> 1
                else -> 0
            },
            form = teamB.form + teamBWon
        )
    }

    return teams.values.sortedWith(
        compareByDescending<TournamentTeamStats> { it.points }
            .thenByDescending { it.goalDifference }
            .thenByDescending { it.goalsFor }
            .thenBy { it.teamName }
    )
}

private fun standingColors(index: Int): Pair<Color, Color> {
    return when (index % 4) {
        0 -> Color(0xFFD7EBFF) to AthloColors.Blue
        1 -> Color(0xFFDFF3D8) to Color(0xFF4D8B4A)
        2 -> Color(0xFFFFEFD7) to Color(0xFF9A6B22)
        else -> Color(0xFFF8FFB0) to Color(0xFFD4A000)
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