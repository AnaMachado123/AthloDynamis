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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.domain.model.Team
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.TeamsViewModel

@Composable
fun TeamsScreen(navController: NavController) {
    val viewModel: TeamsViewModel = viewModel()
    val allTeams by viewModel.teams.collectAsState()

    var selectedFilter by remember { mutableStateOf("Todos") }

    val filters = listOf("Todos", "Futebol", "Basquetebol", "Voleibol", "Ténis")

    val teams = if (selectedFilter == "Todos") {
        allTeams
    } else {
        allTeams.filter { it.sport == selectedFilter }
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Teams.route
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                TeamsHeader()
            }

            item {
                PaddedContent {
                    SearchBox()
                }
            }

            item {
                PaddedContent {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filters.forEach { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AthloColors.Blue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            items(teams) { team ->
                PaddedContent {
                    TeamListCard(
                        team = team,
                        onClick = {
                            navController.navigate(Screen.TeamDetail.createRoute(team.id))
                        }
                    )
                }
            }

            item {
                PaddedContent {
                    Button(
                        onClick = {
                            navController.navigate(Screen.CreateTeam.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AthloColors.Blue
                        )
                    ) {
                        Text(
                            text = "Criar equipa",
                            color = Color.White,
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
private fun TeamsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .background(AthloColors.DarkNavy)
            .padding(horizontal = 24.dp, vertical = 22.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = "Equipas",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Lista de Equipas",
                color = Color(0xFF8EC5F4),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        AdminBadge(
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun AdminBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
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
private fun SearchBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF10243A), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Pesquisar",
                tint = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Pesquisar equipa...",
                color = Color(0xFF9CA3AF),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TeamListCard(
    team: Team,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.acronym,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = team.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallBadge(
                        text = "${team.playersCount} Jogadores",
                        background = AthloColors.InfoBg,
                        textColor = AthloColors.Blue
                    )

                    SmallBadge(
                        text = team.sport,
                        background = AthloColors.SuccessBg,
                        textColor = Color(0xFF3F7A28)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AthloColors.NeutralBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Abrir equipa",
                    tint = AthloColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun SmallBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}