package com.example.athlodynamis.presentation.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.data.mock.MockTournaments
import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.domain.model.Standing
import com.example.athlodynamis.domain.model.TournamentTeam
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.navigation.Screen

@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    navController: NavController
) {
    val tournament = MockTournaments.getTournamentById(tournamentId)
    var selectedTab by remember { mutableStateOf("Equipas") }

    Scaffold(
        containerColor = AthloColors.Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                TournamentHeader(
                    title = "Calendário",
                    subtitle = "${tournament.name} - ${tournament.sport}",
                    onBackClick = { navController.popBackStack() }
                )
            }

            item {
                Text(
                    text = "Sábado, 16 abr",
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            items(MockTournaments.matches.size) { index ->
                val match = MockTournaments.matches[index]

                MatchCard(
                    match = match,
                    onClick = {
                        navController.navigate(Screen.MatchDetail.createRoute(match.id))
                    }
                )
            }

            item {
                TournamentTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            item {
                if (selectedTab == "Equipas") {
                    TeamsTab()
                } else {
                    ClassificationTab()
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun TournamentHeader(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                modifier = Modifier.clickable { onBackClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Voltar",
                    tint = Color(0xFF8DC5F0),
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "voltar",
                    color = Color(0xFF8DC5F0),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

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

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle,
                        color = Color(0xFF8DC5F0),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                StatusPill(
                    text = "2 jogos hoje",
                    background = AthloColors.SuccessBg,
                    textColor = Color(0xFF4D8B4A)
                )
            }
        }
    }
}

@Composable
private fun MatchCard(
    match: Match,
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
                text = match.time,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamMiniBadge(
                    label = "EQP",
                    color = Color(0xFFD7EBFF)
                )

                Text(
                    text = match.teamA,
                    color = AthloColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )

                ScoreBox(score = match.scoreA?.toString() ?: "-")

                Text(
                    text = "-",
                    color = AthloColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                ScoreBox(score = match.scoreB?.toString() ?: "-")

                Text(
                    text = match.teamB,
                    color = AthloColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
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
                    text = match.status,
                    background = if (match.status == "A decorrer") {
                        AthloColors.DangerBg
                    } else {
                        AthloColors.NeutralBg
                    },
                    textColor = if (match.status == "A decorrer") {
                        Color(0xFFC83755)
                    } else {
                        AthloColors.TextSecondary
                    }
                )

                if (!match.minute.isNullOrBlank()) {
                    Text(
                        text = match.minute,
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.labelMedium,
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
            .padding(top = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (selected) Color(0xFF8DC5F0) else Color(0xFF9DB2C5),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(
                    if (selected) Color(0xFF8DC5F0) else Color.Transparent,
                    RoundedCornerShape(999.dp)
                )
        )
    }
}

@Composable
private fun TeamsTab() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFAF5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MockTournaments.teams.forEach { team ->
                TeamRow(team = team)
            }
        }
    }
}

@Composable
private fun TeamRow(team: TournamentTeam) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TeamMiniBadge(
            label = team.acronym,
            color = Color(0xFFD7EBFF)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                text = team.name,
                color = AthloColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${team.players} jogadores · Liga",
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Text(
            text = "${team.points}\npts",
            color = AthloColors.TextPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Abrir equipa",
            tint = AthloColors.TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ClassificationTab() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFAF5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", color = AthloColors.TextSecondary, modifier = Modifier.width(24.dp))
                Text("Equipa", color = AthloColors.TextSecondary, modifier = Modifier.weight(1f))
                Text("J", color = AthloColors.TextSecondary, modifier = Modifier.width(30.dp))
                Text("V", color = AthloColors.TextSecondary, modifier = Modifier.width(30.dp))
                Text("Pts", color = AthloColors.TextSecondary, modifier = Modifier.width(44.dp))
                Text("Forma", color = AthloColors.TextSecondary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            MockTournaments.standings.forEach { standing ->
                StandingRow(standing = standing)

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun StandingRow(standing: Standing) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = standing.position.toString(),
            color = AthloColors.TextPrimary,
            modifier = Modifier.width(24.dp)
        )

        TeamMiniBadge(
            label = standing.acronym,
            color = Color(0xFFD7EBFF)
        )

        Text(
            text = standing.teamName,
            color = AthloColors.TextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )

        Text(
            text = standing.games.toString(),
            color = AthloColors.TextPrimary,
            modifier = Modifier.width(30.dp)
        )

        Text(
            text = standing.wins.toString(),
            color = AthloColors.TextPrimary,
            modifier = Modifier.width(30.dp)
        )

        Text(
            text = standing.points.toString(),
            color = AthloColors.TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(44.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            standing.form.forEach { result ->
                FormDot(result = result)
            }
        }
    }
}

@Composable
private fun FormDot(result: String) {
    val color = when (result) {
        "W" -> Color(0xFF67A878)
        "L" -> Color(0xFFD01E1E)
        else -> Color(0xFFB8B8B8)
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color, CircleShape)
    )
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