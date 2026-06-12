package com.example.athlodynamis.presentation.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.data.mock.MockTournaments.tournaments
import com.example.athlodynamis.domain.model.PlayerStatsData
import com.example.athlodynamis.domain.model.RecentGameData
import com.example.athlodynamis.presentation.components.*
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.StatsViewModel
import com.example.athlodynamis.data.repository.TournamentRepository
import com.example.athlodynamis.data.repository.MatchRepository
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.data.repository.PlayerRepository
import com.example.athlodynamis.domain.model.Player
import com.example.athlodynamis.data.repository.UserRepository
import com.example.athlodynamis.data.remote.dto.UserDto

data class StatSummary(val value: String, val label: String)
data class ProgressItem(val label: String, val value: String, val progress: Float, val color: Color)
data class RankingItem(val position: Int, val initials: String, val name: String, val subtitle: String, val value: String)

@Composable
fun StatsScreen(
    navController: NavController,
    userRole: AthloUserRole,
    userId: String,
    statsViewModel: StatsViewModel = viewModel()
) {
    val uiState by statsViewModel.uiState.collectAsState()
    var organizerTournaments by remember {
        mutableStateOf<List<Tournament>>(emptyList())
    }

    var organizerMatches by remember {
        mutableStateOf<List<Match>>(emptyList())
    }
    var organizerPlayers by remember {
        mutableStateOf<List<Player>>(emptyList())
    }
    var adminTournaments by remember {
        mutableStateOf<List<Tournament>>(emptyList())
    }

    var adminMatches by remember {
        mutableStateOf<List<Match>>(emptyList())
    }

    var adminUsers by remember {
        mutableStateOf<List<UserDto>>(emptyList())
    }

    LaunchedEffect(userRole, userId) {
        if (userRole == AthloUserRole.PLAYER) {
            if (userId.isNotBlank()) {
                statsViewModel.loadPlayerStatsByUserId(userId)
            }
        }

        if (userRole == AthloUserRole.ORGANIZER) {

            val tournaments = TournamentRepository()
                .getTournaments()
                .filter { tournament ->
                    tournament.organizerId == userId
                }


            organizerTournaments = tournaments

            val allMatches = MatchRepository().getAllMatches()

            organizerMatches = allMatches.filter { match ->
                tournaments.any { tournament ->
                    tournament.id.toLongOrNull() == match.tournamentId
                }
            }
            val organizerTeamIds = organizerMatches
                .flatMap { match ->
                    listOfNotNull(
                        match.teamAId?.toInt(),
                        match.teamBId?.toInt()
                    )
                }
                .distinct()

            organizerPlayers = PlayerRepository()
                .getAllPlayers()
                .filter { player ->
                    player.teamId in organizerTeamIds
                }
        }
        if (userRole == AthloUserRole.ADMIN) {
            adminTournaments = TournamentRepository()
                .getTournaments()

            adminMatches = MatchRepository()
                .getAllMatches()

            adminUsers = UserRepository()
                .getAllUsers()
        }
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Stats.route,
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

                when (userRole) {
                    AthloUserRole.PLAYER -> {
                        when {
                            uiState.isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = AthloColors.Blue)
                                }
                            }

                            uiState.error != null -> {
                                Text(
                                    text = uiState.error ?: stringResource(R.string.stats_error_loading),
                                    color = Color(0xFFC83755),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            else -> {
                                PlayerStatsContent(
                                    stats = uiState.playerStats ?: PlayerStatsData(
                                        totalMatches = 0,
                                        wins = 0,
                                        draws = 0,
                                        losses = 0,
                                        goals = 0,
                                        assists = 0,
                                        yellowCards = 0,
                                        redCards = 0,
                                        teams = 0,
                                        trophies = 0
                                    )
                                )
                            }
                        }
                    }

                    AthloUserRole.ORGANIZER -> OrganizerStatsContent(
                        tournaments = organizerTournaments,
                        matches = organizerMatches,
                        players = organizerPlayers
                    )
                    AthloUserRole.ADMIN -> AdminStatsContent(
                        tournaments = adminTournaments,
                        matches = adminMatches,
                        users = adminUsers
                    )
                }
            }
        }
    }
}

/* PLAYER */

@Composable
private fun PlayerStatsContent(stats: PlayerStatsData) {
    StatsHeader(
        title = stringResource(R.string.stats_title),
        subtitle = stringResource(R.string.stats_my_stats),
        summaries = listOf(
            StatSummary(stats.totalMatches.toString(), stringResource(R.string.stats_games)),
            StatSummary(stats.trophies.toString(), stringResource(R.string.stats_trophies)),
            StatSummary(stats.teams.toString(), stringResource(R.string.stats_teams))
        ),
        showAdminBadge = false
    )

    Spacer(modifier = Modifier.height(22.dp))
    PerformanceCard(stats)
    Spacer(modifier = Modifier.height(18.dp))
    PlayerSeasonNumbersCard(stats)
    Spacer(modifier = Modifier.height(18.dp))
    RecentGamesCard(
        recentGames = stats.recentGames
    )
}

