package com.example.athlodynamis.presentation.screens.home

import android.content.Context
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.data.remote.dto.UserDto
import com.example.athlodynamis.data.repository.MatchRepository
import com.example.athlodynamis.data.repository.PlayerRepository
import com.example.athlodynamis.data.repository.StatsRepository
import com.example.athlodynamis.data.repository.TeamRepository
import com.example.athlodynamis.data.repository.TournamentRepository
import com.example.athlodynamis.data.repository.UserRepository
import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.domain.model.Team
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.OfflineViewModel

data class DashboardStat(
    val value: String,
    val label: String
)

@Composable
fun HomeScreen(
    navController: NavController,
    userRole: AthloUserRole,
    userName: String = "",
    userId: String,
    playerTeamId: Int? = null
) {
    val offlineViewModel: OfflineViewModel = viewModel()
    val context = LocalContext.current

    val isOnline by offlineViewModel.isOnline.collectAsState()
    val pendingOperationsCount by offlineViewModel.pendingOperationsCount.collectAsState()

    val displayedUserName = userName.ifBlank {
        stringResource(R.string.home_default_user)
    }

    Scaffold(
        containerColor = AthloColors.Background,
        floatingActionButton = {
            if (userRole == AthloUserRole.ORGANIZER) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.CreateTeam.route)
                    },
                    containerColor = AthloColors.Blue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_create_team)
                    )
                }
            }
        },
        bottomBar = {
            AthloBottomBar(
                navController = navController,
                currentRoute = Screen.Home.route,
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                if (pendingOperationsCount > 0) {
                    PendingSyncBanner(count = pendingOperationsCount)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                when (userRole) {
                    AthloUserRole.ADMIN -> AdminHomeContent(
                        navController = navController,
                        userName = displayedUserName,
                        context = context,
                        isOnline = isOnline
                    )

                    AthloUserRole.ORGANIZER -> OrganizerHomeContent(
                        navController = navController,
                        userName = displayedUserName,
                        userId = userId,
                        context = context,
                        isOnline = isOnline
                    )

                    AthloUserRole.PLAYER -> PlayerHomeContent(
                        navController = navController,
                        userName = displayedUserName,
                        userId = userId,
                        playerTeamId = playerTeamId,
                        isOnline = isOnline,
                        context = context
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

/* ---------------------------------------------------------
   ADMIN HOME
--------------------------------------------------------- */

@Composable
private fun AdminHomeContent(
    navController: NavController,
    userName: String,
    context: Context,
    isOnline: Boolean
) {
    val errorLoadingAdmin = stringResource(R.string.home_error_loading_admin)
    val errorLoadingData = stringResource(R.string.home_error_loading_data)

    var users by remember {
        mutableStateOf<List<UserDto>>(emptyList())
    }

    var tournamentsCount by remember {
        mutableStateOf(0)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var showOfflineWarning by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isOnline) {
        isLoading = true
        errorMessage = null

        if (!isOnline) {
            users = emptyList()
            tournamentsCount = 0
            showOfflineWarning = true
            isLoading = false
            return@LaunchedEffect
        }

        showOfflineWarning = false

        try {
            users = UserRepository().getAllUsers()
            tournamentsCount = TournamentRepository(context).getTournaments().size
        } catch (e: Exception) {
            users = emptyList()
            tournamentsCount = 0
            errorMessage = e.message ?: errorLoadingAdmin

            val message = errorMessage.orEmpty()
            showOfflineWarning =
                message.contains("Unable to resolve host", ignoreCase = true) ||
                        message.contains("No address associated with hostname", ignoreCase = true) ||
                        message.contains("failed to connect", ignoreCase = true) ||
                        message.contains("timeout", ignoreCase = true)
        } finally {
            isLoading = false
        }
    }

    val organizersCount = users.count {
        it.role.equals("ORGANIZER", ignoreCase = true)
    }

    val playersCount = users.count {
        it.role.equals("PLAYER", ignoreCase = true)
    }

    val adminsCount = users.count {
        it.role.equals("ADMIN", ignoreCase = true)
    }

    val pendingRequestsCount = users.count {
        it.role.equals("ORGANIZER", ignoreCase = true) &&
                it.approvalStatus.equals("PENDING", ignoreCase = true)
    }

    val recentUsers = users
        .sortedByDescending { it.createdAt ?: "" }
        .take(4)

    val adminInitials = userName.initials().ifBlank { "AD" }

    DashboardHeader(
        name = userName.ifBlank { stringResource(R.string.home_admin_default_name) },
        initials = adminInitials,
        stats = listOf(
            DashboardStat(
                value = if (isLoading) "..." else tournamentsCount.toString(),
                label = stringResource(R.string.home_events)
            ),
            DashboardStat(
                value = if (isLoading) "..." else users.size.toString(),
                label = stringResource(R.string.home_users)
            ),
            DashboardStat(
                value = if (isLoading) "..." else organizersCount.toString(),
                label = stringResource(R.string.home_organizers)
            )
        ),
        showAdminBadge = true,
        onProfileClick = {
            navController.navigate(Screen.Profile.route)
        }
    )

    Spacer(modifier = Modifier.height(28.dp))

    if (showOfflineWarning) {
        OfflineWarningCard()
        Spacer(modifier = Modifier.height(22.dp))
    } else if (errorMessage != null) {
        AdminErrorCard(
            text = errorMessage ?: errorLoadingData
        )

        Spacer(modifier = Modifier.height(22.dp))
    }

    PendingRequestsCard(
        pendingCount = pendingRequestsCount,
        onClick = {
            navController.navigate(Screen.PendingRequests.route)
        }
    )

    Spacer(modifier = Modifier.height(26.dp))

    SectionTitle(title = stringResource(R.string.home_platform_summary))

    Spacer(modifier = Modifier.height(8.dp))

    AdminPlatformSummaryCard(
        playersCount = playersCount,
        organizersCount = organizersCount,
        adminsCount = adminsCount,
        tournamentsCount = tournamentsCount,
        isLoading = isLoading
    )

    Spacer(modifier = Modifier.height(26.dp))

    SectionTitle(title = stringResource(R.string.home_recent_registrations))

    Spacer(modifier = Modifier.height(8.dp))

    RecentRegistrationsCard(
        users = recentUsers,
        isLoading = isLoading
    )
}

@Composable
private fun PendingRequestsCard(
    pendingCount: Int,
    onClick: () -> Unit
) {
    val pendingText = if (pendingCount == 1) {
        stringResource(R.string.home_pending_request_single)
    } else {
        stringResource(R.string.home_pending_request_multiple, pendingCount)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7CC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFFFD928), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = stringResource(R.string.cd_pending_requests),
                    tint = Color(0xFF6B5A00),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = pendingText,
                    color = Color(0xFF7A5B00),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(R.string.home_organizer_approval),
                    color = Color(0xFFB48A00),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.cd_pending_requests),
                tint = Color(0xFFB48A00),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun AdminPlatformSummaryCard(
    playersCount: Int,
    organizersCount: Int,
    adminsCount: Int,
    tournamentsCount: Int,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AdminSummaryRow(
                label = stringResource(R.string.home_players),
                value = if (isLoading) "..." else playersCount.toString(),
                color = Color(0xFFD7EBFF)
            )

            AdminSummaryRow(
                label = stringResource(R.string.home_organizers),
                value = if (isLoading) "..." else organizersCount.toString(),
                color = Color(0xFFDFF3D8)
            )

            AdminSummaryRow(
                label = stringResource(R.string.home_admins),
                value = if (isLoading) "..." else adminsCount.toString(),
                color = Color(0xFFFFF7CC)
            )

            AdminSummaryRow(
                label = stringResource(R.string.home_tournaments_events),
                value = if (isLoading) "..." else tournamentsCount.toString(),
                color = Color(0xFFE3D7FF)
            )
        }
    }
}

@Composable
private fun AdminSummaryRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                color = AthloColors.Navy,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Text(
            text = label,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
private fun AdminErrorCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE5E5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFCC1F2F),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(18.dp)
        )
    }
}

