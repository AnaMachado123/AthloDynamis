package com.example.athlodynamis.presentation.screens.profile

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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
fun ProfileScreen(
    navController: NavController,
    userRole: AthloUserRole
) {
    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Profile.route,
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

                ProfileHeader(
                    userRole = userRole,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = {
                        navController.navigate(Screen.EditProfile.route)
                    }
                )
            }

            when (userRole) {
                AthloUserRole.PLAYER -> {
                    item {
                        PlayerProfileTabs()
                    }
                }

                AthloUserRole.ORGANIZER -> {
                    item {
                        SectionTitle("Contacto")
                    }

                    item {
                        ContactCard()
                    }

                    item {
                        SectionTitle("Eventos associados")
                    }

                    item {
                        AssociatedEventCard()
                    }

                    item {
                        LogoutButton()
                    }
                }

                AthloUserRole.ADMIN -> {
                    item {
                        SectionTitle("Contacto")
                    }

                    item {
                        ContactCard()
                    }

                    item {
                        SectionTitle("Eventos associados")
                    }

                    item {
                        AssociatedEventCard()
                    }

                    item {
                        SuspendOrganizerButton()
                    }

                    item {
                        LogoutButton()
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userRole: AthloUserRole,
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
        Column(
            modifier = Modifier
                .background(AthloColors.Navy)
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "‹ voltar",
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBackClick() }
                )

                if (isAdmin) {
                    AdminBadge()
                } else {
                    Text(
                        text = "Terminar Sessão",
                        color = Color(0xFF8EC5F4),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Perfil",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(AthloColors.Blue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GM",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.White, CircleShape)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar perfil",
                        tint = AthloColors.Blue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Guilherme Magalhães",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            RolePill(userRole = userRole)

            Spacer(modifier = Modifier.height(22.dp))

            ProfileStatsRow(userRole = userRole)
        }
    }
}

@Composable
private fun ProfileStatsRow(userRole: AthloUserRole) {
    val stats = when (userRole) {
        AthloUserRole.PLAYER -> listOf(
            "48" to "Jogos",
            "6" to "Troféus",
            "4" to "Equipas"
        )

        AthloUserRole.ORGANIZER -> listOf(
            "12" to "Eventos",
            "96" to "Jogos",
            "348" to "Atletas"
        )

        AthloUserRole.ADMIN -> listOf(
            "12" to "Eventos",
            "96" to "Jogos",
            "348" to "Atletas"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF132A42), RoundedCornerShape(18.dp))
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        stats.forEachIndexed { index, stat ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stat.first,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = stat.second,
                    color = Color(0xFFC8DCEF),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (index < stats.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .width(1.dp)
                        .background(Color(0xFF365675))
                )
            }
        }
    }
}

@Composable
private fun RolePill(userRole: AthloUserRole) {
    val text = when (userRole) {
        AthloUserRole.PLAYER -> "Jogador"
        AthloUserRole.ORGANIZER -> "Organizador"
        AthloUserRole.ADMIN -> "Administrador"
    }

    Box(
        modifier = Modifier
            .background(Color(0xFF294F76), RoundedCornerShape(999.dp))
            .padding(horizontal = 24.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFC8DCEF),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PlayerProfileTabs() {
    var selectedTab by remember { mutableStateOf("Estatísticas") }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(18.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ProfileTabButton(
                text = "Estatísticas",
                selected = selectedTab == "Estatísticas",
                onClick = { selectedTab = "Estatísticas" },
                modifier = Modifier.weight(1f)
            )

            ProfileTabButton(
                text = "Equipas",
                selected = selectedTab == "Equipas",
                onClick = { selectedTab = "Equipas" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (selectedTab == "Estatísticas") {
            LastGamesCard()
        } else {
            TeamsCard()
        }

        Spacer(modifier = Modifier.height(18.dp))

        LogoutButton()
    }
}

@Composable
private fun ProfileTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (selected) AthloColors.SoftBlue else Color.White,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) AthloColors.Blue else AthloColors.TextPrimary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ContactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(AthloColors.SoftBlue, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Email",
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = "guilherme@example.com",
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LastGamesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            SectionSmallTitle("ÚLTIMOS JOGOS")

            Spacer(modifier = Modifier.height(16.dp))

            GameRow("V", "SC Virius vs GD São", "3-1", AthloColors.SuccessBg, Color(0xFF3F7A28))
            GameRow("E", "SC Virius vs GD São", "1-1", AthloColors.NeutralBg, AthloColors.TextSecondary)
            GameRow("D", "SC Virius vs GD São", "0-2", AthloColors.DangerBg, Color(0xFFC83755))
            GameRow("E", "SC Virius vs GD São", "2-2", AthloColors.NeutralBg, AthloColors.TextSecondary)
        }
    }
}

@Composable
private fun TeamsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            SectionSmallTitle("EQUIPAS INSCRITAS")

            Spacer(modifier = Modifier.height(16.dp))

            TeamProfileRow("SCV", "SC Virius", "Futsal", "A decorrer", AthloColors.DangerBg)
            TeamProfileRow("GDM", "GD Monção", "Voleibol", "Inscrito", AthloColors.SuccessBg)
            TeamProfileRow("AFC", "AF Cinfães", "Futebol", "Terminado", AthloColors.NeutralBg)
        }
    }
}

@Composable
private fun TeamProfileRow(
    acronym: String,
    name: String,
    sport: String,
    status: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(AthloColors.SoftBlue, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = acronym,
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
                text = name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = sport,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Box(
            modifier = Modifier
                .background(statusColor, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = status,
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GameRow(
    result: String,
    opponent: String,
    score: String,
    resultColor: Color,
    textColor: Color
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
                color = textColor,
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
                text = "Torneio de Braga · 16/04/2026",
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Text(
            text = score,
            color = AthloColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AssociatedEventCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "10 abr - 25 abr",
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Torneio de Braga",
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallBadge("Futebol", AthloColors.SuccessBg)
                    SmallBadge("A decorrer", AthloColors.SuccessBg)
                    SmallBadge("Grupos", AthloColors.NeutralBg)
                }
            }
        }
    }
}

@Composable
private fun SmallBadge(
    text: String,
    background: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AthloColors.TextSecondary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SectionSmallTitle(title: String) {
    Text(
        text = title,
        color = AthloColors.TextMuted,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SuspendOrganizerButton() {
    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE84D4D)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Suspender organizador",
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Suspender organizador",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LogoutButton() {
    Button(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AthloColors.Navy
        )
    ) {
        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = "Terminar sessão",
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Terminar sessão",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
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