@Composable
private fun PerformanceCard(stats: PlayerStatsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SectionMiniTitle(stringResource(R.string.stats_performance))
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    DonutChart(
                        percentage = stats.winPercentage,
                        modifier = Modifier.size(94.dp)
                    )

                    Spacer(modifier = Modifier.width(22.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LegendItem(AthloColors.Blue, stringResource(R.string.stats_wins, stats.wins))
                        LegendItem(Color(0xFFE84D4D), stringResource(R.string.stats_draws, stats.draws))
                        LegendItem(Color(0xFF9CA3AF), stringResource(R.string.stats_losses, stats.losses))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerSeasonNumbersCard(stats: PlayerStatsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            SectionMiniTitle(stringResource(R.string.stats_season_numbers))
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStatBox(stats.goals.toString(), stringResource(R.string.stats_goals_scored), Modifier.weight(1f))
                SmallStatBox(stats.assists.toString(), stringResource(R.string.stats_assists), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStatBox(stats.yellowCards.toString(), stringResource(R.string.stats_yellow_cards), Modifier.weight(1f))
                SmallStatBox(stats.redCards.toString(), stringResource(R.string.stats_red_cards), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RecentGamesCard(
    recentGames: List<RecentGameData>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                SectionMiniTitle(stringResource(R.string.stats_recent_games))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (recentGames.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = stringResource(R.string.stats_no_games),
                    tint = AthloColors.TextMuted,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.stats_no_games),
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            } else {
                recentGames.forEach { game ->
                    val bgColor = when (game.result) {
                        "V" -> AthloColors.SuccessBg
                        "D" -> AthloColors.DangerBg
                        else -> AthloColors.NeutralBg
                    }

                    val textColor = when (game.result) {
                        "V" -> Color(0xFF3F7A28)
                        "D" -> Color(0xFFC83755)
                        else -> AthloColors.TextSecondary
                    }

                    RecentGameRow(
                        result = game.result,
                        resultColor = bgColor,
                        resultTextColor = textColor,
                        opponent = game.matchTitle,
                        score = game.score,
                        subtitle = game.subtitle
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentGameRow(
    result: String,
    resultColor: Color,
    resultTextColor: Color,
    opponent: String,
    score: String,
    subtitle: String
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
                color = resultTextColor,
                style = MaterialTheme.typography.labelMedium,
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
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}


/* ---------------------------------------------------------
   ORGANIZER
--------------------------------------------------------- */

@Composable

private fun OrganizerStatsContent(
    tournaments: List<Tournament>,
    matches: List<Match>,
    players: List<Player>,
) {
    val totalTournaments = tournaments.size
    val totalMatches = matches.size

    val completedMatches = matches.count {
        it.status.equals("Terminado", ignoreCase = true)
    }

    val completionPercentage = if (totalMatches == 0) {
        0
    } else {
        ((completedMatches.toFloat() / totalMatches.toFloat()) * 100).toInt()
    }
    val matchesBySport = tournaments
        .groupBy { it.sport }
        .mapValues { entry ->
            val tournamentIds = entry.value.mapNotNull { it.id.toLongOrNull() }

            matches.count { match ->
                match.tournamentId in tournamentIds
            }
        }
        .filter { it.value > 0 }

    val maxSportCount = matchesBySport.values.maxOrNull() ?: 1

    val modalityItems = matchesBySport.map { (sport, count) ->
        ProgressItem(
            label = localizedSportName(sport),
            value = count.toString(),
            progress = count.toFloat() / maxSportCount.toFloat(),
            color = when (sport.lowercase()) {
                "futebol" -> AthloColors.SuccessBg
                "basquetebol" -> AthloColors.WarningBg
                "voleibol" -> Color(0xFFBFA7FF)
                "ténis", "tenis" -> Color(0xFFD7EBFF)
                else -> AthloColors.Blue
            }
        )
    }
    val activeTeamsCount = matches
        .flatMap { match ->
            listOfNotNull(
                match.teamAId,
                match.teamBId
            )
        }
        .distinct()
        .size

    val totalGoals = matches.sumOf { match ->
        match.scoreA + match.scoreB
    }

    val topScorers = players
        .filter { it.goals > 0 }
        .sortedByDescending { it.goals }
        .take(3)
        .mapIndexed { index, player ->
            RankingItem(
                position = index + 1,
                initials = player.name
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercase() },
                name = player.name,
                subtitle = stringResource(R.string.stats_team, player.teamId ?: 0),
                value = stringResource(R.string.stats_goals, player.goals)
            )
        }

    StatsHeader(
        title = stringResource(R.string.stats_title),
        subtitle = stringResource(R.string.stats_my_tournaments),
        summaries = listOf(
            StatSummary(totalTournaments.toString(), stringResource(R.string.stats_tournaments)),
            StatSummary(totalMatches.toString(), stringResource(R.string.stats_games)),
            StatSummary("$completionPercentage%", stringResource(R.string.stats_completion))
        ),
        showAdminBadge = false
    )

    Spacer(modifier = Modifier.height(22.dp))

    ProgressCard(
        title = stringResource(R.string.stats_games_by_sport),
        items = modalityItems
    )

    Spacer(modifier = Modifier.height(18.dp))

    OrganizerSeasonSummaryCard(
        athletesCount = players.size,
        activeTeamsCount = activeTeamsCount,
        goalsCount = totalGoals,
        tournamentsCount = tournaments.size
    )

    Spacer(modifier = Modifier.height(18.dp))

    RankingCard(
        title = stringResource(R.string.stats_top_scorers),
        items = topScorers
    )
}

@Composable
private fun OrganizerSeasonSummaryCard(
    athletesCount: Int,
    activeTeamsCount: Int,
    goalsCount: Int,
    tournamentsCount: Int
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
            SectionMiniTitle(stringResource(R.string.stats_season_summary))

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStatBox(
                    value = athletesCount.toString(),
                    label = stringResource(R.string.stats_registered_athletes),
                    modifier = Modifier.weight(1f)
                )

                SmallStatBox(
                    value = activeTeamsCount.toString(),
                    label = stringResource(R.string.stats_active_teams),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStatBox(
                    value = goalsCount.toString(),
                    label = stringResource(R.string.stats_goals_scored),
                    modifier = Modifier.weight(1f)
                )

                SmallStatBox(
                    value = tournamentsCount.toString(),
                    label = stringResource(R.string.stats_managed_tournaments),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


/* ---------------------------------------------------------
   ADMIN
--------------------------------------------------------- */

@Composable
private fun AdminStatsContent(
    tournaments: List<Tournament>,
    matches: List<Match>,
    users: List<UserDto>
) {
    val totalTournaments = tournaments.size
    val totalUsers = users.size
    val totalMatches = matches.size

    val completedMatches = matches.count {
        it.status.equals("Terminado", ignoreCase = true)
    }

    val maxGrowthValue = listOf(
        totalUsers,
        totalTournaments,
        completedMatches
    ).maxOrNull() ?: 1

    val growthItems = listOf(
        ProgressItem(
            label = stringResource(R.string.stats_users),
            value = totalUsers.toString(),
            progress = totalUsers.toFloat() / maxGrowthValue.toFloat(),
            color = AthloColors.Blue
        ),
        ProgressItem(
            label = stringResource(R.string.stats_created_tournaments),
            value = totalTournaments.toString(),
            progress = totalTournaments.toFloat() / maxGrowthValue.toFloat(),
            color = AthloColors.SuccessBg
        ),
        ProgressItem(
            label = stringResource(R.string.stats_finished_games),
            value = completedMatches.toString(),
            progress = completedMatches.toFloat() / maxGrowthValue.toFloat(),
            color = AthloColors.WarningBg
        )
    )

    val tournamentsBySport = tournaments
        .groupBy { it.sport }
        .mapValues { it.value.size }
        .filter { it.value > 0 }

    val maxTournamentSportCount = tournamentsBySport.values.maxOrNull() ?: 1

    val tournamentSportItems = tournamentsBySport.map { (sport, count) ->
        ProgressItem(
            label = localizedSportName(sport),
            value = count.toString(),
            progress = count.toFloat() / maxTournamentSportCount.toFloat(),
            color = when (sport.lowercase()) {
                "futebol" -> AthloColors.SuccessBg
                "basquetebol" -> AthloColors.WarningBg
                "voleibol" -> Color(0xFFBFA7FF)
                "ténis", "tenis" -> Color(0xFFD7EBFF)
                else -> AthloColors.Blue
            }
        )
    }

    val organizers = users.filter {
        it.role.equals("ORGANIZER", ignoreCase = true)
    }

    val topOrganizers = organizers
        .map { organizer ->
            val organizerTournamentCount = tournaments.count { tournament ->
                tournament.organizerId == organizer.id
            }

            organizer to organizerTournamentCount
        }
        .filter { it.second > 0 }
        .sortedByDescending { it.second }
        .take(3)
        .mapIndexed { index, pair ->
            val organizer = pair.first
            val tournamentCount = pair.second

            RankingItem(
                position = index + 1,
                initials = organizer.name
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercase() },
                name = organizer.name,
                subtitle = stringResource(R.string.stats_organizer_tournaments, tournamentCount),
                value = organizer.approvalStatus
            )
        }

    StatsHeader(
        title = stringResource(R.string.stats_title),
        subtitle = stringResource(R.string.stats_platform_overview),
        summaries = listOf(
            StatSummary(totalTournaments.toString(), stringResource(R.string.stats_tournaments)),
            StatSummary(totalUsers.toString(), stringResource(R.string.stats_users)),
            StatSummary(totalMatches.toString(), stringResource(R.string.stats_games))
        ),
        showAdminBadge = true
    )

    Spacer(modifier = Modifier.height(22.dp))

    ProgressCard(
        title = stringResource(R.string.stats_platform_summary),
        items = growthItems
    )

    Spacer(modifier = Modifier.height(18.dp))

    ProgressCard(
        title = stringResource(R.string.stats_tournaments_by_sport),
        items = tournamentSportItems
    )

    Spacer(modifier = Modifier.height(18.dp))

    RankingCard(
        title = stringResource(R.string.stats_top_organizers),
        items = topOrganizers
    )

    Spacer(modifier = Modifier.height(18.dp))

    PlatformStatusCard()
}

/* ---------------------------------------------------------
   COMMON COMPONENTS
--------------------------------------------------------- */

@Composable
private fun StatsHeader(
    title: String,
    subtitle: String,
    summaries: List<StatSummary>,
    showAdminBadge: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AthloColors.Navy)
                    .padding(horizontal = 22.dp, vertical = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = subtitle,
                            color = Color(0xFF8EC5F4),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (showAdminBadge) {
                        AdminBadge()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                summaries.forEach { stat ->
                    HeaderStatBox(
                        stat = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderStatBox(
    stat: StatSummary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(74.dp)
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
            contentDescription = stringResource(R.string.stats_admin),
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(12.dp)
        )

        Spacer(modifier = Modifier.width(3.dp))

        Text(
            text = stringResource(R.string.stats_admin),
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
private fun SectionMiniTitle(text: String) {
    Text(
        text = text,
        color = AthloColors.TextMuted,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SmallStatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(66.dp)
            .background(
                color = AthloColors.NeutralBg,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = value,
                color = AthloColors.Navy,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = label,
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun ProgressCard(
    title: String,
    items: List<ProgressItem>
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
            SectionMiniTitle(title)

            Spacer(modifier = Modifier.height(18.dp))

            items.forEach { item ->
                ProgressRow(item = item)

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun ProgressRow(item: ProgressItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.label,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(112.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .background(AthloColors.NeutralBg, RoundedCornerShape(999.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(item.progress)
                    .height(10.dp)
                    .background(item.color, RoundedCornerShape(999.dp))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = item.value,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RankingCard(
    title: String,
    items: List<RankingItem>
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
            SectionMiniTitle(title)

            Spacer(modifier = Modifier.height(16.dp))

            items.forEach { item ->
                RankingRow(item = item)

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun RankingRow(item: RankingItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.position.toString(),
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(22.dp)
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(AthloColors.SoftBlue, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.initials,
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
                text = item.name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = item.subtitle,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Text(
            text = item.value,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PlatformStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            SectionMiniTitle(stringResource(R.string.stats_platform_status))

            Spacer(modifier = Modifier.height(18.dp))

            PlatformStatusRow(
                icon = Icons.Default.Api,
                title = stringResource(R.string.stats_api),
                status = stringResource(R.string.stats_operational),
                statusColor = Color(0xFF3F7A28)
            )

            Spacer(modifier = Modifier.height(14.dp))

            PlatformStatusRow(
                icon = Icons.Default.Sync,
                title = stringResource(R.string.stats_offline_sync),
                status = stringResource(R.string.stats_slow),
                statusColor = Color(0xFF9A6B22)
            )
        }
    }
}

@Composable
private fun PlatformStatusRow(
    icon: ImageVector,
    title: String,
    status: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = AthloColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = status,
            color = statusColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
private fun localizedSportName(sport: String): String {
    return when (sport.lowercase()) {
        "futebol" -> stringResource(R.string.sport_football)
        "basquetebol" -> stringResource(R.string.sport_basketball)
        "voleibol" -> stringResource(R.string.sport_volleyball)
        "ténis", "tenis" -> stringResource(R.string.sport_tennis)
        else -> sport
    }
}

@Composable
private fun DonutChart(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 16.dp.toPx()
            val diameter = size.minDimension
            val arcSize = Size(diameter, diameter)

            drawArc(
                color = AthloColors.NeutralBg,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = AthloColors.Blue,
                startAngle = -90f,
                sweepAngle = 360f * (percentage / 100f),
                useCenter = false,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = Color(0xFFE84D4D),
                startAngle = -90f + 360f * (percentage / 100f),
                sweepAngle = 70f,
                useCenter = false,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = "$percentage%",
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(3.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
