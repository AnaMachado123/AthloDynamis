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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.athlodynamis.R
import com.example.athlodynamis.domain.model.Player
import com.example.athlodynamis.domain.model.Team
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.PlayersViewModel
import com.example.athlodynamis.presentation.viewmodel.TeamsViewModel
import com.example.athlodynamis.data.repository.MatchRepository
import com.example.athlodynamis.data.repository.TournamentRepository
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.data.repository.TeamStatsRepository
import com.example.athlodynamis.domain.model.TeamStatsData
import com.example.athlodynamis.presentation.components.AthloBackButton

@Composable
fun TeamDetailScreen(
    navController: NavController,
    teamId: Int,
    userRole: AthloUserRole
) {
    val viewModel: TeamsViewModel = viewModel()
    val playersViewModel: PlayersViewModel = viewModel()

    val teams by viewModel.teams.collectAsState()
    val players by playersViewModel.players.collectAsState()
    val team = teams.firstOrNull { it.id == teamId }

    val isAdmin = userRole == AthloUserRole.ADMIN
    val canManageTeam = userRole == AthloUserRole.ADMIN || userRole == AthloUserRole.ORGANIZER
    val lifecycleOwner = LocalLifecycleOwner.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var registeredTournaments by remember {
        mutableStateOf<List<Tournament>>(emptyList())
    }
    var teamStats by remember {
        mutableStateOf(
            TeamStatsData(
                games = 0,
                wins = 0,
                goals = 0
            )
        )
    }

    LaunchedEffect(viewModel.teamDeleted) {
        if (viewModel.teamDeleted) {
            viewModel.resetTeamDeleted()
            navController.popBackStack()
        }
    }

    LaunchedEffect(teamId) {
        playersViewModel.loadPlayersByTeam(teamId)

        val teamMatches = MatchRepository().getMatchesByTeamId(teamId)

        val tournamentIds = teamMatches
            .map { it.tournamentId.toString() }
            .distinct()

        registeredTournaments = TournamentRepository()
            .getTournaments()
            .filter { tournament ->
                tournament.id in tournamentIds
            }

        teamStats = TeamStatsRepository().getTeamStats(teamId)
    }

    DisposableEffect(lifecycleOwner, teamId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                playersViewModel.loadPlayersByTeam(teamId)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (team == null) {
        TeamNotFoundScreen(
            onBackClick = { navController.popBackStack() }
        )
        return
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = AthloColors.CardWhite,
            titleContentColor = AthloColors.TextPrimary,
            textContentColor = AthloColors.TextSecondary,
            title = {
                Text(
                    text = stringResource(R.string.team_detail_delete_title),
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Text(stringResource(R.string.team_detail_delete_confirm))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteTeam(team.id)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.team_detail_delete),
                        color = Color(0xFFCC1F2F),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        text = stringResource(R.string.create_team_cancel),
                        color = AthloColors.Blue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
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

                DetailHeader(
                    title = stringResource(R.string.teams_title),
                    subtitle = stringResource(R.string.team_detail_subtitle),
                    isAdmin = isAdmin,
                    onBackClick = { navController.popBackStack() }
                )
            }

            item {
                TeamIdentityCard(team = team)
            }

            item {
                SectionTitle(title = stringResource(R.string.team_detail_players))
            }

            if (players.isEmpty()) {
                item {
                    EmptyPlayersCard()
                }
            } else {
                items(players) { player ->
                    PlayerRow(
                        player = player,
                        canRemovePlayer = canManageTeam,
                        onRemovePlayerClick = {
                            playersViewModel.removePlayerFromTeam(
                                playerId = player.id,
                                teamId = team.id
                            )
                        }
                    )
                }
            }

            if (canManageTeam) {
                item {
                    ActionButtons(
                        onAddPlayerClick = {
                            navController.navigate(
                                Screen.AddPlayers.createRoute(team.id)
                            )
                        },
                        onEditTeamClick = {
                            navController.navigate(
                                Screen.EditTeam.createRoute(team.id)
                            )
                        }
                    )
                }
            }

            item {
                SectionTitle(title = stringResource(R.string.team_detail_stats))
            }

            item {
                TeamStatsCard(stats = teamStats)
            }

            item {
                SectionTitle(title = stringResource(R.string.team_detail_registered_events))
            }

            if (registeredTournaments.isEmpty()) {
                item {
                    EmptyRegisteredEventsCard()
                }
            } else {
                items(
                    items = registeredTournaments,
                    key = { tournament -> tournament.id }
                ) { tournament ->
                    RegisteredEventCard(
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
                }
            }

            if (isAdmin) {
                item {
                    DeleteTeamButton(
                        onDeleteClick = { showDeleteDialog = true }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DetailHeader(
    title: String,
    subtitle: String,
    isAdmin: Boolean,
    onBackClick: () -> Unit
) {
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
                AthloBackButton(
                    onClick = {
                        onBackClick()
                    }
                )

                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
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
            contentDescription = stringResource(R.string.create_team_admin_cd),
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.create_team_admin),
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun TeamIdentityCard(team: Team) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(
                            AthloColors.SoftBlue,
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!team.logoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = team.logoUrl,
                            contentDescription = stringResource(R.string.teams_logo_cd),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = stringResource(R.string.teams_team_cd),
                            tint = AthloColors.Blue,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = team.name,
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = localizedTeamTag(team.sport),
                        color = AthloColors.Blue,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(AthloColors.WarningBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = team.acronym,
                    color = AthloColors.DarkNavy,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AthloColors.TextPrimary,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun PlayerRow(
    player: Player,
    canRemovePlayer: Boolean,
    onRemovePlayerClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Medium),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(AthloColors.SoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.add_players_player_cd),
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f)
            ) {
                Text(
                    text = player.name,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(R.string.add_players_position_number, player.position, player.number),
                    color = AthloColors.TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (canRemovePlayer) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(AthloColors.DangerBg, CircleShape)
                        .clickable { onRemovePlayerClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.team_detail_remove_player_cd),
                        tint = Color(0xFFCC1F2F),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPlayersCard() {
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
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.team_detail_no_players_cd),
                    tint = AthloColors.Blue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.team_detail_no_players_title),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.team_detail_no_players_desc),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onAddPlayerClick: () -> Unit,
    onEditTeamClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onAddPlayerClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AthloColors.Blue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.GroupAdd,
                contentDescription = stringResource(R.string.team_detail_add_player),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.team_detail_add_player),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onEditTeamClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AthloColors.Navy),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_team_edit),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.edit_team_edit),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TeamStatsCard(stats: TeamStatsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                icon = Icons.Default.CalendarMonth,
                value = stats.games.toString(),
                label = stringResource(R.string.team_detail_games)
            )

            StatItem(
                icon = Icons.Default.EmojiEvents,
                value = stats.wins.toString(),
                label = stringResource(R.string.team_detail_wins)
            )

            StatItem(
                icon = Icons.Default.SportsSoccer,
                value = stats.goals.toString(),
                label = stringResource(R.string.team_detail_goals)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(AthloColors.SoftBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = AthloColors.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            color = AthloColors.Blue,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = label,
            color = AthloColors.TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RegisteredEventCard(
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
        Column(
            modifier = Modifier.padding(20.dp)
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

            EventTagRows(tags = tags)
        }
    }
}

@Composable
private fun EmptyRegisteredEventsCard() {
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
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = stringResource(R.string.team_detail_no_events_cd),
                tint = AthloColors.TextMuted,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.team_detail_no_events_title),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.team_detail_no_events_desc),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Composable
private fun EventTagRows(tags: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.take(2).forEach { tag ->
                SmallBadge(
                    text = localizedTeamTag(tag),
                    background = tagBackground(tag),
                    textColor = tagTextColor(tag)
                )
            }
        }

        if (tags.size > 2) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.drop(2).forEach { tag ->
                    SmallBadge(
                        text = tag,
                        background = tagBackground(tag),
                        textColor = tagTextColor(tag)
                    )
                }
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
private fun DeleteTeamButton(
    onDeleteClick: () -> Unit
) {
    OutlinedButton(
        onClick = onDeleteClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFCC1F2F)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.team_detail_delete_team_cd),
            tint = Color(0xFFCC1F2F),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.team_detail_delete_team),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TeamNotFoundScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = AthloColors.Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AthloRadius.Large),
                colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.edit_team_not_found),
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AthloColors.Blue),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.common_back),
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
private fun localizedTeamTag(tag: String): String {
    return when (tag) {
        "Futebol" -> stringResource(R.string.create_team_sport_football)
        "Basquetebol" -> stringResource(R.string.create_team_sport_basketball)
        "Ténis" -> stringResource(R.string.create_team_sport_tennis)
        "Voleibol" -> stringResource(R.string.create_team_sport_volleyball)
        "Futsal" -> stringResource(R.string.team_detail_sport_futsal)
        "A decorrer" -> stringResource(R.string.match_detail_status_live)
        "Agendado" -> stringResource(R.string.match_detail_status_scheduled)
        "Terminado" -> stringResource(R.string.match_detail_status_finished)
        "Em preparação" -> stringResource(R.string.event_status_preparing)
        "Grupos" -> stringResource(R.string.format_group)
        "Liga" -> stringResource(R.string.format_league)
        "Eliminatórias" -> stringResource(R.string.format_knockout)
        else -> tag
    }
}

private fun tagBackground(tag: String): Color {
    return when (tag) {
        "Futsal" -> Color(0xFFD7EBFF)
        "Futebol" -> AthloColors.SuccessBg
        "A decorrer" -> AthloColors.SuccessBg
        "Agendado" -> Color(0xFFD7EBFF)
        "Basquetebol" -> AthloColors.WarningBg
        "Em preparação" -> AthloColors.WarningBg
        "Grupos" -> AthloColors.NeutralBg
        "Liga" -> AthloColors.NeutralBg
        "Eliminatórias" -> AthloColors.NeutralBg
        else -> AthloColors.NeutralBg
    }
}

private fun tagTextColor(tag: String): Color {
    return when (tag) {
        "Futsal" -> AthloColors.Blue
        "Futebol" -> Color(0xFF3F7A28)
        "A decorrer" -> Color(0xFF3F7A28)
        "Agendado" -> AthloColors.Blue
        "Basquetebol" -> Color(0xFF9A6B22)
        "Em preparação" -> Color(0xFF9A6B22)
        else -> AthloColors.TextSecondary
    }
}