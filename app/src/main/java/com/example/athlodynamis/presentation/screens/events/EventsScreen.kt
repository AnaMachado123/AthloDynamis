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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.data.mock.MockTournaments
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.navigation.Screen

@Composable
fun EventsScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    val tournaments = MockTournaments.tournaments.filter { tournament ->
        val matchesSearch =
            tournament.name.contains(searchText, ignoreCase = true) ||
                    tournament.sport.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Todos" -> true
            "Agendado" -> tournament.status == "Agendado"
            "A decorrer" -> tournament.status == "A decorrer"
            "Em preparação" -> tournament.status == "Em preparação"
            "Futebol" -> tournament.sport == "Futebol"
            "Basquetebol" -> tournament.sport == "Basquetebol"
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            EventsBottomBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                EventsHeader(
                    searchText = searchText,
                    onSearchChange = { searchText = it },
                    selectedFilter = selectedFilter,
                    onFilterClick = { selectedFilter = it },
                    total = MockTournaments.tournaments.size
                )
            }

            items(tournaments.size) { index ->
                TournamentCard(
                    tournament = tournaments[index],
                    onClick = {
                        navController.navigate(
                            Screen.TournamentDetail.createRoute(tournaments[index].id)
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun EventsHeader(
    searchText: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterClick: (String) -> Unit,
    total: Int
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
                Column {
                    Text(
                        text = "Eventos",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Eventos Disponíveis",
                            color = Color(0xFF8DC5F0),
                            style = MaterialTheme.typography.titleMedium
                        )

                        StatusPill(
                            text = "$total torneios",
                            background = AthloColors.SuccessBg,
                            textColor = Color(0xFF4D8B4A)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Pesquisar eventos...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = AthloColors.TextMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0xFF9CA3AF),
                        focusedContainerColor = Color(0xFF223344),
                        unfocusedContainerColor = Color(0xFF223344),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = AthloColors.TextMuted,
                        unfocusedPlaceholderColor = AthloColors.TextMuted,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                FilterRows(
                    selectedFilter = selectedFilter,
                    onFilterClick = onFilterClick
                )
            }
        }
    }
}

@Composable
private fun FilterRows(
    selectedFilter: String,
    onFilterClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipPill(
                text = "Todos",
                selected = selectedFilter == "Todos",
                onClick = { onFilterClick("Todos") }
            )

            FilterChipPill(
                text = "Agendado",
                selected = selectedFilter == "Agendado",
                onClick = { onFilterClick("Agendado") }
            )

            FilterChipPill(
                text = "A decorrer",
                selected = selectedFilter == "A decorrer",
                onClick = { onFilterClick("A decorrer") }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipPill(
                text = "Em preparação",
                selected = selectedFilter == "Em preparação",
                onClick = { onFilterClick("Em preparação") }
            )

            FilterChipPill(
                text = "Futebol",
                selected = selectedFilter == "Futebol",
                onClick = { onFilterClick("Futebol") }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipPill(
                text = "Basquetebol",
                selected = selectedFilter == "Basquetebol",
                onClick = { onFilterClick("Basquetebol") }
            )
        }
    }
}

@Composable
private fun FilterChipPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) Color.White else Color(0xFF203447)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) AthloColors.TextPrimary else Color(0xFF8DC5F0),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun TournamentCard(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tournament.dateRange,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = tournament.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                TournamentTags(tournament = tournament)
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(AthloColors.SoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Abrir evento",
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun TournamentTags(tournament: Tournament) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusPill(
                text = tournament.sport,
                background = sportColor(tournament.sport),
                textColor = sportTextColor(tournament.sport)
            )

            StatusPill(
                text = tournament.status,
                background = statusColor(tournament.status),
                textColor = statusTextColor(tournament.status)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusPill(
                text = tournament.format,
                background = AthloColors.NeutralBg,
                textColor = AthloColors.TextSecondary
            )
        }
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

private fun sportColor(sport: String): Color {
    return when (sport) {
        "Futsal" -> Color(0xFFD7EBFF)
        "Futebol" -> AthloColors.SuccessBg
        "Basquetebol" -> AthloColors.WarningBg
        else -> AthloColors.InfoBg
    }
}

private fun sportTextColor(sport: String): Color {
    return when (sport) {
        "Futsal" -> AthloColors.Blue
        "Futebol" -> Color(0xFF4D8B4A)
        "Basquetebol" -> Color(0xFF9A6B22)
        else -> AthloColors.Blue
    }
}

private fun statusColor(status: String): Color {
    return when (status) {
        "A decorrer" -> AthloColors.SuccessBg
        "Em preparação" -> AthloColors.WarningBg
        "Agendado" -> Color(0xFFD7EBFF)
        else -> AthloColors.NeutralBg
    }
}

private fun statusTextColor(status: String): Color {
    return when (status) {
        "A decorrer" -> Color(0xFF4D8B4A)
        "Em preparação" -> Color(0xFF9A6B22)
        "Agendado" -> AthloColors.Blue
        else -> AthloColors.TextSecondary
    }
}

@Composable
private fun EventsBottomBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(28.dp))
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screen.Home.route) },
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
            selected = true,
            onClick = { },
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