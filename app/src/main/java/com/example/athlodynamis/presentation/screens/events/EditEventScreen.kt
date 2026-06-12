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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.data.remote.dto.UpdateTournamentDto
import com.example.athlodynamis.data.remote.dto.UserDto
import com.example.athlodynamis.data.repository.NotificationRepository
import com.example.athlodynamis.data.repository.TeamRepository
import com.example.athlodynamis.data.repository.TournamentRepository
import com.example.athlodynamis.data.repository.UserRepository
import com.example.athlodynamis.domain.model.Team
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.presentation.components.AthloBottomBar
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.navigation.Screen
import kotlinx.coroutines.launch

private const val EDIT_STATUS_PREPARING = "Em preparação"

private const val EDIT_SPORT_TENNIS_VALUE = "Ténis"
private const val EDIT_SPORT_FOOTBALL_VALUE = "Futebol"
private const val EDIT_SPORT_BASKETBALL_VALUE = "Basquetebol"
private const val EDIT_SPORT_VOLLEYBALL_VALUE = "Voleibol"

private const val EDIT_FORMAT_LEAGUE_VALUE = "Liga"
private const val EDIT_FORMAT_KNOCKOUT_VALUE = "Eliminatórias"
private const val EDIT_FORMAT_GROUP_VALUE = "Grupo"

private data class EditChoiceOption(
    val value: String,
    val label: String
)

