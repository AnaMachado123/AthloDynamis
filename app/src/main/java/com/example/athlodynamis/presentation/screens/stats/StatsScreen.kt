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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.domain.model.PlayerStatsData
import com.example.athlodynamis.domain.model.RecentGameData
import com.example.athlodynamis.presentation.components.*
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.StatsViewModel

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

    LaunchedEffect(userRole) {
        if (userRole == AthloUserRole.PLAYER) {
            if (userId.isNotBlank()) {
                statsViewModel.loadPlayerStatsByUserId(userId)
            }
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
                                    text = uiState.error ?: "Erro ao carregar estatísticas",
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

                    AthloUserRole.ORGANIZER -> OrganizerStatsContent()
                    AthloUserRole.ADMIN -> AdminStatsContent()
                }
            }
        }
    }
}

/* PLAYER */

@Composable
private fun PlayerStatsContent(stats: PlayerStatsData) {
    StatsHeader(
        title = "Estatísticas",
        subtitle = "As minhas stats",
        summaries = listOf(
            StatSummary(stats.totalMatches.toString(), "Jogos"),
            StatSummary(stats.trophies.toString(), "Troféus"),
            StatSummary(stats.teams.toString(), "Equipas")
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
                SectionMiniTitle("DESEMPENHO")
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    DonutChart(
                        percentage = stats.winPercentage,
                        modifier = Modifier.size(94.dp)
                    )

                    Spacer(modifier = Modifier.width(22.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LegendItem(AthloColors.Blue, "${stats.wins} Vitórias")
                        LegendItem(Color(0xFFE84D4D), "${stats.draws} Empates")
                        LegendItem(Color(0xFF9CA3AF), "${stats.losses} Derrotas")
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
            SectionMiniTitle("NÚMEROS DA ÉPOCA")
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStatBox(stats.goals.toString(), "Golos marcados", Modifier.weight(1f))
                SmallStatBox(stats.assists.toString(), "Assistências", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStatBox(stats.yellowCards.toString(), "Cartões amarelos", Modifier.weight(1f))
                SmallStatBox(stats.redCards.toString(), "Cartões vermelhos", Modifier.weight(1f))
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
                SectionMiniTitle("ÚLTIMOS JOGOS")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (recentGames.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sem jogos registados",
                    tint = AthloColors.TextMuted,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Sem jogos registados",
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
private fun OrganizerStatsContent() {
    StatsHeader(
        title = "Estatísticas",
        subtitle = "Os meus torneios",
        summaries = listOf(
            StatSummary("48", "Torneios"),
            StatSummary("100", "Jogos"),
            StatSummary("96%", "Conclusão")
        ),
        showAdminBadge = false
    )

    Spacer(modifier = Modifier.height(22.dp))

    ProgressCard(
        title = "JOGOS POR MODALIDADE",
        items = listOf(
            ProgressItem("Futsal", "42", 0.82f, AthloColors.Blue),
            ProgressItem("Futebol", "28", 0.58f, AthloColors.SuccessBg),
            ProgressItem("Basquetebol", "19", 0.40f, AthloColors.WarningBg),
            ProgressItem("Voleibol", "8", 0.20f, Color(0xFFBFA7FF))
        )
    )

    Spacer(modifier = Modifier.height(18.dp))

    OrganizerSeasonSummaryCard()

    Spacer(modifier = Modifier.height(18.dp))

    RankingCard(
        title = "TOP MARCADORES",
        items = listOf(
            RankingItem(1, "RM", "Rui Moreira", "SC Viseu", "23 golos"),
            RankingItem(2, "AF", "Ana Ferreira", "GD Monção", "19 golos"),
            RankingItem(3, "JS", "João Santos", "Atlético FC", "17 golos")
        )
    )
}

@Composable
private fun OrganizerSeasonSummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            SectionMiniTitle("RESUMO DA TEMPORADA")

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStatBox(
                    value = "348",
                    label = "Atletas registados",
                    modifier = Modifier.weight(1f)
                )

                SmallStatBox(
                    value = "32",
                    label = "Equipas ativas",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallStatBox(
                    value = "487",
                    label = "Golos marcados",
                    modifier = Modifier.weight(1f)
                )

                SmallStatBox(
                    value = "12",
                    label = "Torneios geridos",
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
private fun AdminStatsContent() {
    StatsHeader(
        title = "Estatísticas",
        subtitle = "Vista global da plataforma",
        summaries = listOf(
            StatSummary("48", "Torneios"),
            StatSummary("1.2k", "Utilizadores"),
            StatSummary("312", "Jogos")
        ),
        showAdminBadge = true
    )

    Spacer(modifier = Modifier.height(22.dp))

    ProgressCard(
        title = "CRESCIMENTO MENSAL",
        items = listOf(
            ProgressItem("Novos Utilizadores", "+184", 0.78f, AthloColors.Blue),
            ProgressItem("Torneios Criados", "+6", 0.58f, AthloColors.SuccessBg),
            ProgressItem("Jogos Disputados", "+32", 0.45f, AthloColors.WarningBg)
        )
    )

    Spacer(modifier = Modifier.height(18.dp))

    ProgressCard(
        title = "TORNEIOS POR MODALIDADE",
        items = listOf(
            ProgressItem("Futsal", "10", 0.82f, AthloColors.Blue),
            ProgressItem("Futebol", "7", 0.62f, AthloColors.SuccessBg),
            ProgressItem("Basquetebol", "5", 0.45f, AthloColors.WarningBg),
            ProgressItem("Voleibol", "2", 0.22f, Color(0xFFBFA7FF))
        )
    )

    Spacer(modifier = Modifier.height(18.dp))

    RankingCard(
        title = "TOP ORGANIZADORES",
        items = listOf(
            RankingItem(1, "CM", "Carlos Mendes", "12 torneios", "4.8 rating"),
            RankingItem(2, "AC", "Ana Carvalho", "8 torneios", "4.5 rating")
        )
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
            SectionMiniTitle("ESTADO DA PLATAFORMA")

            Spacer(modifier = Modifier.height(18.dp))

            PlatformStatusRow(
                icon = Icons.Default.Api,
                title = "API",
                status = "Operacional",
                statusColor = Color(0xFF3F7A28)
            )

            Spacer(modifier = Modifier.height(14.dp))

            PlatformStatusRow(
                icon = Icons.Default.Sync,
                title = "Sync Offline",
                status = "Lenta",
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
