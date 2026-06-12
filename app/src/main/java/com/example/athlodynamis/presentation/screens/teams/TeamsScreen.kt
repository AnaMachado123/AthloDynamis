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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalWifiOff
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.athlodynamis.R
import com.example.athlodynamis.data.repository.PlayerRepository
import com.example.athlodynamis.domain.model.Team
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.FavoriteTeamsViewModel
import com.example.athlodynamis.presentation.viewmodel.OfflineViewModel
import com.example.athlodynamis.presentation.viewmodel.TeamsViewModel

@Composable
fun TeamsScreen(
    navController: NavController,
    userRole: AthloUserRole,
    currentUserId: String
) {
    val viewModel: TeamsViewModel = viewModel()
    val allTeams by viewModel.teams.collectAsState()

    val favoriteViewModel: FavoriteTeamsViewModel = viewModel()
    val favoriteTeamIds by favoriteViewModel.favoriteTeamIds.collectAsState()

    val offlineViewModel: OfflineViewModel = viewModel()
    val isOnline by offlineViewModel.isOnline.collectAsState()

    var playersCountByTeam by remember {
        mutableStateOf<Map<Int, Int>>(emptyMap())
    }

    LaunchedEffect(isOnline) {
        if (!isOnline) {
            playersCountByTeam = emptyMap()
            return@LaunchedEffect
        }

        runCatching {
            PlayerRepository().getAllPlayers()
        }.onSuccess { players ->
            playersCountByTeam = players
                .filter { it.teamId != null }
                .groupingBy { it.teamId ?: 0 }
                .eachCount()
        }.onFailure {
            playersCountByTeam = emptyMap()
        }
    }

    LaunchedEffect(currentUserId, userRole, isOnline) {
        if (!isOnline) {
            return@LaunchedEffect
        }

        if (userRole == AthloUserRole.PLAYER) {
            runCatching {
                favoriteViewModel.loadFavorites(currentUserId)
            }
        }
    }

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var searchHistory by remember { mutableStateOf<List<String>>(emptyList()) }

    fun saveSearchHistory() {
        val query = searchText.trim()

        if (query.isNotBlank() && !searchHistory.contains(query)) {
            searchHistory = listOf(query) + searchHistory.take(4)
        }
    }

    val isAdmin = userRole == AthloUserRole.ADMIN
    val canCreateTeam = userRole == AthloUserRole.ADMIN || userRole == AthloUserRole.ORGANIZER

    val sports = allTeams
        .map { it.sport }
        .distinct()
        .sorted()

    val filteredTeams = allTeams.filter { team ->
        val matchesSearch =
            team.name.contains(searchText, ignoreCase = true) ||
                    team.acronym.contains(searchText, ignoreCase = true) ||
                    team.sport.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Todos" -> true
            else -> team.sport == selectedFilter
        }

        matchesSearch && matchesFilter
    }

    val favoriteTeams = filteredTeams.filter { team ->
        favoriteTeamIds.contains(team.id)
    }

    val myTeams = filteredTeams.filter {
        it.createdBy == currentUserId
    }

    Scaffold(
        containerColor = AthloColors.Background,
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Teams.route,
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

                TeamsHeader(
                    totalTeams = if (isOnline) allTeams.size else 0,
                    totalSports = if (isOnline) sports.size else 0,
                    isAdmin = isAdmin
                )
            }

            if (!isOnline) {
                item {
                    OfflineTeamsCard()
                }

                return@LazyColumn
            }

            item {
                SearchBox(
                    searchText = searchText,
                    onSearchChange = { searchText = it },
                    onSearchSubmit = { saveSearchHistory() }
                )
            }

            if (searchHistory.isNotEmpty()) {
                item {
                    SearchHistorySection(
                        searchHistory = searchHistory,
                        selectedSearch = searchText,
                        onHistoryClick = { searchText = it },
                        onClearHistory = {
                            searchHistory = emptyList()
                        }
                    )
                }
            }

            item {
                TeamFilterRows(
                    sports = sports,
                    selectedFilter = selectedFilter,
                    onFilterClick = { selectedFilter = it }
                )
            }

            if (userRole == AthloUserRole.PLAYER && favoriteTeams.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.teams_favorites),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AthloColors.TextPrimary
                    )
                }

                items(favoriteTeams) { team ->
                    TeamListCard(
                        team = team,
                        playersCount = playersCountByTeam[team.id] ?: 0,
                        isFavorite = favoriteTeamIds.contains(team.id),
                        showFavoriteButton = userRole == AthloUserRole.PLAYER,
                        onFavoriteClick = {
                            favoriteViewModel.toggleFavorite(
                                userId = currentUserId,
                                teamId = team.id
                            )
                        },
                        onClick = {
                            navController.navigate(
                                Screen.TeamDetail.createRoute(team.id)
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.teams_all),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AthloColors.TextPrimary
                    )
                }
            }

            if (isAdmin || userRole == AthloUserRole.ORGANIZER) {
                item {
                    Text(
                        text = stringResource(R.string.teams_mine),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AthloColors.TextPrimary
                    )
                }

                if (myTeams.isEmpty()) {
                    item {
                        EmptyTeamsCard()
                    }
                } else {
                    items(myTeams) { team ->
                        TeamListCard(
                            team = team,
                            playersCount = playersCountByTeam[team.id] ?: 0,
                            isFavorite = favoriteTeamIds.contains(team.id),
                            showFavoriteButton = userRole == AthloUserRole.PLAYER,
                            onFavoriteClick = {
                                favoriteViewModel.toggleFavorite(
                                    userId = currentUserId,
                                    teamId = team.id
                                )
                            },
                            onClick = {
                                navController.navigate(
                                    Screen.TeamDetail.createRoute(team.id)
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.teams_all),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AthloColors.TextPrimary
                    )
                }
            }

            if (filteredTeams.isEmpty()) {
                item {
                    EmptyTeamsCard()
                }
            } else {
                items(filteredTeams) { team ->
                    TeamListCard(
                        team = team,
                        playersCount = playersCountByTeam[team.id] ?: 0,
                        isFavorite = favoriteTeamIds.contains(team.id),
                        showFavoriteButton = userRole == AthloUserRole.PLAYER,
                        onFavoriteClick = {
                            favoriteViewModel.toggleFavorite(
                                userId = currentUserId,
                                teamId = team.id
                            )
                        },
                        onClick = {
                            navController.navigate(
                                Screen.TeamDetail.createRoute(team.id)
                            )
                        }
                    )
                }
            }

            if (canCreateTeam) {
                item {
                    Button(
                        onClick = {
                            navController.navigate(Screen.CreateTeam.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AthloColors.Blue
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.teams_create_cd),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.teams_create),
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
private fun TeamsHeader(
    totalTeams: Int,
    totalSports: Int,
    isAdmin: Boolean
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.teams_title),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = stringResource(R.string.teams_list_title),
                            color = Color(0xFF8EC5F4),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (isAdmin) {
                        AdminBadge()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                HeaderStatBox(
                    value = totalTeams.toString(),
                    label = stringResource(R.string.teams_title),
                    modifier = Modifier.weight(1f)
                )

                HeaderStatBox(
                    value = totalSports.toString(),
                    label = stringResource(R.string.teams_sports),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeaderStatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(74.dp)
            .background(
                color = Color(0xFF244A70),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = Color(0xFFC8DCEF),
                style = MaterialTheme.typography.labelSmall
            )
        }
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
            contentDescription = stringResource(R.string.admin_badge),
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.admin_badge),
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
private fun SearchBox(
    searchText: String,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: () -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchChange,
        placeholder = { Text(stringResource(R.string.teams_search_hint)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.teams_search_cd),
                tint = AthloColors.TextMuted
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchSubmit()
            }
        ),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AthloColors.Blue,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = AthloColors.Blue,
            focusedTextColor = AthloColors.TextPrimary,
            unfocusedTextColor = AthloColors.TextPrimary,
            focusedPlaceholderColor = AthloColors.TextMuted,
            unfocusedPlaceholderColor = AthloColors.TextMuted
        )
    )
}

@Composable
private fun SearchHistorySection(
    searchHistory: List<String>,
    selectedSearch: String,
    onHistoryClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.teams_search_history),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.teams_clear),
                color = AthloColors.Blue,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onClearHistory()
                }
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchHistory) { query ->
                FilterPill(
                    text = query,
                    selected = selectedSearch == query,
                    onClick = {
                        onHistoryClick(query)
                    }
                )
            }
        }
    }
}

