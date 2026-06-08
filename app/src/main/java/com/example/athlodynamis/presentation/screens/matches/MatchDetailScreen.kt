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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
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
import com.example.athlodynamis.domain.model.MatchEvent
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.MatchEventsViewModel
import com.example.athlodynamis.presentation.viewmodel.MatchesViewModel
import com.example.athlodynamis.presentation.viewmodel.NotificationsViewModel
import com.example.athlodynamis.presentation.viewmodel.PlayersViewModel
import com.example.athlodynamis.presentation.components.AthloBackButton
private const val EVENT_GOAL = "Golo"
private const val EVENT_ASSIST = "Assistência"
private const val EVENT_YELLOW_CARD = "Cartão amarelo"
private const val EVENT_RED_CARD = "Cartão vermelho"
private const val EVENT_SUBSTITUTION = "Substituição"

@Composable
fun MatchDetailScreen(
    matchId: String,
    navController: NavController,
    userRole: AthloUserRole
) {
    val matchesViewModel: MatchesViewModel = viewModel()
    val matchEventsViewModel: MatchEventsViewModel = viewModel()
    val playersViewModel: PlayersViewModel = viewModel()
    val notificationsViewModel: NotificationsViewModel = viewModel()

    val match by matchesViewModel.selectedMatch.collectAsState()
    val error by matchesViewModel.error.collectAsState()

    val players by playersViewModel.players.collectAsState()

    val matchEvents by matchEventsViewModel.events.collectAsState()
    val eventsError by matchEventsViewModel.error.collectAsState()
    val isLoadingEvents by matchEventsViewModel.isLoading.collectAsState()

    val isAdmin = userRole == AthloUserRole.ADMIN
    val canManageMatch = userRole == AthloUserRole.ADMIN ||
            userRole == AthloUserRole.ORGANIZER

    val matchIdLong = matchId.toLongOrNull()
    val matchIdInt = matchId.toIntOrNull()

    LaunchedEffect(matchId) {
        if (matchIdLong != null) {
            matchesViewModel.loadMatchById(matchIdLong)
        }

        if (matchIdInt != null) {
            matchEventsViewModel.loadEventsByMatch(matchIdInt)
        }
    }

    val currentMatch = match

    LaunchedEffect(
        currentMatch?.teamAId,
        currentMatch?.teamBId
    ) {
        if (currentMatch != null) {
            playersViewModel.loadPlayersByTeams(
                teamAId = currentMatch.teamAId?.toInt(),
                teamBId = currentMatch.teamBId?.toInt()
            )
        }
    }

    val playerNamesById = players
        .filter { it.name.isNotBlank() }
        .associate { player ->
            player.id to player.name
        }

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
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditClick = {
                        navController.navigate(
                            Screen.EditMatch.createRoute(matchId)
                        )
                    }
                )
            }

            when {
                matchIdLong == null -> {
                    item {
                        InfoCard(text = "ID do jogo inválido.")
                    }
                }

                error != null -> {
                    item {
                        InfoCard(text = error ?: "Erro ao carregar jogo")
                    }
                }

                currentMatch == null -> {
                    item {
                        InfoCard(text = "A carregar jogo...")
                    }
                }

                else -> {
                    item {
                        MatchScoreSection(match = currentMatch)
                    }

                    item {
                        Divider(color = Color(0xFFD7D7D7))
                    }

                    item {
                        MatchEventsCard(
                            match = currentMatch,
                            events = matchEvents,
                            isLoading = isLoadingEvents,
                            error = eventsError,
                            playerNamesById = playerNamesById
                        )
                    }

                    if (canManageMatch) {
                        when {
                            currentMatch.status.equals("Agendado", ignoreCase = true) -> {
                                item {
                                    Button(
                                        onClick = {
                                            matchesViewModel.updateMatchStatus(
                                                matchId = currentMatch.id,
                                                status = "A decorrer",
                                                minute = 0,
                                                onSuccess = {
                                                    matchesViewModel.loadMatchById(currentMatch.id)
                                                }
                                            )
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
                                            text = "Iniciar jogo",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            currentMatch.status.equals("A decorrer", ignoreCase = true) -> {
                                item {
                                    Button(
                                        onClick = {
                                            navController.navigate(
                                                Screen.ManageLiveMatch.createRoute(matchId)
                                            )
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

                                item {
                                    Button(
                                        onClick = {
                                            matchesViewModel.updateMatchStatus(
                                                matchId = currentMatch.id,
                                                status = "Terminado",
                                                minute = currentMatch.minute ?: 90,
                                                onSuccess = {
                                                    notificationsViewModel.createNotification(
                                                        title = matchFinishedNotificationTitle(currentMatch),
                                                        message = matchFinishedNotificationMessage(currentMatch)
                                                    )

                                                    matchesViewModel.loadMatchById(currentMatch.id)
                                                }
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFCC1F2F)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                    ) {
                                        Text(
                                            text = "Terminar jogo",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            currentMatch.status.equals("Terminado", ignoreCase = true) -> {
                                item {
                                    InfoCard(text = "Jogo terminado. Resultado final registado.")
                                }
                            }
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

                AthloBackButton(
                    onClick = {
                        onBackClick()
                    }
                )

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
    val timeText = match.matchTime?.ifBlank { null } ?: "Hora por definir"
    val minuteText = match.minute?.let { "${it}'" }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hora: $timeText",
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
                acronym = getAcronym(match.teamAName),
                teamName = match.teamAName,
                background = Color(0xFFD7EBFF),
                textColor = AthloColors.Blue
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreDarkBox(match.scoreA.toString())

                Text(
                    text = "vs",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                ScoreDarkBox(match.scoreB.toString())
            }

            TeamBlock(
                acronym = getAcronym(match.teamBName),
                teamName = match.teamBName,
                background = Color(0xFFE8F3DD),
                textColor = Color(0xFF5F9E6E)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (
            match.status.equals("A decorrer", ignoreCase = true) &&
            minuteText != null
        ) {
            Text(
                text = "• $minuteText",
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
    val background = when {
        status.equals("A decorrer", ignoreCase = true) -> AthloColors.DangerBg
        status.equals("Agendado", ignoreCase = true) -> Color(0xFFDCEBFF)
        else -> AthloColors.Navy
    }

    val textColor = when {
        status.equals("A decorrer", ignoreCase = true) -> Color(0xFFC83755)
        status.equals("Agendado", ignoreCase = true) -> AthloColors.Blue
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
private fun MatchEventsCard(
    match: Match,
    events: List<MatchEvent>,
    isLoading: Boolean,
    error: String?,
    playerNamesById: Map<Int, String>
) {
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

            when {
                isLoading -> {
                    Text(
                        text = "A carregar eventos...",
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                error != null -> {
                    Text(
                        text = error,
                        color = Color(0xFFCC1F2F),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                events.isEmpty() && match.status.equals("Agendado", ignoreCase = true) -> {
                    EmptyEventsState()
                }

                events.isEmpty() -> {
                    Text(
                        text = "Ainda não existem eventos registados.",
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    val sortedEvents = events.sortedWith(
                        compareByDescending<MatchEvent> { it.minute ?: 0 }
                            .thenByDescending { it.id }
                    )

                    sortedEvents.forEach { event ->
                        EventRow(
                            eventType = event.eventType,
                            minute = "${event.minute ?: 0}'",
                            playerName = event.playerId?.let { playerId ->
                                playerNamesById[playerId] ?: "Jogador não encontrado"
                            } ?: "Jogador não associado",
                            secondaryPlayerName = event.secondaryPlayerId?.let { playerId ->
                                playerNamesById[playerId] ?: "Jogador não encontrado"
                            },
                            team = when (event.teamSide) {
                                "A" -> match.teamAName
                                "B" -> match.teamBName
                                else -> "Equipa indefinida"
                            },
                            teamColor = when (event.teamSide) {
                                "A" -> Color(0xFFD7EBFF)
                                "B" -> Color(0xFFE8F3DD)
                                else -> AthloColors.NeutralBg
                            }
                        )
                    }
                }
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
private fun EventRow(
    eventType: String,
    minute: String,
    playerName: String,
    secondaryPlayerName: String?,
    team: String,
    teamColor: Color
) {
    val colors = eventColors(eventType)

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
                .background(colors.first, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = eventEmoji(eventType),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = eventType,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = if (eventType == EVENT_SUBSTITUTION) {
                    "Entrou: $playerName · Saiu: ${secondaryPlayerName ?: "-"}"
                } else {
                    playerName
                },
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

private fun eventEmoji(eventType: String): String {
    return when (eventType) {
        EVENT_GOAL -> "⚽"
        EVENT_ASSIST -> "🎯"
        EVENT_YELLOW_CARD -> "🟨"
        EVENT_RED_CARD -> "🟥"
        EVENT_SUBSTITUTION -> "🔁"
        else -> "•"
    }
}

private fun eventColors(eventType: String): Pair<Color, Color> {
    return when (eventType) {
        EVENT_GOAL -> Color(0xFFDFF3D8) to Color(0xFF3F7A28)
        EVENT_ASSIST -> AthloColors.SoftBlue to AthloColors.Blue
        EVENT_YELLOW_CARD -> Color(0xFFFFF7CC) to Color(0xFF9A6B22)
        EVENT_RED_CARD -> AthloColors.DangerBg to Color(0xFFC83755)
        EVENT_SUBSTITUTION -> Color(0xFFE3D7FF) to Color(0xFF6A3FCB)
        else -> AthloColors.NeutralBg to AthloColors.TextSecondary
    }
}

private fun matchFinishedNotificationTitle(match: Match): String {
    return when {
        match.scoreA > match.scoreB -> "Vitória de ${match.teamAName}"
        match.scoreB > match.scoreA -> "Vitória de ${match.teamBName}"
        else -> "Empate registado"
    }
}

private fun matchFinishedNotificationMessage(match: Match): String {
    return when {
        match.scoreA > match.scoreB -> {
            "${match.teamAName} venceu ${match.teamBName} por ${match.scoreA} - ${match.scoreB}."
        }

        match.scoreB > match.scoreA -> {
            "${match.teamBName} venceu ${match.teamAName} por ${match.scoreB} - ${match.scoreA}."
        }

        else -> {
            "${match.teamAName} e ${match.teamBName} empataram ${match.scoreA} - ${match.scoreB}."
        }
    }
}