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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.data.remote.dto.CreateMatchDto
import com.example.athlodynamis.data.repository.NotificationRepository
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.MatchesViewModel
import com.example.athlodynamis.presentation.viewmodel.TeamsViewModel
import com.example.athlodynamis.presentation.viewmodel.TournamentsViewModel
import kotlinx.coroutines.launch

private const val MATCH_STATUS_SCHEDULED = "Agendado"

private data class TeamOption(
    val id: Long,
    val name: String
)

@Composable
fun AddMatchScreen(
    navController: NavController,
    eventId: String,
    userRole: AthloUserRole
) {
    val matchesViewModel: MatchesViewModel = viewModel()
    val notificationRepository = remember { NotificationRepository() }
    val coroutineScope = rememberCoroutineScope()

    val teamsViewModel: TeamsViewModel = viewModel()
    val tournamentsViewModel: TournamentsViewModel = viewModel()

    val error by matchesViewModel.error.collectAsState()
    val allTeams by teamsViewModel.teams.collectAsState()
    val tournaments by tournamentsViewModel.tournaments.collectAsState()

    val currentEventId = eventId
    val isAdmin = userRole == AthloUserRole.ADMIN

    var matchCreated by remember { mutableStateOf(false) }

    var matchTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var teamA by remember { mutableStateOf<TeamOption?>(null) }
    var teamB by remember { mutableStateOf<TeamOption?>(null) }

    val tournamentId = eventId.toLongOrNull()

    val currentTournament = tournaments.firstOrNull {
        it.id == eventId
    }

    val filteredTeamOptions = allTeams
        .filter { team ->
            currentTournament == null ||
                    team.sport.equals(currentTournament.sport, ignoreCase = true)
        }
        .map { team ->
            TeamOption(
                id = team.id.toLong(),
                name = team.name
            )
        }

    val canSave = tournamentId != null &&
            matchTime.isNotBlank() &&
            teamA != null &&
            teamB != null &&
            teamA?.id != teamB?.id

    val notificationTitle = stringResource(R.string.add_match_notification_title)
    val selectedTeamAName = teamA?.name.orEmpty()
    val selectedTeamBName = teamB?.name.orEmpty()
    val notificationMessage = stringResource(
        R.string.add_match_notification_message,
        selectedTeamAName,
        selectedTeamBName
    )

    LaunchedEffect(matchCreated) {
        if (matchCreated) {
            navController.popBackStack()
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

            AddMatchHeader(
                title = stringResource(R.string.add_match_title),
                subtitle = stringResource(R.string.add_match_subtitle),
                backText = stringResource(R.string.add_match_cancel),
                isAdmin = isAdmin,
                eventId = currentEventId,
                onBackClick = {
                    navController.popBackStack()
                }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AthloRadius.ExtraLarge),
                colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    FieldLabel(stringResource(R.string.add_match_time_label))
                    AthloTextField(
                        value = matchTime,
                        onValueChange = { matchTime = it },
                        placeholder = stringResource(R.string.add_match_time_hint)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.add_match_location_label))
                    AthloTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = stringResource(R.string.add_match_location_hint)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    FieldLabel(stringResource(R.string.add_match_team_1_label))
                    TeamDropdown(
                        selectedTeam = teamA,
                        placeholder = stringResource(R.string.add_match_team_1_hint),
                        options = filteredTeamOptions,
                        onValueSelected = {
                            teamA = it
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.add_match_team_2_label))
                    TeamDropdown(
                        selectedTeam = teamB,
                        placeholder = stringResource(R.string.add_match_team_2_hint),
                        options = filteredTeamOptions,
                        onValueSelected = {
                            teamB = it
                        }
                    )
                }
            }

            if (teamA != null && teamA?.id == teamB?.id) {
                ErrorText(text = stringResource(R.string.add_match_same_teams_error))
            }

            if (tournamentId == null) {
                ErrorText(text = stringResource(R.string.add_match_invalid_tournament_error))
            }

            if (error != null) {
                ErrorText(text = error ?: "")
            }

            Button(
                onClick = {
                    val selectedTeamA = teamA
                    val selectedTeamB = teamB
                    val selectedTournamentId = tournamentId

                    if (selectedTeamA != null && selectedTeamB != null && selectedTournamentId != null) {
                        matchesViewModel.createMatch(
                            CreateMatchDto(
                                tournamentId = selectedTournamentId,
                                teamAId = selectedTeamA.id,
                                teamBId = selectedTeamB.id,
                                teamAName = selectedTeamA.name,
                                teamBName = selectedTeamB.name,
                                scoreA = 0,
                                scoreB = 0,
                                status = MATCH_STATUS_SCHEDULED,
                                matchTime = matchTime.trim(),
                                minute = null,
                                location = location.trim().ifBlank { null }
                            )
                        )

                        coroutineScope.launch {
                            notificationRepository.createNotification(
                                title = notificationTitle,
                                message = notificationMessage
                            )

                            matchCreated = true
                        }
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.Blue,
                    disabledContainerColor = AthloColors.TextMuted
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(R.string.add_match_create_cd),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.add_match_create),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AddMatchHeader(
    title: String,
    subtitle: String,
    backText: String,
    isAdmin: Boolean,
    eventId: String,
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
                Text(
                    text = backText,
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onBackClick()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(R.string.add_match_tournament_id, eventId),
                    color = Color(0xFF8EC5F4).copy(alpha = 0.65f),
                    style = MaterialTheme.typography.labelSmall
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
            contentDescription = stringResource(R.string.admin_badge),
            tint = AthloColors.DarkNavy,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.admin_badge),
            color = AthloColors.DarkNavy,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun FieldLabel(
    text: String
) {
    Text(
        text = text,
        color = AthloColors.TextPrimary,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun AthloTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AthloColors.Blue,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = AthloColors.Blue,
            focusedTextColor = AthloColors.TextPrimary,
            unfocusedTextColor = AthloColors.TextPrimary
        )
    )
}

@Composable
private fun TeamDropdown(
    selectedTeam: TeamOption?,
    placeholder: String,
    options: List<TeamOption>,
    onValueSelected: (TeamOption) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .clickable {
                    expanded = true
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedTeam?.name ?: placeholder,
                color = if (selectedTeam == null) {
                    AthloColors.TextMuted
                } else {
                    AthloColors.TextPrimary
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.add_match_select_team_cd),
                tint = AthloColors.TextMuted
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.name,
                            color = AthloColors.TextPrimary
                        )
                    },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorText(text: String) {
    Text(
        text = text,
        color = Color(0xFFCC1F2F),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold
    )
}