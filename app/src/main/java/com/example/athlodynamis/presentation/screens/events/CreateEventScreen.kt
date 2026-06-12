package com.example.athlodynamis.presentation.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.viewmodel.NotificationsViewModel
import com.example.athlodynamis.presentation.viewmodel.TournamentsViewModel

private const val STATUS_PREPARING = "Em preparação"

private const val SPORT_TENNIS_VALUE = "Ténis"
private const val SPORT_FOOTBALL_VALUE = "Futebol"
private const val SPORT_BASKETBALL_VALUE = "Basquetebol"
private const val SPORT_VOLLEYBALL_VALUE = "Voleibol"

private const val FORMAT_LEAGUE_VALUE = "Liga"
private const val FORMAT_KNOCKOUT_VALUE = "Eliminatórias"
private const val FORMAT_GROUP_VALUE = "Grupo"

private data class ChoiceOption(
    val value: String,
    val label: String
)

@Composable
fun CreateEventScreen(
    navController: NavController,
    userRole: AthloUserRole,
    currentUserId: String
) {
    val tournamentsViewModel: TournamentsViewModel = viewModel()
    val notificationsViewModel: NotificationsViewModel = viewModel()

    val error by tournamentsViewModel.error.collectAsState()
    val isLoading by tournamentsViewModel.isLoading.collectAsState()
    val tournamentCreated by tournamentsViewModel.tournamentCreated.collectAsState()

    val isAdmin = userRole == AthloUserRole.ADMIN

    var eventName by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var maxTeams by remember { mutableStateOf("") }

    val sportOptions = listOf(
        ChoiceOption(SPORT_TENNIS_VALUE, stringResource(R.string.sport_tennis)),
        ChoiceOption(SPORT_FOOTBALL_VALUE, stringResource(R.string.sport_football)),
        ChoiceOption(SPORT_BASKETBALL_VALUE, stringResource(R.string.sport_basketball)),
        ChoiceOption(SPORT_VOLLEYBALL_VALUE, stringResource(R.string.sport_volleyball))
    )

    val formatOptions = listOf(
        ChoiceOption(FORMAT_LEAGUE_VALUE, stringResource(R.string.format_league)),
        ChoiceOption(FORMAT_KNOCKOUT_VALUE, stringResource(R.string.format_knockout)),
        ChoiceOption(FORMAT_GROUP_VALUE, stringResource(R.string.format_group))
    )

    val maxTeamOptions = listOf(
        stringResource(R.string.max_teams_4),
        stringResource(R.string.max_teams_8),
        stringResource(R.string.max_teams_16),
        stringResource(R.string.max_teams_32)
    )

    val selectedSportLabel = sportOptions
        .firstOrNull { it.value == selectedSport }
        ?.label
        .orEmpty()

    val notificationTitle = stringResource(R.string.create_event_notification_title)
    val notificationMessage = stringResource(
        R.string.create_event_notification_message,
        eventName.trim(),
        selectedSportLabel.ifBlank { selectedSport }
    )

    val canSave = eventName.isNotBlank() &&
            selectedSport.isNotBlank() &&
            selectedFormat.isNotBlank()

    LaunchedEffect(tournamentCreated) {
        if (tournamentCreated) {
            notificationsViewModel.createNotification(
                title = notificationTitle,
                message = notificationMessage
            )

            tournamentsViewModel.resetTournamentCreated()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = AthloColors.Background
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

            AdminEventHeader(
                title = stringResource(R.string.create_event_title),
                subtitle = "",
                backText = stringResource(R.string.create_event_cancel),
                isAdmin = isAdmin,
                onBackClick = { navController.popBackStack() }
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
                    Spacer(modifier = Modifier.height(18.dp))

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

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        placeholder = stringResource(R.string.create_event_max_teams_hint),
                        options = maxTeamOptions,
                        onValueSelected = { maxTeams = it }
                    )
                }
            }

            if (error != null) {
                Text(
                    text = error ?: "",
                    color = Color(0xFFCC1F2F),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {
                    tournamentsViewModel.createTournament(
                        name = eventName.trim(),
                        sport = selectedSport,
                        startDate = startDate.trim().ifBlank { null },
                        endDate = endDate.trim().ifBlank { null },
                        status = STATUS_PREPARING,
                        format = selectedFormat,
                        rules = null,
                        organizerId = currentUserId.ifBlank { null }
                    )
                },
                enabled = canSave && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AthloColors.Blue,
                    disabledContainerColor = AthloColors.TextMuted
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = stringResource(R.string.create_event_create_cd),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isLoading) {
                        stringResource(R.string.create_event_creating)
                    } else {
                        stringResource(R.string.create_event_create)
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AdminEventHeader(
    title: String,
    subtitle: String,
    backText: String,
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
                Text(
                    text = backText,
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle,
                        color = Color(0xFF8EC5F4),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (isAdmin) {
                AdminBadge(modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    }
}

@Composable
private fun AdminBadge(modifier: Modifier = Modifier) {
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
private fun FieldLabel(text: String) {
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
private fun AthloDropdown(
    selectedValue: String,
    placeholder: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedValue.ifBlank { placeholder },
                color = if (selectedValue.isBlank()) {
                    AthloColors.TextMuted
                } else {
                    AthloColors.TextPrimary
                },
                style = MaterialTheme.typography.bodyMedium,
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
            onDismissRequest = { expanded = false },
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
    options: List<ChoiceOption>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.take(3).forEach { option ->
                ChoicePill(
                    text = option.label,
                    selected = selected == option.value,
                    onClick = { onSelected(option.value) }
                )
            }
        }

        if (options.size > 3) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.drop(3).forEach { option ->
                    ChoicePill(
                        text = option.label,
                        selected = selected == option.value,
                        onClick = { onSelected(option.value) }
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
                if (selected) AthloColors.SoftBlue else AthloColors.NeutralBg,
                RoundedCornerShape(999.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = if (selected) AthloColors.Blue else AthloColors.TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}