@Composable
private fun RecentRegistrationsCard(
    users: List<UserDto>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            when {
                isLoading -> {
                    Text(
                        text = stringResource(R.string.home_loading_users),
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                users.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.home_no_users),
                        color = AthloColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    users.forEachIndexed { index, user ->
                        RecentUserRow(
                            position = index + 1,
                            user = user
                        )

                        if (index < users.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFE5E7EB))
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentUserRow(
    position: Int,
    user: UserDto
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = position.toString(),
            color = AthloColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(28.dp)
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(user.role.roleColor(), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.initials(),
                color = AthloColors.Blue,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                text = user.name,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = user.email,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Box(
            modifier = Modifier
                .background(AthloColors.NeutralBg, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.role,
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ---------------------------------------------------------
   ORGANIZER HOME
--------------------------------------------------------- */

@Composable
private fun OrganizerHomeContent(
    navController: NavController,
    userName: String,
    userId: String,
    context: Context,
    isOnline: Boolean
) {
    var tournaments by remember { mutableStateOf<List<Tournament>>(emptyList()) }
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var athletesCount by remember { mutableStateOf(0) }
    var showOfflineWarning by remember { mutableStateOf(false) }

    LaunchedEffect(userId, isOnline) {
        if (!isOnline) {
            tournaments = emptyList()
            matches = emptyList()
            athletesCount = 0
            showOfflineWarning = true
            return@LaunchedEffect
        }

        showOfflineWarning = false

        try {
            tournaments = TournamentRepository(context).getTournaments()
            matches = MatchRepository(context).getAllMatches()
            athletesCount = PlayerRepository().getAllPlayers().size
        } catch (e: Exception) {
            tournaments = emptyList()
            matches = emptyList()
            athletesCount = 0

            val message = e.message.orEmpty()
            showOfflineWarning =
                message.contains("Unable to resolve host", ignoreCase = true) ||
                        message.contains("No address associated with hostname", ignoreCase = true) ||
                        message.contains("failed to connect", ignoreCase = true) ||
                        message.contains("timeout", ignoreCase = true)
        }
    }

    val myEvents = tournaments.filter { tournament ->
        tournament.organizerId == userId
    }

    val otherEvents = tournaments.filter { tournament ->
        tournament.organizerId != userId
    }

    val myEventIds = myEvents.map { it.id }.toSet()

    val myMatches = matches.filter { match ->
        match.tournamentId.toString() in myEventIds
    }

    val activeTournaments = myEvents.count {
        !it.status.equals("Terminado", ignoreCase = true)
    }

    val activeMatches = myMatches.count {
        it.status.equals("Agendado", ignoreCase = true) ||
                it.status.equals("A decorrer", ignoreCase = true)
    }

    val liveMatch = myMatches.firstOrNull {
        it.status.equals("A decorrer", ignoreCase = true)
    }

    val organizerInitials = userName.initials().ifBlank { "ORG" }

    DashboardHeader(
        name = userName.ifBlank { stringResource(R.string.home_organizer_default_name) },
        initials = organizerInitials,
        stats = listOf(
            DashboardStat(
                activeTournaments.toString(),
                stringResource(R.string.home_active_tournaments)
            ),
            DashboardStat(
                activeMatches.toString(),
                stringResource(R.string.home_active_matches)
            ),
            DashboardStat(
                athletesCount.toString(),
                stringResource(R.string.home_athletes)
            )
        ),
        showAdminBadge = false,
        onProfileClick = {
            navController.navigate(Screen.Profile.route)
        }
    )

    if (showOfflineWarning) {
        Spacer(modifier = Modifier.height(22.dp))
        OfflineWarningCard()
    }

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = stringResource(R.string.home_live))

    Spacer(modifier = Modifier.height(8.dp))

    if (liveMatch == null) {
        EmptyLiveMatchCard()
    } else {
        LiveMatchCard(
            time = liveMatch.matchTime ?: stringResource(R.string.home_time_undefined),
            teamA = liveMatch.teamAName,
            teamB = liveMatch.teamBName,
            scoreA = liveMatch.scoreA.toString(),
            scoreB = liveMatch.scoreB.toString(),
            status = liveMatch.status,
            minute = "${liveMatch.minute ?: 0}'",
            onClick = {
                navController.navigate(
                    Screen.MatchDetail.createRoute(liveMatch.id.toString())
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(22.dp))

    SectionTitle(title = stringResource(R.string.home_my_events))

    Spacer(modifier = Modifier.height(8.dp))

    if (myEvents.isEmpty()) {
        EmptyOrganizerEventsCard()
    } else {
        myEvents.forEach { tournament ->
            EventCard(
                date = tournament.dateRange,
                title = tournament.name,
                tags = listOf(
                    tournament.sport,
                    tournament.status,
                    tournament.format
                ),
                onClick = {
                    navController.navigate(
                        Screen.TournamentDetail.createRoute(tournament.id)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    SectionTitle(title = stringResource(R.string.home_other_events))

    Spacer(modifier = Modifier.height(8.dp))

    if (otherEvents.isEmpty()) {
        EmptyOtherEventsCard()
    } else {
        otherEvents.take(5).forEach { tournament ->
            EventCard(
                date = tournament.dateRange,
                title = tournament.name,
                tags = listOf(
                    tournament.sport,
                    tournament.status,
                    tournament.format
                ),
                onClick = {
                    navController.navigate(
                        Screen.TournamentDetail.createRoute(tournament.id)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/* ---------------------------------------------------------
   PLAYER HOME
--------------------------------------------------------- */

@Composable
private fun PlayerHomeContent(
    navController: NavController,
    userName: String,
    userId: String,
    playerTeamId: Int?,
    isOnline: Boolean,
    context: Context
) {
    val initials = userName.initials().ifBlank { "J" }

    var playerTeam by remember {
        mutableStateOf<Team?>(null)
    }

    var playerMatches by remember {
        mutableStateOf<List<Match>>(emptyList())
    }

    var nextMatch by remember {
        mutableStateOf<Match?>(null)
    }

    var playerGoals by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(playerTeamId, isOnline) {
        if (!isOnline) {
            playerTeam = null
            playerMatches = emptyList()
            nextMatch = null
            playerGoals = 0
            return@LaunchedEffect
        }

        if (playerTeamId != null) {
            TeamRepository.fetchTeamsFromSupabase()
            playerTeam = TeamRepository.getTeamById(playerTeamId)

            val matches = MatchRepository(context).getMatchesByTeamId(playerTeamId)

            playerMatches = matches

            nextMatch = matches
                .filter { match ->
                    match.status.equals("Agendado", ignoreCase = true) ||
                            match.status.equals("A decorrer", ignoreCase = true)
                }
                .sortedBy { it.id }
                .firstOrNull()
        } else {
            playerTeam = null
            playerMatches = emptyList()
            nextMatch = null
        }

        if (userId.isNotBlank()) {
            runCatching {
                StatsRepository().getPlayerStatsByUserId(userId)
            }.onSuccess { stats ->
                playerGoals = stats.goals
            }.onFailure {
                playerGoals = 0
            }
        }
    }

    DashboardHeader(
        name = userName,
        initials = initials,
        stats = listOf(
            DashboardStat(
                playerMatches.count {
                    it.status.equals("Agendado", ignoreCase = true) ||
                            it.status.equals("A decorrer", ignoreCase = true)
                }.toString(),
                stringResource(R.string.home_next_games)
            ),
            DashboardStat(
                playerGoals.toString(),
                stringResource(R.string.home_goals)
            ),
            DashboardStat(
                playerTeam?.wins?.toString() ?: "0",
                stringResource(R.string.home_trophies)
            )
        ),
        showAdminBadge = false,
        onProfileClick = {
            navController.navigate(Screen.Profile.route)
        }
    )

    Spacer(modifier = Modifier.height(22.dp))

    if (playerTeamId == null) {
        EmptyPlayerHomeCard()
    } else {
        SectionTitle(title = stringResource(R.string.home_next_match))

        Spacer(modifier = Modifier.height(8.dp))

        if (!isOnline) {
            PlayerOfflineNextMatchCard()
        } else if (nextMatch == null) {
            EmptyNextMatchCard()
        } else {
            PlayerNextMatchCard(
                match = nextMatch!!,
                onClick = {
                    navController.navigate(
                        Screen.MatchDetail.createRoute(nextMatch!!.id.toString())
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        SectionTitle(title = stringResource(R.string.home_my_teams))

        Spacer(modifier = Modifier.height(8.dp))

        TeamCard(
            acronym = playerTeam?.acronym ?: "EQP",
            name = playerTeam?.name ?: stringResource(R.string.home_team_with_id, playerTeamId),
            sport = playerTeam?.sport ?: stringResource(R.string.home_associated_team),
            status = stringResource(R.string.home_registered),
            statusColor = AthloColors.SoftBlue,
            acronymColor = AthloColors.InfoBg,
            onClick = {
                navController.navigate(Screen.TeamDetail.createRoute(playerTeamId))
            }
        )
    }
}

@Composable
private fun PlayerNextMatchCard(
    match: Match,
    onClick: () -> Unit
) {
    val timeText = match.matchTime?.ifBlank { null }
        ?: stringResource(R.string.home_time_undefined)

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
                text = timeText,
                color = AthloColors.TextMuted,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamMiniBadge(
                    label = match.teamAName.toAcronym(),
                    color = Color(0xFFFFEFD7)
                )

                Text(
                    text = match.teamAName,
                    fontWeight = FontWeight.Bold,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )

                ScoreBox(score = match.scoreA.toString())

                Text(
                    text = "-",
                    color = AthloColors.TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                ScoreBox(score = match.scoreB.toString())

                Text(
                    text = match.teamBName,
                    fontWeight = FontWeight.Bold,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            StatusPill(
                text = match.status,
                background = if (match.status.equals("A decorrer", ignoreCase = true)) {
                    AthloColors.DangerBg
                } else {
                    AthloColors.NeutralBg
                },
                textColor = if (match.status.equals("A decorrer", ignoreCase = true)) {
                    Color(0xFFC83755)
                } else {
                    AthloColors.TextSecondary
                }
            )
        }
    }
}

/* ---------------------------------------------------------
   SHARED COMPONENTS
--------------------------------------------------------- */

@Composable
private fun EmptyPlayerHomeCard() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionTitle(title = stringResource(R.string.home_my_sport_profile))

        Spacer(modifier = Modifier.height(8.dp))

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
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = stringResource(R.string.cd_no_team),
                    tint = AthloColors.TextMuted,
                    modifier = Modifier.size(58.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.home_no_team),
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.home_no_team_desc),
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EmptyNextMatchCard() {
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
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = stringResource(R.string.cd_no_next_match),
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.home_no_scheduled_matches),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.home_no_scheduled_matches_desc),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DashboardHeader(
    name: String,
    initials: String,
    stats: List<DashboardStat>,
    showAdminBadge: Boolean,
    onProfileClick: () -> Unit
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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.home_good_morning),
                        color = Color(0xFFBBD7EF),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )

                        if (showAdminBadge) {
                            Spacer(modifier = Modifier.width(8.dp))
                            AdminBadge()
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(AthloColors.Blue, CircleShape)
                        .clickable { onProfileClick() },
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
private fun AdminBadge() {
    Row(
        modifier = Modifier
            .background(Color(0xFFFFD928), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Admin",
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(13.dp)
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
                    label = teamA.toAcronym(),
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

                TeamMiniBadge(
                    label = teamB.toAcronym(),
                    color = AthloColors.SoftBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
    tags: List<String>,
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
                    contentDescription = stringResource(R.string.cd_open_event),
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
    acronymColor: Color,
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
                textColor = if (status.equals("A decorrer", ignoreCase = true)) {
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
                    contentDescription = stringResource(R.string.cd_open_team),
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
private fun EmptyLiveMatchCard() {
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
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = stringResource(R.string.cd_no_live_matches),
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.home_no_live_matches),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.home_no_live_matches_desc),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyOrganizerEventsCard() {
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
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = stringResource(R.string.cd_no_events),
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.home_no_created_events),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.home_no_created_events_desc),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyOtherEventsCard() {
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
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = stringResource(R.string.cd_no_other_events),
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.home_no_other_events),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.home_no_other_events_desc),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OfflineWarningCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7CC)
        ),
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

private fun String.toAcronym(): String {
    val parts = trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        parts.isEmpty() -> "EQP"
        parts.size == 1 -> parts.first().take(3).uppercase()
        else -> parts.take(2).joinToString("") {
            it.first().uppercase()
        }
    }
}

private fun String.initials(): String {
    return split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") {
            it.first().uppercase()
        }
        .ifBlank { "U" }
}

private fun String.roleColor(): Color {
    return when {
        equals("ADMIN", ignoreCase = true) -> Color(0xFFFFD928)
        equals("ORGANIZER", ignoreCase = true) -> Color(0xFFDFF3D8)
        equals("PLAYER", ignoreCase = true) -> Color(0xFFD7EBFF)
        else -> Color(0xFFE3D7FF)
    }
}

@Composable
private fun PendingSyncBanner(count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7CC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = stringResource(R.string.home_pending_sync, count),
            modifier = Modifier.padding(14.dp),
            color = Color(0xFF7A5B00),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun PlayerOfflineNextMatchCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(
            containerColor = AthloColors.Background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SignalWifiOff,
                    contentDescription = stringResource(R.string.cd_offline),
                    tint = AthloColors.Navy,
                    modifier = Modifier.size(34.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = stringResource(R.string.home_offline),
                    color = AthloColors.Navy,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}