@Composable
fun EditEventScreen(
    navController: NavController,
    eventId: String,
    userRole: AthloUserRole
) {
    val isAdmin = userRole == AthloUserRole.ADMIN
    val notificationRepository = remember { NotificationRepository() }

    var tournament by remember { mutableStateOf<Tournament?>(null) }

    var eventName by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var maxTeams by remember { mutableStateOf("") }
    var organizer by remember { mutableStateOf("") }

    var teams by remember { mutableStateOf<List<Team>>(emptyList()) }
    var selectedTeamIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    var organizers by remember { mutableStateOf<List<UserDto>>(emptyList()) }
    var selectedOrganizerId by remember { mutableStateOf("") }

    val noOrganizerText = stringResource(R.string.edit_event_no_organizer)
    val defaultMaxTeams = stringResource(R.string.max_teams_8)

    val notificationTitle = stringResource(R.string.edit_event_notification_title)
    val notificationMessage = stringResource(
        R.string.edit_event_notification_message,
        eventName.trim()
    )

    val sportOptions = listOf(
        EditChoiceOption(EDIT_SPORT_TENNIS_VALUE, stringResource(R.string.sport_tennis)),
        EditChoiceOption(EDIT_SPORT_FOOTBALL_VALUE, stringResource(R.string.sport_football)),
        EditChoiceOption(EDIT_SPORT_BASKETBALL_VALUE, stringResource(R.string.sport_basketball)),
        EditChoiceOption(EDIT_SPORT_VOLLEYBALL_VALUE, stringResource(R.string.sport_volleyball))
    )

    val formatOptions = listOf(
        EditChoiceOption(EDIT_FORMAT_LEAGUE_VALUE, stringResource(R.string.format_league)),
        EditChoiceOption(EDIT_FORMAT_KNOCKOUT_VALUE, stringResource(R.string.format_knockout)),
        EditChoiceOption(EDIT_FORMAT_GROUP_VALUE, stringResource(R.string.format_group))
    )

    val maxTeamOptions = listOf(
        stringResource(R.string.max_teams_4),
        stringResource(R.string.max_teams_8),
        stringResource(R.string.max_teams_16),
        stringResource(R.string.max_teams_32)
    )

    LaunchedEffect(eventId) {
        val loadedTournament = TournamentRepository()
            .getTournamentById(eventId)

        val loadedOrganizers = UserRepository()
            .getAllUsers()
            .filter {
                it.role.equals("ORGANIZER", ignoreCase = true) &&
                        it.approvalStatus.equals("APPROVED", ignoreCase = true)
            }

        organizers = loadedOrganizers
        tournament = loadedTournament

        if (loadedTournament != null) {
            eventName = loadedTournament.name
            selectedSport = loadedTournament.sport
            selectedFormat = loadedTournament.format

            val dates = loadedTournament.dateRange.split(" - ")
            startDate = dates.getOrNull(0) ?: ""
            endDate = dates.getOrNull(1) ?: ""

            selectedOrganizerId = loadedTournament.organizerId ?: ""

            organizer = loadedOrganizers
                .firstOrNull { it.id == selectedOrganizerId }
                ?.name
                ?: noOrganizerText

            maxTeams = defaultMaxTeams

            TeamRepository.fetchTeamsFromSupabase()

            teams = TeamRepository.teams.value.filter { team ->
                team.sport.equals(
                    loadedTournament.sport,
                    ignoreCase = true
                )
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

            EventEditHeader(
                title = stringResource(R.string.edit_event_title),
                backText = stringResource(R.string.edit_event_cancel),
                isAdmin = isAdmin,
                eventId = eventId,
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
                    FieldLabel(stringResource(R.string.create_event_tournament_name))
                    AthloTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        placeholder = stringResource(R.string.create_event_tournament_name_hint)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.create_event_sport))
                    ChoiceRows(
                        options = sportOptions,
                        selected = selectedSport,
                        onSelected = { selectedSport = it }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.create_event_format))
                    ChoiceRows(
                        options = formatOptions,
                        selected = selectedFormat,
                        onSelected = { selectedFormat = it }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel(stringResource(R.string.create_event_start_date))
                            AthloTextField(
                                value = startDate,
                                onValueChange = { startDate = it },
                                placeholder = stringResource(R.string.create_event_date_hint)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            FieldLabel(stringResource(R.string.create_event_end_date))
                            AthloTextField(
                                value = endDate,
                                onValueChange = { endDate = it },
                                placeholder = stringResource(R.string.create_event_date_hint)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.create_event_location))
                    AthloTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = stringResource(R.string.create_event_location_hint)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.create_event_max_teams))
                    AthloDropdown(
                        selectedValue = maxTeams,
                        options = maxTeamOptions,
                        onValueSelected = { maxTeams = it }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    FieldLabel(stringResource(R.string.edit_event_assign_teams))

                    if (teams.isEmpty()) {
                        Text(
                            text = stringResource(R.string.edit_event_no_teams),
                            color = AthloColors.TextMuted,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        teams.forEach { team ->
                            TeamCheckboxRow(
                                checked = selectedTeamIds.contains(team.id),
                                onCheckedChange = { checked ->
                                    selectedTeamIds = if (checked) {
                                        selectedTeamIds + team.id
                                    } else {
                                        selectedTeamIds - team.id
                                    }
                                },
                                text = team.name
                            )
                        }
                    }

                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(18.dp))

                        FieldLabel(stringResource(R.string.edit_event_change_organizer))
                        AthloDropdown(
                            selectedValue = organizer,
                            options = organizers.map { it.name },
                            onValueSelected = { selectedName ->
                                val selectedOrganizer = organizers.firstOrNull {
                                    it.name == selectedName
                                }

                                organizer = selectedOrganizer?.name ?: noOrganizerText
                                selectedOrganizerId = selectedOrganizer?.id ?: ""
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    kotlinx.coroutines.MainScope().launch {
                        TournamentRepository().updateTournament(
                            tournamentId = eventId,
                            tournament = UpdateTournamentDto(
                                name = eventName.trim(),
                                sport = selectedSport,
                                format = selectedFormat,
                                status = tournament?.status ?: EDIT_STATUS_PREPARING,
                                startDate = startDate.ifBlank { null },
                                endDate = endDate.ifBlank { null },
                                rules = null,
                                organizerId = if (isAdmin) {
                                    selectedOrganizerId.ifBlank { null }
                                } else {
                                    tournament?.organizerId
                                }
                            )
                        )

                        notificationRepository.createNotification(
                            title = notificationTitle,
                            message = notificationMessage
                        )

                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.Blue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(R.string.edit_event_save_cd),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.edit_event_save),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isAdmin) {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD01E1E)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.edit_event_delete_cd),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.edit_event_delete),
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
private fun EventEditHeader(
    title: String,
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
                    color = Color(0xFF9CC8F2),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
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

                Text(
                    text = stringResource(R.string.edit_event_id, eventId),
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
private fun TeamCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AthloColors.NeutralBg, RoundedCornerShape(14.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AthloColors.Blue,
                uncheckedColor = AthloColors.TextMuted,
                checkmarkColor = Color.White
            )
        )

        Text(
            text = text,
            color = AthloColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
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
            Text(placeholder)
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
private fun AthloDropdown(
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .clickable {
                    expanded = true
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedValue,
                color = AthloColors.TextPrimary,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.dropdown_open_options),
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
                            text = option,
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
private fun ChoiceRows(
    options: List<EditChoiceOption>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.take(3).forEach { option ->
                ChoicePill(
                    text = option.label,
                    selected = selected == option.value,
                    onClick = {
                        onSelected(option.value)
                    }
                )
            }
        }

        if (options.size > 3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.drop(3).forEach { option ->
                    ChoicePill(
                        text = option.label,
                        selected = selected == option.value,
                        onClick = {
                            onSelected(option.value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChoicePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (selected) {
                    AthloColors.SoftBlue
                } else {
                    AthloColors.NeutralBg
                },
                RoundedCornerShape(999.dp)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = if (selected) {
                AthloColors.Blue
            } else {
                AthloColors.TextSecondary
            },
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}