@Composable
private fun TeamFilterRows(
    sports: List<String>,
    selectedFilter: String,
    onFilterClick: (String) -> Unit
) {
    val filters = listOf("Todos") + sports

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.chunked(3).forEach { rowFilters ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowFilters.forEach { filter ->
                    FilterPill(
                        text = localizedTeamFilter(filter),
                        selected = selectedFilter == filter,
                        onClick = { onFilterClick(filter) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) AthloColors.Blue else Color.White
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else AthloColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun TeamListCard(
    team: Team,
    playersCount: Int,
    isFavorite: Boolean = false,
    showFavoriteButton: Boolean = false,
    onFavoriteClick: () -> Unit = {},
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
            TeamLogoBox(team.logoUrl)

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.acronym,
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = team.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallBadge(
                        text = stringResource(R.string.teams_players_count, playersCount),
                        background = AthloColors.InfoBg,
                        textColor = AthloColors.Blue
                    )

                    SmallBadge(
                        text = localizedSportName(team.sport),
                        background = sportColor(team.sport),
                        textColor = sportTextColor(team.sport)
                    )
                }
            }

            if (showFavoriteButton) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFavorite) Color(0xFFFFF3D6) else AthloColors.SoftBlue
                        )
                        .clickable {
                            onFavoriteClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.teams_favorite_cd),
                        tint = if (isFavorite) Color(0xFFFFC107) else AthloColors.TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AthloColors.SoftBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = stringResource(R.string.teams_open_cd),
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TeamLogoBox(
    logoUrl: String?
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(
                AthloColors.SoftBlue,
                RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!logoUrl.isNullOrBlank()) {
            AsyncImage(
                model = logoUrl,
                contentDescription = stringResource(R.string.teams_logo_cd),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = stringResource(R.string.teams_team_cd),
                tint = AthloColors.Blue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun EmptyTeamsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(AthloColors.SoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.teams_empty_cd),
                    tint = AthloColors.Blue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.teams_empty_title),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.teams_empty_desc),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun OfflineTeamsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7CC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SignalWifiOff,
                contentDescription = stringResource(R.string.cd_offline),
                tint = Color(0xFF7A5B00),
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = stringResource(R.string.home_offline),
                    color = Color(0xFF7A5B00),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.home_offline_desc),
                    color = Color(0xFF9A7800),
                    style = MaterialTheme.typography.bodySmall
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
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}


@Composable
private fun localizedTeamFilter(filter: String): String {
    return when (filter) {
        "Todos" -> stringResource(R.string.filter_all)
        else -> localizedSportName(filter)
    }
}

@Composable
private fun localizedSportName(sport: String): String {
    return when (sport) {
        "Futebol" -> stringResource(R.string.sport_football)
        "Basquetebol" -> stringResource(R.string.sport_basketball)
        "Voleibol" -> stringResource(R.string.sport_volleyball)
        "Ténis" -> stringResource(R.string.sport_tennis)
        else -> sport
    }
}

private fun sportColor(sport: String): Color {
    return when (sport) {
        "Futebol" -> AthloColors.SuccessBg
        "Basquetebol" -> AthloColors.WarningBg
        "Voleibol" -> AthloColors.InfoBg
        "Ténis" -> AthloColors.SoftBlue
        else -> AthloColors.NeutralBg
    }
}

private fun sportTextColor(sport: String): Color {
    return when (sport) {
        "Futebol" -> Color(0xFF3F7A28)
        "Basquetebol" -> Color(0xFF9A6B22)
        "Voleibol" -> AthloColors.Blue
        "Ténis" -> AthloColors.Blue
        else -> AthloColors.TextSecondary
    }
}