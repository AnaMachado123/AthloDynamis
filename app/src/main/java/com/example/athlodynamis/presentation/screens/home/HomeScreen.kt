package com.example.athlodynamis.presentation.screens.home

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.components.AthloBottomBar

enum class UserRole {
    ORGANIZER,
    PLAYER
}

data class DashboardStat(
    val value: String,
    val label: String
)

@Composable
fun HomeScreen(navController: NavController) {
    /*
     * Para testar:
     * UserRole.ORGANIZER = ecrã do organizador
     * UserRole.PLAYER = ecrã do jogador
     *
     * Depois, quando houver login real, isto passa a vir do utilizador autenticado.
     */
    val userRole = remember { UserRole.ORGANIZER }

    Scaffold(
        containerColor = AthloColors.Background,
        floatingActionButton = {
            if (userRole == UserRole.ORGANIZER) {
                FloatingActionButton(
                    onClick = { },
                    containerColor = AthloColors.Blue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar"
                    )
                }
            }
        },
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Home.route
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                when (userRole) {
                    UserRole.ORGANIZER -> OrganizerHomeContent()
                    UserRole.PLAYER -> PlayerHomeContent()
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun OrganizerHomeContent() {
    DashboardHeader(
        name = "Gonçalo Magalhães",
        initials = "GM",
        stats = listOf(
            DashboardStat("6", "Torneios ativos"),
            DashboardStat("3", "Jogos hoje"),
            DashboardStat("348", "Atletas")
        )
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
        minute = "33'"
    )

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = "Os meus eventos")

    Spacer(modifier = Modifier.height(8.dp))

    EventCard(
        date = "10 abr - 25 abr",
        title = "Torneio de Braga",
        tags = listOf("Futebol", "A decorrer", "Grupos")
    )

    Spacer(modifier = Modifier.height(16.dp))

    EventCard(
        date = "22 jul - 25 jul",
        title = "Torneio Regional Basquetebol",
        tags = listOf("Basquetebol", "Em preparação", "Eliminatórias")
    )
}

@Composable
private fun PlayerHomeContent() {
    DashboardHeader(
        name = "Gonçalo Magalhães",
        initials = "GM",
        stats = listOf(
            DashboardStat("2", "Próximos jogos"),
            DashboardStat("19", "Golos"),
            DashboardStat("3", "Troféus")
        )
    )

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = "Próximo jogo")

    Spacer(modifier = Modifier.height(8.dp))

    LiveMatchCard(
        time = "Hoje - 10:00",
        teamA = "Equipa 1",
        teamB = "Equipa 2",
        scoreA = "-",
        scoreB = "-",
        status = "Agendado",
        minute = ""
    )

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = "As minhas equipas")

    Spacer(modifier = Modifier.height(8.dp))

    TeamCard(
        acronym = "EQP",
        name = "Equipa 1",
        sport = "Futebol",
        status = "A decorrer",
        statusColor = AthloColors.DangerBg,
        acronymColor = AthloColors.InfoBg
    )

    Spacer(modifier = Modifier.height(16.dp))

    TeamCard(
        acronym = "EQP",
        name = "Equipa 4",
        sport = "Voleibol",
        status = "Inscrito",
        statusColor = AthloColors.SoftBlue,
        acronymColor = Color(0xFFF8FFB0)
    )
}

@Composable
private fun DashboardHeader(
    name: String,
    initials: String,
    stats: List<DashboardStat>
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
                Column {
                    Text(
                        text = "Bom dia",
                        color = Color(0xFFBBD7EF),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(AthloColors.Blue, CircleShape),
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
    minute: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
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
    tags: List<String>
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

@Composable
private fun AthloBottomBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(28.dp))
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Início"
                )
            },
            label = { Text("Início") },
            colors = bottomBarItemColors()
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Events.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Eventos"
                )
            },
            label = { Text("Eventos") },
            colors = bottomBarItemColors()
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Teams.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "Equipas"
                )
            },
            label = { Text("Equipas") },
            colors = bottomBarItemColors()
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Stats.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Stats"
                )
            },
            label = { Text("Stats") },
            colors = bottomBarItemColors()
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Notifications.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações"
                )
            },
            label = { Text("Notif.") },
            colors = bottomBarItemColors()
        )
    }
}

@Composable
private fun bottomBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = AthloColors.Blue,
    selectedTextColor = AthloColors.Blue,
    indicatorColor = AthloColors.SoftBlue,
    unselectedIconColor = AthloColors.TextMuted,
    unselectedTextColor = AthloColors.TextMuted
)