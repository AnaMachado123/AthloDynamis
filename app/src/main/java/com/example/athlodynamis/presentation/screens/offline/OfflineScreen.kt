package com.example.athlodynamis.presentation.screens.offline

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun OfflineScreen(navController: NavController) {
    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Home.route,
                userRole = AthloUserRole.PLAYER
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

                OfflinePlayerHeader()
            }

            item {
                SectionTitle(title = "Próximo jogo")
            }

            item {
                NoInternetCard()
            }

            item {
                SectionTitle(title = "As minhas equipas")
            }

            item {
                OfflineTeamCard(
                    acronym = "EQP",
                    name = "Equipa 1",
                    sport = "Futebol",
                    status = "A decorrer",
                    badgeBackground = AthloColors.DangerBg,
                    badgeText = Color(0xFFC83755),
                    logoBackground = Color(0xFFD7EBFF),
                    logoText = AthloColors.Blue
                )
            }

            item {
                OfflineTeamCard(
                    acronym = "EQP",
                    name = "Equipa 4",
                    sport = "Voleibol",
                    status = "Inscrito",
                    badgeBackground = AthloColors.SoftBlue,
                    badgeText = AthloColors.Blue,
                    logoBackground = Color(0xFFF7FFA6),
                    logoText = Color(0xFFD4DD00)
                )
            }
        }
    }
}

@Composable
private fun OfflinePlayerHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.DarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .background(AthloColors.Navy)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Bom dia",
                        color = Color(0xFF8EC5F4),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Gonçalo Magalhães",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(AthloColors.Blue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GM",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HeaderStatCard(
                    value = "2",
                    label = "Próximos jogos",
                    modifier = Modifier.weight(1f)
                )

                HeaderStatCard(
                    value = "19",
                    label = "Golos",
                    modifier = Modifier.weight(1f)
                )

                HeaderStatCard(
                    value = "3",
                    label = "Troféus",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeaderStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(104.dp)
            .background(
                color = Color(0xFF2D4F70),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                color = Color(0xFF8EC5F4),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AthloColors.TextMuted,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun NoInternetCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SignalWifiOff,
                contentDescription = "Sem internet",
                tint = AthloColors.Navy,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = "SEM LIGAÇÃO À INTERNET",
                color = AthloColors.Navy,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun OfflineTeamCard(
    acronym: String,
    name: String,
    sport: String,
    status: String,
    badgeBackground: Color,
    badgeText: Color,
    logoBackground: Color,
    logoText: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    .size(58.dp)
                    .background(logoBackground, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = acronym,
                    color = logoText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 18.dp)
            ) {
                Text(
                    text = name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = sport,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            StatusBadge(
                text = status,
                background = badgeBackground,
                textColor = badgeText
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Abrir equipa",
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}