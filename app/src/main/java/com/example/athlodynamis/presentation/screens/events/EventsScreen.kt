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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.data.repository.MatchRepository
import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.TournamentsViewModel
import kotlinx.coroutines.launch

private const val FILTER_ALL = "Todos"
private const val FILTER_SCHEDULED = "Agendado"
private const val FILTER_LIVE = "A decorrer"
private const val FILTER_PREPARING = "Em preparação"
private const val SPORT_FOOTBALL = "Futebol"
private const val SPORT_BASKETBALL = "Basquetebol"
private const val SPORT_TENNIS = "Ténis"
private const val SPORT_VOLLEYBALL = "Voleibol"

@Composable
fun EventsScreen(
    navController: NavController,
    userRole: AthloUserRole,
    currentUserId: String = ""
) {
    val tournamentsViewModel: TournamentsViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val allTournaments by tournamentsViewModel.tournaments.collectAsState()
    val isLoading by tournamentsViewModel.isLoading.collectAsState()
    val error by tournamentsViewModel.error.collectAsState()

    var allMatches by remember {
        mutableStateOf<List<Match>>(emptyList())
    }

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FILTER_ALL) }
    var searchHistory by remember { mutableStateOf<List<String>>(emptyList()) }

    fun saveSearchHistory() {
        val query = searchText.trim()
        if (query.isNotBlank() && !searchHistory.contains(query)) {
            searchHistory = listOf(query) + searchHistory.take(4)
        }
    }

    val isAdmin = userRole == AthloUserRole.ADMIN
    val isOrganizer = userRole == AthloUserRole.ORGANIZER
    val canCreateEvent = isAdmin || isOrganizer

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                tournamentsViewModel.loadTournaments()

                scope.launch {
                    allMatches = MatchRepository(context).getAllMatches()
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val liveTournamentIds = allMatches
        .filter { match ->
            match.status.equals("A decorrer", ignoreCase = true) ||
                    match.status.equals("Live", ignoreCase = true)
        }
        .map { match ->
            match.tournamentId.toString()
        }
        .toSet()

    fun matchesSearchAndFilter(tournament: Tournament): Boolean {
        val matchesSearch =
            searchText.isBlank() ||
                    tournament.name.contains(searchText, ignoreCase = true) ||
                    tournament.sport.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            FILTER_ALL -> true

            FILTER_SCHEDULED -> {
                tournament.status.equals(FILTER_SCHEDULED, ignoreCase = true) ||
                        tournament.status.equals("Scheduled", ignoreCase = true)
            }

            FILTER_LIVE -> {
                tournament.id in liveTournamentIds ||
                        tournament.status.equals(FILTER_LIVE, ignoreCase = true) ||
                        tournament.status.equals("Live", ignoreCase = true)
            }

            FILTER_PREPARING -> {
                tournament.status.equals(FILTER_PREPARING, ignoreCase = true) ||
                        tournament.status.equals("Preparing", ignoreCase = true)
            }

            SPORT_FOOTBALL -> {
                tournament.sport.equals(SPORT_FOOTBALL, ignoreCase = true) ||
                        tournament.sport.equals("Football", ignoreCase = true)
            }

            SPORT_BASKETBALL -> {
                tournament.sport.equals(SPORT_BASKETBALL, ignoreCase = true) ||
                        tournament.sport.equals("Basketball", ignoreCase = true)
            }

            SPORT_TENNIS -> {
                tournament.sport.equals(SPORT_TENNIS, ignoreCase = true) ||
                        tournament.sport.equals("Tennis", ignoreCase = true)
            }

            SPORT_VOLLEYBALL -> {
                tournament.sport.equals(SPORT_VOLLEYBALL, ignoreCase = true) ||
                        tournament.sport.equals("Volleyball", ignoreCase = true)
            }

            else -> true
        }

        return matchesSearch && matchesFilter
    }

    val filteredTournaments = allTournaments.filter { matchesSearchAndFilter(it) }
    val myTournaments = filteredTournaments.filter { it.organizerId == currentUserId }
    val otherTournaments = filteredTournaments.filter { it.organizerId != currentUserId }

    val headerTotal = when (userRole) {
        AthloUserRole.ORGANIZER -> myTournaments.size
        else -> filteredTournaments.size
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
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                EventsHeader(
                    searchText = searchText,
                    onSearchChange = { searchText = it },
                    onSearchSubmit = { saveSearchHistory() },
                    searchHistory = searchHistory,
                    onHistoryClick = { searchText = it },
                    selectedFilter = selectedFilter,
                    onFilterClick = { selectedFilter = it },
                    total = headerTotal,
                    userRole = userRole,
                    onClearHistory = { searchHistory = emptyList() }
                )
            }

            if (isLoading) {
                item {
                    InfoCard(text = stringResource(R.string.events_loading))
                }
            } else if (error != null) {
                item {
                    InfoCard(text = error ?: stringResource(R.string.events_loading_error))
                }
            } else {
                if (isOrganizer || isAdmin) {
                    item {
                        EventsSectionTitle(
                            title = stringResource(R.string.events_my_events),
                            canCreateEvent = canCreateEvent,
                            onCreateEventClick = {
                                navController.navigate(Screen.CreateEvent.route)
                            }
                        )
                    }

                    if (myTournaments.isEmpty()) {
                        item {
                            InfoCard(
                                text = if (searchText.isNotBlank()) {
                                    stringResource(R.string.events_no_results_my, searchText)
                                } else {
                                    stringResource(R.string.events_no_created)
                                }
                            )
                        }
                    } else {
                        items(myTournaments) { tournament ->
                            TournamentCard(
                                tournament = tournament,
                                onClick = {
                                    navController.navigate(
                                        Screen.TournamentDetail.createRoute(tournament.id)
                                    )
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        EventsSectionTitle(
                            title = if (isAdmin) {
                                stringResource(R.string.events_all_events)
                            } else {
                                stringResource(R.string.events_other_events)
                            },
                            canCreateEvent = false,
                            onCreateEventClick = {}
                        )
                    }

                    val tournamentsToShow = if (isAdmin) {
                        filteredTournaments
                    } else {
                        otherTournaments
                    }

                    if (tournamentsToShow.isEmpty()) {
                        item {
                            InfoCard(
                                text = if (searchText.isNotBlank()) {
                                    stringResource(R.string.events_no_results, searchText)
                                } else if (isAdmin) {
                                    stringResource(R.string.events_no_tournaments)
                                } else {
                                    stringResource(R.string.events_no_other_tournaments)
                                }
                            )
                        }
                    } else {
                        items(tournamentsToShow) { tournament ->
                            TournamentCard(
                                tournament = tournament,
                                onClick = {
                                    navController.navigate(
                                        Screen.TournamentDetail.createRoute(tournament.id)
                                    )
                                }
                            )
                        }
                    }
                } else {
                    item {
                        EventsSectionTitle(
                            title = when (userRole) {
                                AthloUserRole.ADMIN -> stringResource(R.string.events_all_events)
                                AthloUserRole.PLAYER -> stringResource(R.string.events_available)
                                else -> stringResource(R.string.events_title)
                            },
                            canCreateEvent = canCreateEvent,
                            onCreateEventClick = {
                                navController.navigate(Screen.CreateEvent.route)
                            }
                        )
                    }

                    if (filteredTournaments.isEmpty()) {
                        item {
                            InfoCard(
                                text = if (searchText.isNotBlank()) {
                                    stringResource(R.string.events_no_results, searchText)
                                } else if (isAdmin) {
                                    stringResource(R.string.events_no_tournaments)
                                } else {
                                    stringResource(R.string.events_no_other_tournaments)
                                }
                            )
                        }
                    } else {
                        items(filteredTournaments) { tournament ->
                            TournamentCard(
                                tournament = tournament,
                                onClick = {
                                    navController.navigate(
                                        Screen.TournamentDetail.createRoute(tournament.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
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
private fun EventsHeader(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    searchHistory: List<String>,
    onHistoryClick: (String) -> Unit,
    onClearHistory: () -> Unit,
    selectedFilter: String,
    onFilterClick: (String) -> Unit,
    total: Int,
    userRole: AthloUserRole
) {
    val isAdmin = userRole == AthloUserRole.ADMIN
    val isOrganizer = userRole == AthloUserRole.ORGANIZER

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.events_title),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = when {
                                isAdmin -> stringResource(R.string.events_global_view, total)
                                isOrganizer -> stringResource(R.string.events_created_by_you, total)
                                else -> stringResource(R.string.events_available)
                            },
                            color = Color(0xFF8DC5F0),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (isAdmin) {
                        AdminBadge()
                    } else {
                        StatusPill(
                            text = if (isOrganizer) {
                                stringResource(R.string.events_my_count, total)
                            } else {
                                stringResource(R.string.events_tournaments_count, total)
                            },
                            background = AthloColors.SuccessBg,
                            textColor = Color(0xFF4D8B4A)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .background(AthloColors.DarkNavy)
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(stringResource(R.string.events_search))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.events_search_cd),
                            tint = AthloColors.TextMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearchSubmit() }
                    ),
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

                if (searchHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.events_search_history),
                            color = Color(0xFF8DC5F0),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = stringResource(R.string.events_clear),
                            color = Color(0xFF8DC5F0),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                onClearHistory()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchHistory) { query ->
                            FilterChipPill(
                                text = query,
                                selected = searchText == query,
                                onClick = {
                                    onHistoryClick(query)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    text = stringResource(R.string.events_filters),
                    color = Color(0xFF8DC5F0),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                FilterRows(
                    selectedFilter = selectedFilter,
                    onFilterClick = onFilterClick
                )
            }
        }
    }
}

@Composable
private fun EventsSectionTitle(
    title: String,
    canCreateEvent: Boolean,
    onCreateEventClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (canCreateEvent) {
            Button(
                onClick = onCreateEventClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.Blue
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.events_create),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(6.dp))

                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.events_create_cd),
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipPill(stringResource(R.string.filter_all), selectedFilter == FILTER_ALL) {
                onFilterClick(FILTER_ALL)
            }

            FilterChipPill(stringResource(R.string.filter_scheduled), selectedFilter == FILTER_SCHEDULED) {
                onFilterClick(FILTER_SCHEDULED)
            }

            FilterChipPill(stringResource(R.string.filter_live), selectedFilter == FILTER_LIVE) {
                onFilterClick(FILTER_LIVE)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipPill(stringResource(R.string.filter_preparing), selectedFilter == FILTER_PREPARING) {
                onFilterClick(FILTER_PREPARING)
            }

            FilterChipPill(stringResource(R.string.sport_football), selectedFilter == SPORT_FOOTBALL) {
                onFilterClick(SPORT_FOOTBALL)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChipPill(stringResource(R.string.sport_basketball), selectedFilter == SPORT_BASKETBALL) {
                onFilterClick(SPORT_BASKETBALL)
            }

            FilterChipPill(stringResource(R.string.sport_tennis), selectedFilter == SPORT_TENNIS) {
                onFilterClick(SPORT_TENNIS)
            }

            FilterChipPill(stringResource(R.string.sport_volleyball), selectedFilter == SPORT_VOLLEYBALL) {
                onFilterClick(SPORT_VOLLEYBALL)
            }
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
                    contentDescription = stringResource(R.string.events_open),
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(
                text = localizedSportName(tournament.sport),
                background = sportColor(tournament.sport),
                textColor = sportTextColor(tournament.sport)
            )

            StatusPill(
                text = localizedStatusName(tournament.status),
                background = statusColor(tournament.status),
                textColor = statusTextColor(tournament.status)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPill(
                text = localizedTournamentFormat(tournament.format),
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
            contentDescription = stringResource(R.string.admin_badge),
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.size(4.dp))

        Text(
            text = stringResource(R.string.admin_badge),
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun localizedSportName(sport: String): String {
    return when (sport) {
        SPORT_FOOTBALL -> stringResource(R.string.sport_football)
        SPORT_BASKETBALL -> stringResource(R.string.sport_basketball)
        SPORT_TENNIS -> stringResource(R.string.sport_tennis)
        SPORT_VOLLEYBALL -> stringResource(R.string.sport_volleyball)
        else -> sport
    }
}

@Composable
private fun localizedStatusName(status: String): String {
    return when {
        status.equals(FILTER_SCHEDULED, ignoreCase = true) ||
                status.equals("Scheduled", ignoreCase = true) ->
            stringResource(R.string.filter_scheduled)

        status.equals(FILTER_LIVE, ignoreCase = true) ||
                status.equals("Live", ignoreCase = true) ->
            stringResource(R.string.filter_live)

        status.equals(FILTER_PREPARING, ignoreCase = true) ||
                status.equals("Preparing", ignoreCase = true) ->
            stringResource(R.string.filter_preparing)

        else -> status
    }
}

@Composable
fun localizedTournamentFormat(format: String): String {
    return when (format.lowercase()) {
        "liga" -> stringResource(R.string.tournament_format_league)
        "grupo" -> stringResource(R.string.tournament_format_group)
        "eliminatórias" -> stringResource(R.string.tournament_format_knockout)
        "eliminatorias" -> stringResource(R.string.tournament_format_knockout)
        else -> format
    }
}

private fun sportColor(sport: String): Color {
    return when (sport) {
        "Futsal" -> Color(0xFFD7EBFF)
        SPORT_FOOTBALL -> AthloColors.SuccessBg
        SPORT_BASKETBALL -> AthloColors.WarningBg
        SPORT_TENNIS -> AthloColors.InfoBg
        SPORT_VOLLEYBALL -> AthloColors.SuccessBg
        else -> AthloColors.InfoBg
    }
}

private fun sportTextColor(sport: String): Color {
    return when (sport) {
        "Futsal" -> AthloColors.Blue
        SPORT_FOOTBALL -> Color(0xFF4D8B4A)
        SPORT_BASKETBALL -> Color(0xFF9A6B22)
        SPORT_TENNIS -> AthloColors.Blue
        SPORT_VOLLEYBALL -> Color(0xFF4D8B4A)
        else -> AthloColors.Blue
    }
}

private fun statusColor(status: String): Color {
    return when {
        status.equals(FILTER_LIVE, ignoreCase = true) ||
                status.equals("Live", ignoreCase = true) ->
            AthloColors.SuccessBg

        status.equals(FILTER_PREPARING, ignoreCase = true) ||
                status.equals("Preparing", ignoreCase = true) ->
            AthloColors.WarningBg

        status.equals(FILTER_SCHEDULED, ignoreCase = true) ||
                status.equals("Scheduled", ignoreCase = true) ->
            Color(0xFFD7EBFF)

        else -> AthloColors.NeutralBg
    }
}

private fun statusTextColor(status: String): Color {
    return when {
        status.equals(FILTER_LIVE, ignoreCase = true) ||
                status.equals("Live", ignoreCase = true) ->
            Color(0xFF4D8B4A)

        status.equals(FILTER_PREPARING, ignoreCase = true) ||
                status.equals("Preparing", ignoreCase = true) ->
            Color(0xFF9A6B22)

        status.equals(FILTER_SCHEDULED, ignoreCase = true) ||
                status.equals("Scheduled", ignoreCase = true) ->
            AthloColors.Blue

        else -> AthloColors.TextSecondary
    }
}