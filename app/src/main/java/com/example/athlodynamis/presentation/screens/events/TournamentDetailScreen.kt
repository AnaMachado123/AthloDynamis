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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    navController: NavController,
    userRole: AthloUserRole
) {
    var selectedTab by remember { mutableStateOf("Equipas") }

    val isAdmin = userRole == AthloUserRole.ADMIN
    val canManageEvent = userRole == AthloUserRole.ADMIN || userRole == AthloUserRole.ORGANIZER

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

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sábado, 16 abr",
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
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
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

            item {
                MatchCard(
                    matchId = "1",
                    time = "10:00",
                    teamA = "Equipa 1",
                    teamB = "Equipa 2",
                    scoreA = "3",
                    scoreB = "1",
                    status = "Terminado",
                    minute = "",
                    onClick = {
                        navController.navigate(Screen.MatchDetail.createRoute("1"))
                    }
                )
            }

            item {
                MatchCard(
                    matchId = "2",
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
                if (selectedTab == "Equipas") {
                    TeamsTable()
                } else {
                    StandingsTable()
                }
            }
        }
    }
}

@Composable
private fun TournamentHeader(
    userRole: AthloUserRole,
    canManageEvent: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val isAdmin = userRole == AthloUserRole.ADMIN

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
            Column {
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
                    text = "Torneio de Braga · Futebol",
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
                        text = "2 jogos hoje",
                        background = AthloColors.SuccessBg,
                        textColor = Color(0xFF4D8B4A)
                    )
                }

                if (canManageEvent) {
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
private fun MatchCard(
    matchId: String,
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
                    color = Color(0xFFD7EBFF),
                    textColor = AthloColors.Blue
                )

                Text(
                    text = teamA,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
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
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                )

                TeamMiniBadge(
                    label = "EQP",
                    color = Color(0xFFDFF3D8),
                    textColor = Color(0xFF4D8B4A)
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
private fun TeamsTable() {
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
            TeamStandingRow("Equipa 1", "8 jogadores · Liga", "9 pts", Color(0xFFD7EBFF), AthloColors.Blue)
            Separator()
            TeamStandingRow("Equipa 2", "7 jogadores · Liga", "6 pts", Color(0xFFDFF3D8), Color(0xFF4D8B4A))
            Separator()
            TeamStandingRow("Equipa 3", "8 jogadores · Liga", "3 pts", Color(0xFFFFEFD7), Color(0xFF9A6B22))
            Separator()
            TeamStandingRow("Equipa 4", "6 jogadores · Liga", "1 pts", Color(0xFFF8FFB0), Color(0xFFD4DD00))
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
            label = "EQP",
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
private fun StandingsTable() {
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

            StandingRow("1", "Equipa 1", "3", "3", "9", listOf(true, true, true), Color(0xFFD7EBFF), AthloColors.Blue)
            Separator()
            StandingRow("2", "Equipa 2", "3", "2", "6", listOf(false, true, true), Color(0xFFDFF3D8), Color(0xFF4D8B4A))
            Separator()
            StandingRow("3", "Equipa 3", "3", "1", "4", listOf(false, false, true), Color(0xFFFFEFD7), Color(0xFF9A6B22))
            Separator()
            StandingRow("4", "Equipa 4", "3", "0", "1", listOf(false, false, false), Color(0xFFF8FFB0), Color(0xFFD4DD00))
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
                label = "EQP",
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
            form.forEach { win ->
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