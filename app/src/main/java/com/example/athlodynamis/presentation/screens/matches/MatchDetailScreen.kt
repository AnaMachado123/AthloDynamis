package com.example.athlodynamis.presentation.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.MatchesViewModel

@Composable
fun MatchDetailScreen(
    matchId: String,
    navController: NavController,
    userRole: AthloUserRole
) {
    val matchesViewModel: MatchesViewModel = viewModel()

    val match by matchesViewModel.selectedMatch.collectAsState()
    val error by matchesViewModel.error.collectAsState()

    val isAdmin = userRole == AthloUserRole.ADMIN
    val canManageMatch = userRole == AthloUserRole.ADMIN || userRole == AthloUserRole.ORGANIZER

    LaunchedEffect(matchId) {
        matchesViewModel.loadMatchById(matchId.toLong())
    }

    val currentMatch = match

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
            verticalArrangement = Arrangement.spacedBy(22.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                MatchHeader(
                    tournamentName = "Torneio #${currentMatch?.tournamentId ?: ""}",
                    sport = currentMatch?.status ?: "A carregar",
                    isAdmin = isAdmin,
                    canManageMatch = canManageMatch && currentMatch != null,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = {
                        navController.navigate(Screen.EditMatch.createRoute(matchId))
                    }
                )
            }

            if (error != null) {
                item {
                    InfoCard(text = error ?: "Erro ao carregar jogo")
                }
            } else if (currentMatch == null) {
                item {
                    InfoCard(text = "A carregar jogo...")
                }
            } else {
                item {
                    MatchScoreSection(match = currentMatch)
                }

                item {
                    Divider(color = Color(0xFFD7D7D7))
                }

                item {
                    MatchEventsCard(match = currentMatch)
                }

                if (canManageMatch && currentMatch.status == "A decorrer") {
                    item {
                        Button(
                            onClick = {
                                navController.navigate(Screen.ManageLiveMatch.createRoute(matchId))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AthloColors.Blue
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Gerir jogo ao vivo",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
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
private fun MatchHeader(
    tournamentName: String,
    sport: String,
    isAdmin: Boolean,
    canManageMatch: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 90.dp)
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Detalhe Jogo",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$tournamentName - $sport",
                    color = Color(0xFF8DC5F0),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Column(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isAdmin) {
                    AdminBadge()
                }

                if (canManageMatch) {
                    EditBadge(
                        onClick = onEditClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchScoreSection(match: Match) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (match.time.isNotBlank()) "Hora: ${match.time}" else "Hora por definir",
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp)
        )

        if (!match.location.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = match.location,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamBlock(
                acronym = getAcronym(match.teamA),
                teamName = match.teamA,
                background = Color(0xFFD7EBFF),
                textColor = AthloColors.Blue
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreDarkBox(match.scoreA?.toString() ?: "-")

                Text(
                    text = "vs",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                ScoreDarkBox(match.scoreB?.toString() ?: "-")
            }

            TeamBlock(
                acronym = getAcronym(match.teamB),
                teamName = match.teamB,
                background = Color(0xFFE8F3DD),
                textColor = Color(0xFF5F9E6E)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (match.status == "A decorrer" && !match.minute.isNullOrBlank()) {
            Text(
                text = "• ${match.minute}",
                color = Color(0xFFC83755),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        StatusPillLarge(match.status)
    }
}

private fun getAcronym(teamName: String): String {
    val parts = teamName
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        parts.isEmpty() -> "EQP"
        parts.size == 1 -> parts.first().take(3).uppercase()
        else -> parts.take(3).joinToString("") { it.first().uppercase() }
    }
}

@Composable
private fun TeamBlock(
    acronym: String,
    teamName: String,
    background: Color,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(74.dp)
                .background(background, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = acronym,
                color = textColor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = teamName,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ScoreDarkBox(score: String) {
    Box(
        modifier = Modifier
            .background(AthloColors.Navy, RoundedCornerShape(10.dp))
            .padding(horizontal = 15.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = score,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun StatusPillLarge(status: String) {
    val background = when (status) {
        "A decorrer" -> AthloColors.DangerBg
        "Agendado" -> Color(0xFFDCEBFF)
        else -> AthloColors.Navy
    }

    val textColor = when (status) {
        "A decorrer" -> Color(0xFFC83755)
        "Agendado" -> AthloColors.Blue
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(12.dp))
            .padding(horizontal = 22.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun MatchEventsCard(match: Match) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 260.dp),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFAF5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Eventos do Jogo",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(26.dp))

            when (match.status) {
                "Agendado" -> EmptyEventsState()
                "A decorrer" -> LiveEvents()
                else -> FinishedEvents()
            }
        }
    }
}

@Composable
private fun EmptyEventsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(
                    width = 2.dp,
                    color = Color(0xFFB5B5B5),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "i",
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Jogo ainda não iniciado",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Os eventos aparecerão aqui quando o\njogo começar",
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LiveEvents() {
    EventRow(
        minute = "38'",
        playerName = "Rui Moreira",
        team = "Equipa 3",
        teamColor = Color(0xFFF8FFB0)
    )
}

@Composable
private fun FinishedEvents() {
    EventRow("38'", "Rui Moreira", "Equipa 1", Color(0xFFD7EBFF))
    EventRow("42'", "André Cerqueira", "Equipa 2", Color(0xFFE8F3DD))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFCFCFCF)
        )

        Text(
            text = "Intervalo",
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Divider(
            modifier = Modifier.weight(1f),
            color = Color(0xFFCFCFCF)
        )
    }

    EventRow("70'", "João Silva", "Equipa 1", Color(0xFFD7EBFF))
    EventRow("80'", "Bruno Fernandes", "Equipa 1", Color(0xFFD7EBFF))
}

@Composable
private fun EventRow(
    minute: String,
    playerName: String,
    team: String,
    teamColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = minute,
            color = Color(0xFF8DC5F0),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(44.dp)
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFF7AB88A), RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SportsVolleyball,
                contentDescription = "Golo",
                tint = AthloColors.Navy,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = "Golo",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = playerName,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Box(
            modifier = Modifier
                .background(teamColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = team,
                color = AthloColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }
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
private fun EditBadge(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color(0xFF76B982), RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar jogo",
            tint = Color.White,
            modifier = Modifier.size(13.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Editar",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}