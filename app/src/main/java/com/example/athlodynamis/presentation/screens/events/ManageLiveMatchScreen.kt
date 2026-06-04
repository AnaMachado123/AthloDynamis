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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.domain.model.MatchEvent
import com.example.athlodynamis.domain.model.Player
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.MatchEventsViewModel
import com.example.athlodynamis.presentation.viewmodel.MatchesViewModel
import com.example.athlodynamis.presentation.viewmodel.PlayersViewModel
import kotlinx.coroutines.delay

private const val EVENT_GOAL = "Golo"
private const val EVENT_YELLOW_CARD = "Cartão amarelo"
private const val EVENT_RED_CARD = "Cartão vermelho"

private data class EventOption(
    val label: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageLiveMatchScreen(
    navController: NavController,
    matchId: String,
    userRole: AthloUserRole
) {
    val currentMatchId = matchId.toLongOrNull() ?: 0L

    val matchEventsViewModel: MatchEventsViewModel = viewModel()
    val matchesViewModel: MatchesViewModel = viewModel()

    val goalPlayersViewModel: PlayersViewModel = viewModel(key = "goal_players")
    val eventPlayersViewModel: PlayersViewModel = viewModel(key = "event_players")

    val selectedMatch by matchesViewModel.selectedMatch.collectAsState()

    val matchEvents by matchEventsViewModel.events.collectAsState()
    val isLoadingEvents by matchEventsViewModel.isLoading.collectAsState()
    val eventsError by matchEventsViewModel.error.collectAsState()

    val teamPlayers by goalPlayersViewModel.players.collectAsState()
    val isLoadingPlayers by goalPlayersViewModel.isLoading.collectAsState()

    val eventPlayers by eventPlayersViewModel.players.collectAsState()

    val scoreA = selectedMatch?.scoreA ?: 0
    val scoreB = selectedMatch?.scoreB ?: 0
    val teamAId = selectedMatch?.teamAId
    val teamBId = selectedMatch?.teamBId
    val teamAName = selectedMatch?.teamAName ?: "Equipa A"
    val teamBName = selectedMatch?.teamBName ?: "Equipa B"
    val matchStatus = selectedMatch?.status ?: "Agendado"
    val matchMinute = selectedMatch?.minute ?: 0
    val matchTime = selectedMatch?.matchTime?.ifBlank { null } ?: "Hora por definir"
    val matchLocation = selectedMatch?.location?.ifBlank { null }

    var liveMinute by remember {
        mutableStateOf(matchMinute)
    }

    var selectedEventMinute by remember {
        mutableStateOf(matchMinute)
    }

    var selectedEventType by remember {
        mutableStateOf(EVENT_GOAL)
    }

    var selectedEventTeam by remember { mutableStateOf(teamAName) }
    var selectedEventSide by remember { mutableStateOf("A") }
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }

    var showPlayerPicker by remember { mutableStateOf(false) }
    var showEventSuccess by remember { mutableStateOf(false) }

    var lastEventConfirmation by remember {
        mutableStateOf<LiveEventConfirmation?>(null)
    }

    val eventOptions = listOf(
        EventOption(EVENT_GOAL, "⚽"),
        EventOption(EVENT_YELLOW_CARD, "🟨"),
        EventOption(EVENT_RED_CARD, "🟥")
    )

    val playerNamesById = eventPlayers
        .filter { it.name.isNotBlank() }
        .associate { player ->
            player.id to player.name
        }

    LaunchedEffect(currentMatchId) {
        if (currentMatchId > 0) {
            matchesViewModel.loadMatchById(currentMatchId)
            matchEventsViewModel.loadEventsByMatch(currentMatchId.toInt())
        }
    }

    LaunchedEffect(selectedMatch?.minute) {
        liveMinute = selectedMatch?.minute ?: 0
    }

    LaunchedEffect(
        selectedMatch?.teamAId,
        selectedMatch?.teamBId
    ) {
        val currentTeamAId = selectedMatch?.teamAId?.toInt()
        val currentTeamBId = selectedMatch?.teamBId?.toInt()

        if (currentTeamAId != null || currentTeamBId != null) {
            eventPlayersViewModel.loadPlayersByTeams(
                teamAId = currentTeamAId,
                teamBId = currentTeamBId
            )
        }
    }

    LaunchedEffect(currentMatchId, matchStatus) {
        if (
            currentMatchId > 0 &&
            matchStatus.equals("A decorrer", ignoreCase = true)
        ) {
            while (true) {
                delay(60_000)

                if (!matchStatus.equals("A decorrer", ignoreCase = true)) {
                    break
                }

                if (liveMinute < 90) {
                    liveMinute += 1

                    matchesViewModel.updateMatchStatus(
                        matchId = currentMatchId,
                        status = "A decorrer",
                        minute = liveMinute
                    )
                } else {
                    break
                }
            }
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            ManageMatchHeader(
                matchId = currentMatchId.toString(),
                userRole = userRole,
                status = matchStatus,
                onBackClick = {
                    navController.popBackStack()
                }
            )

            LiveScoreCard(
                scoreA = scoreA,
                scoreB = scoreB,
                teamAName = teamAName,
                teamBName = teamBName,
                status = matchStatus,
                minute = liveMinute,
                matchTime = matchTime,
                matchLocation = matchLocation
            )

            if (!matchStatus.equals("A decorrer", ignoreCase = true)) {
                InfoCard(
                    text = "Este jogo não está a decorrer. Só é possível registar eventos quando o jogo está ativo."
                )
            }

            EventTypeSelector(
                selectedEventType = selectedEventType,
                options = eventOptions,
                onSelected = { selectedEventType = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TeamEventButton(
                    text = "${eventEmoji(selectedEventType)} $teamAName",
                    modifier = Modifier.weight(1f),
                    enabled = teamAId != null && matchStatus.equals("A decorrer", ignoreCase = true),
                    onClick = {
                        selectedEventTeam = teamAName
                        selectedEventSide = "A"
                        selectedPlayer = null
                        selectedEventMinute = liveMinute

                        teamAId?.let {
                            goalPlayersViewModel.loadPlayersByTeam(it.toInt())
                            showPlayerPicker = true
                        }
                    }
                )

                TeamEventButton(
                    text = "${eventEmoji(selectedEventType)} $teamBName",
                    modifier = Modifier.weight(1f),
                    enabled = teamBId != null && matchStatus.equals("A decorrer", ignoreCase = true),
                    onClick = {
                        selectedEventTeam = teamBName
                        selectedEventSide = "B"
                        selectedPlayer = null
                        selectedEventMinute = liveMinute

                        teamBId?.let {
                            goalPlayersViewModel.loadPlayersByTeam(it.toInt())
                            showPlayerPicker = true
                        }
                    }
                )
            }

            EventsOfGameCard(
                events = matchEvents,
                isLoading = isLoadingEvents,
                error = eventsError,
                teamAName = teamAName,
                teamBName = teamBName,
                playerNamesById = playerNamesById
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showPlayerPicker) {
        EventPlayerPickerSheet(
            team = selectedEventTeam,
            eventType = selectedEventType,
            minute = selectedEventMinute,
            players = teamPlayers,
            isLoading = isLoadingPlayers,
            selectedPlayer = selectedPlayer,
            onPlayerSelected = {
                selectedPlayer = it
            },
            onMinuteDecrease = {
                if (selectedEventMinute > 0) {
                    selectedEventMinute -= 1
                }
            },
            onMinuteIncrease = {
                if (selectedEventMinute < 120) {
                    selectedEventMinute += 1
                }
            },
            onDismiss = {
                showPlayerPicker = false
            },
            onConfirm = {
                val player = selectedPlayer ?: return@EventPlayerPickerSheet

                showPlayerPicker = false

                val isGoal = selectedEventType == EVENT_GOAL

                val newScoreA: Int
                val newScoreB: Int

                if (isGoal && selectedEventSide == "A") {
                    newScoreA = scoreA + 1
                    newScoreB = scoreB
                } else if (isGoal && selectedEventSide == "B") {
                    newScoreA = scoreA
                    newScoreB = scoreB + 1
                } else {
                    newScoreA = scoreA
                    newScoreB = scoreB
                }

                val confirmation = LiveEventConfirmation(
                    eventType = selectedEventType,
                    minute = selectedEventMinute,
                    playerId = player.id,
                    playerName = player.name,
                    team = selectedEventTeam
                )

                lastEventConfirmation = confirmation

                matchEventsViewModel.createMatchEvent(
                    matchId = currentMatchId.toInt(),
                    playerId = player.id,
                    eventType = selectedEventType,
                    minute = confirmation.minute,
                    teamSide = selectedEventSide,
                    onSuccess = {
                        if (isGoal) {
                            matchesViewModel.updateMatchScore(
                                matchId = currentMatchId,
                                scoreA = newScoreA,
                                scoreB = newScoreB,
                                minute = confirmation.minute,
                                onSuccess = {
                                    reloadLiveMatchData(
                                        currentMatchId = currentMatchId,
                                        matchesViewModel = matchesViewModel,
                                        matchEventsViewModel = matchEventsViewModel,
                                        eventPlayersViewModel = eventPlayersViewModel,
                                        teamAId = teamAId,
                                        teamBId = teamBId
                                    )

                                    showEventSuccess = true
                                }
                            )
                        } else {
                            reloadLiveMatchData(
                                currentMatchId = currentMatchId,
                                matchesViewModel = matchesViewModel,
                                matchEventsViewModel = matchEventsViewModel,
                                eventPlayersViewModel = eventPlayersViewModel,
                                teamAId = teamAId,
                                teamBId = teamBId
                            )

                            showEventSuccess = true
                        }
                    }
                )
            }
        )
    }

    if (showEventSuccess && lastEventConfirmation != null) {
        EventSuccessSheet(
            eventConfirmation = lastEventConfirmation!!,
            scoreA = scoreA,
            scoreB = scoreB,
            onClose = {
                lastEventConfirmation = null
                showEventSuccess = false
            },
            onContinue = {
                lastEventConfirmation = null
                showEventSuccess = false
            }
        )
    }
}

private fun reloadLiveMatchData(
    currentMatchId: Long,
    matchesViewModel: MatchesViewModel,
    matchEventsViewModel: MatchEventsViewModel,
    eventPlayersViewModel: PlayersViewModel,
    teamAId: Long?,
    teamBId: Long?
) {
    matchesViewModel.loadMatchById(currentMatchId)
    matchEventsViewModel.loadEventsByMatch(currentMatchId.toInt())

    eventPlayersViewModel.loadPlayersByTeams(
        teamAId = teamAId?.toInt(),
        teamBId = teamBId?.toInt()
    )
}

data class LiveEventConfirmation(
    val eventType: String,
    val minute: Int,
    val playerId: Int,
    val playerName: String,
    val team: String
)

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
private fun ManageMatchHeader(
    matchId: String,
    userRole: AthloUserRole,
    status: String,
    onBackClick: () -> Unit
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
                    modifier = Modifier.clickable {
                        onBackClick()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Gerir Jogo",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Jogo #$matchId · $status",
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (isAdmin) {
                AdminBadge(
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
private fun LiveScoreCard(
    scoreA: Int,
    scoreB: Int,
    teamAName: String,
    teamBName: String,
    status: String,
    minute: Int,
    matchTime: String,
    matchLocation: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = matchTime,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.bodySmall
            )

            if (!matchLocation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = matchLocation,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamSide(
                    acronym = teamAName.toAcronym(),
                    name = teamAName,
                    modifier = Modifier.weight(1f),
                    background = Color(0xFFF8FFB0),
                    textColor = Color(0xFFD4DD00)
                )

                ScoreBox(score = scoreA.toString())

                Text(
                    text = "vs",
                    color = AthloColors.TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                ScoreBox(score = scoreB.toString())

                TeamSide(
                    acronym = teamBName.toAcronym(),
                    name = teamBName,
                    modifier = Modifier.weight(1f),
                    background = Color(0xFFFFEFD7),
                    textColor = Color(0xFF9A6B22)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (status.equals("A decorrer", ignoreCase = true)) {
                Text(
                    text = "• $minute’",
                    color = Color(0xFFC83755),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            StatusPill(
                text = status,
                background = if (status.equals("A decorrer", ignoreCase = true)) {
                    AthloColors.DangerBg
                } else {
                    AthloColors.NeutralBg
                },
                textColor = if (status.equals("A decorrer", ignoreCase = true)) {
                    Color(0xFFC83755)
                } else {
                    AthloColors.TextSecondary
                }
            )
        }
    }
}

@Composable
private fun TeamSide(
    acronym: String,
    name: String,
    modifier: Modifier = Modifier,
    background: Color,
    textColor: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(background, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = acronym,
                color = textColor,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EventTypeSelector(
    selectedEventType: String,
    options: List<EventOption>,
    onSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Tipo de evento",
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { option ->
                    EventTypeButton(
                        option = option,
                        selected = selectedEventType == option.label,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onSelected(option.label)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventTypeButton(
    option: EventOption,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val background = if (selected) {
        AthloColors.Navy
    } else {
        AthloColors.NeutralBg
    }

    val textColor = if (selected) {
        Color.White
    } else {
        AthloColors.TextPrimary
    }

    Box(
        modifier = modifier
            .height(70.dp)
            .background(background, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = option.emoji,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = option.label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TeamEventButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AthloColors.Navy,
            disabledContainerColor = Color(0xFFAEB7C3)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EventsOfGameCard(
    events: List<MatchEvent>,
    isLoading: Boolean,
    error: String?,
    teamAName: String,
    teamBName: String,
    playerNamesById: Map<Int, String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFBFAF5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "Eventos do Jogo",
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(18.dp))

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
                        LiveEventRow(
                            event = event,
                            teamName = when (event.teamSide) {
                                "A" -> teamAName
                                "B" -> teamBName
                                else -> "Equipa indefinida"
                            },
                            playerName = event.playerId?.let { playerId ->
                                playerNamesById[playerId] ?: "Jogador não encontrado"
                            } ?: "Jogador não associado"
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveEventRow(
    event: MatchEvent,
    teamName: String,
    playerName: String
) {
    val colors = eventColors(event.eventType)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${event.minute ?: 0}'",
            color = AthloColors.Blue,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.width(42.dp)
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(colors.first, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = eventEmoji(event.eventType),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = event.eventType,
                color = AthloColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = playerName,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        StatusPill(
            text = teamName,
            background = if (event.teamSide == "A") {
                Color(0xFFF8FFB0)
            } else {
                Color(0xFFFFEFD7)
            },
            textColor = if (event.teamSide == "A") {
                Color(0xFFD4DD00)
            } else {
                Color(0xFF9A6B22)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventPlayerPickerSheet(
    team: String,
    eventType: String,
    minute: Int,
    players: List<Player>,
    isLoading: Boolean,
    selectedPlayer: Player?,
    onPlayerSelected: (Player) -> Unit,
    onMinuteDecrease: () -> Unit,
    onMinuteIncrease: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = eventType,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Seleciona o jogador e confirma o minuto do evento",
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            StatusPill(
                text = "$team · ${eventEmoji(eventType)}",
                background = AthloColors.SoftBlue,
                textColor = AthloColors.Blue
            )

            Spacer(modifier = Modifier.height(18.dp))

            MinuteSelector(
                minute = minute,
                onDecrease = onMinuteDecrease,
                onIncrease = onMinuteIncrease
            )

            Spacer(modifier = Modifier.height(18.dp))

            when {
                isLoading -> {
                    Text(
                        text = "A carregar jogadores da equipa...",
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                players.isEmpty() -> {
                    Text(
                        text = "Esta equipa ainda não tem jogadores associados.",
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    players.forEachIndexed { index, player ->
                        PlayerPickerRow(
                            position = index + 1,
                            player = player,
                            selected = selectedPlayer?.id == player.id,
                            onClick = {
                                onPlayerSelected(player)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onConfirm,
                enabled = selectedPlayer != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.Navy,
                    disabledContainerColor = Color(0xFFAEB7C3)
                )
            ) {
                Text(
                    text = selectedPlayer?.let {
                        "Confirmar - ${it.name} aos $minute'"
                    } ?: "Seleciona um jogador",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MinuteSelector(
    minute: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthloColors.NeutralBg, RoundedCornerShape(18.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Minuto do evento",
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onDecrease,
                enabled = minute > 0,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.CardWhite,
                    disabledContainerColor = Color(0xFFE5E7EB)
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "−",
                    color = AthloColors.Navy,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .background(AthloColors.Navy, RoundedCornerShape(16.dp))
                    .padding(horizontal = 28.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$minute'",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Button(
                onClick = onIncrease,
                enabled = minute < 120,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.CardWhite,
                    disabledContainerColor = Color(0xFFE5E7EB)
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "+",
                    color = AthloColors.Navy,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "O minuto live é sugerido, mas podes ajustar antes de guardar.",
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun PlayerPickerRow(
    position: Int,
    player: Player,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = position.toString(),
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(28.dp)
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    if (selected) {
                        AthloColors.Blue
                    } else {
                        Color(0xFFD7EBFF)
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = player.name.initials(),
                color = if (selected) {
                    Color.White
                } else {
                    AthloColors.Blue
                },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = player.name,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) {
                FontWeight.ExtraBold
            } else {
                FontWeight.Medium
            },
            modifier = Modifier.padding(start = 14.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE5E7EB))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventSuccessSheet(
    eventConfirmation: LiveEventConfirmation,
    scoreA: Int,
    scoreB: Int,
    onClose: () -> Unit,
    onContinue: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onContinue,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(
            topStart = 28.dp,
            topEnd = 28.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .background(AthloColors.SuccessBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Evento registado",
                    tint = Color(0xFF4D8B4A),
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Evento registado!",
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "O evento foi guardado no Supabase",
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .background(
                        AthloColors.NeutralBg,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = eventEmoji(eventConfirmation.eventType),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = eventConfirmation.playerName,
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${eventConfirmation.eventType} · ${eventConfirmation.team} · ${eventConfirmation.minute}'",
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = "$scoreA - $scoreB",
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onClose,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AthloColors.NeutralBg
                    )
                ) {
                    Text(
                        text = "Fechar",
                        color = AthloColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onContinue,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AthloColors.Navy
                    )
                ) {
                    Text(
                        text = "Continuar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ScoreBox(
    score: String
) {
    Box(
        modifier = Modifier
            .background(AthloColors.Navy, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = score,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
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

private fun eventEmoji(eventType: String): String {
    return when (eventType) {
        EVENT_GOAL -> "⚽"
        EVENT_YELLOW_CARD -> "🟨"
        EVENT_RED_CARD -> "🟥"
        else -> "•"
    }
}

private fun eventColors(eventType: String): Pair<Color, Color> {
    return when (eventType) {
        EVENT_GOAL -> AthloColors.SuccessBg to Color(0xFF3F7A28)
        EVENT_YELLOW_CARD -> Color(0xFFFFF7CC) to Color(0xFF9A6B22)
        EVENT_RED_CARD -> AthloColors.DangerBg to Color(0xFFC83755)
        else -> AthloColors.NeutralBg to AthloColors.TextSecondary
    }
}

private fun String.initials(): String {
    return split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") {
            it.first().uppercase()
        }
}

private fun String.toAcronym(): String {
    return split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") {
            it.first().uppercase()
        }
}