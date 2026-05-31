package com.example.athlodynamis.presentation.screens.teams

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.data.mock.MockPlayers
import com.example.athlodynamis.data.mock.MockTeams
import com.example.athlodynamis.domain.model.Player
import com.example.athlodynamis.domain.model.Team
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.navigation.Screen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlodynamis.presentation.viewmodel.TeamsViewModel

@Composable
fun TeamDetailScreen(
    navController: NavController,
    teamId: Int
) {
    val viewModel: TeamsViewModel = viewModel()
    val teams by viewModel.teams.collectAsState()

    val team = teams.firstOrNull { it.id == teamId }

    if (team == null) {
        Text("Equipa não encontrada")
        return
    }

    val players = remember(teamId) {
        MockPlayers.getPlayersByTeam(teamId)
    }

    Scaffold(
        containerColor = AthloColors.Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                DetailHeader(
                    title = "Equipas",
                    subtitle = "Detalhes da equipa",
                    onBackClick = { navController.popBackStack() }
                )
            }

            item {
                PaddedContent {
                    TeamIdentity(team = team)
                }
            }

            item {
                PaddedContent {
                    Text(
                        text = "Jogadores",
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(players) { player ->
                PaddedContent {
                    PlayerRow(player = player)
                }
            }

            item {
                PaddedContent {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AthloColors.Blue)
                        ) {
                            Text(
                                text = "Adicionar jogador",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                navController.navigate(Screen.EditTeam.createRoute(team.id))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AthloColors.Blue)
                        ) {
                            Text(
                                text = "Editar equipa",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                PaddedContent {
                    Text(
                        text = "Estatísticas",
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                PaddedContent {
                    TeamStatsCard(team = team)
                }
            }

            item {
                PaddedContent {
                    Text(
                        text = "Eventos inscritos",
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                PaddedContent {
                    RegisteredEventCard(
                        date = "16 abr - 25 abr",
                        title = "Torneio de Futsal",
                        tags = listOf("Futsal", "Agendado", "Liga")
                    )
                }
            }

            item {
                PaddedContent {
                    RegisteredEventCard(
                        date = "10 abr - 25 abr",
                        title = "Torneio de Braga",
                        tags = listOf("Futebol", "A decorrer", "Grupos")
                    )
                }
            }

            item {
                PaddedContent {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFCC1F2F)
                        )
                    ) {
                        Text(
                            text = "Apagar clube",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaddedContent(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.padding(horizontal = 22.dp)
    ) {
        content()
    }
}

@Composable
private fun DetailHeader(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            Text(
                text = "‹ voltar",
                color = Color(0xFF8EC5F4),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable { onBackClick() }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = subtitle,
                        color = Color(0xFF8EC5F4),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                AdminBadge()
            }
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

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = "ADMIN",
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun TeamIdentity(team: Team) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = team.name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = team.sport,
                color = AthloColors.Blue,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .background(AthloColors.WarningBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = team.acronym,
                color = AthloColors.DarkNavy,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun PlayerRow(player: Player) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F1E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(AthloColors.DarkNavy, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Jogador",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = player.name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remover jogador",
                tint = AthloColors.DarkNavy,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TeamStatsCard(team: Team) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F1E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                icon = Icons.Default.CalendarMonth,
                value = team.games.toString(),
                label = "JOGOS"
            )

            StatItem(
                icon = Icons.Default.EmojiEvents,
                value = team.wins.toString(),
                label = "VITÓRIAS"
            )

            StatItem(
                icon = Icons.Default.SportsSoccer,
                value = team.goals.toString(),
                label = "GOLOS"
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = AthloColors.Blue,
            modifier = Modifier.size(34.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            color = AthloColors.Blue,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = label,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RegisteredEventCard(
    date: String,
    title: String,
    tags: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = date,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(AthloColors.NeutralBg, RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag,
                            color = AthloColors.TextSecondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}