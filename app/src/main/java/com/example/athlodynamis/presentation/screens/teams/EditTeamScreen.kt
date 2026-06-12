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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.R
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.components.AthloRadius
import com.example.athlodynamis.presentation.viewmodel.TeamsViewModel
import androidx.compose.runtime.LaunchedEffect
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.athlodynamis.presentation.components.AthloUserRole


private data class EditTeamChoiceOption(
    val value: String,
    val label: String
)

@Composable
fun EditTeamScreen(
    navController: NavController,
    teamId: Int,
    userRole: AthloUserRole
) {
    val viewModel: TeamsViewModel = viewModel()
    val teams by viewModel.teams.collectAsState()
    LaunchedEffect(viewModel.teamUpdated) {
        if (viewModel.teamUpdated) {
            viewModel.resetTeamUpdated()
            navController.popBackStack()
        }
    }
    val team = teams.firstOrNull { it.id == teamId }
    val context = LocalContext.current

    var selectedLogoUri by remember {
        mutableStateOf<Uri?>(null)
    }


    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedLogoUri = uri
    }

    if (team == null) {
        EditTeamNotFoundScreen(
            onBackClick = { navController.popBackStack() }
        )
        return
    }

    var teamName by remember(teamId) { mutableStateOf(team.name) }
    var selectedSport by remember(teamId) { mutableStateOf(team.sport) }
    var selectedLevel by remember(teamId) { mutableStateOf("Avançado") }

    val sportOptions = listOf(
        EditTeamChoiceOption("Futebol", stringResource(R.string.create_team_sport_football)),
        EditTeamChoiceOption("Basquetebol", stringResource(R.string.create_team_sport_basketball)),
        EditTeamChoiceOption("Ténis", stringResource(R.string.create_team_sport_tennis)),
        EditTeamChoiceOption("Voleibol", stringResource(R.string.create_team_sport_volleyball))
    )

    val levelOptions = listOf(
        EditTeamChoiceOption("Avançado", stringResource(R.string.create_team_level_advanced)),
        EditTeamChoiceOption("Médio", stringResource(R.string.create_team_level_medium)),
        EditTeamChoiceOption("Iniciante", stringResource(R.string.create_team_level_beginner))
    )
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

            EditTeamHeader(
                isAdmin = userRole == AthloUserRole.ADMIN,
                onBackClick = {
                    navController.popBackStack()
                }
            )

            EditTeamFormCard(
                teamName = teamName,
                onTeamNameChange = { teamName = it },
                selectedSport = selectedSport,
                onSportChange = { selectedSport = it },
                selectedLevel = selectedLevel,
                onLevelChange = { selectedLevel = it },
                sportOptions = sportOptions,
                levelOptions = levelOptions,
                selectedLogoUri = selectedLogoUri,
                onLogoClick = {
                    logoPickerLauncher.launch("image/*")
                }
            )

            SaveButton(
                enabled = teamName.isNotBlank(),
                onClick = {
                    if (teamName.isNotBlank()) {
                        viewModel.updateTeam(
                            teamId = teamId,
                            name = teamName.trim(),
                            sport = selectedSport,
                            level = selectedLevel,
                            context = context,
                            logoUri = selectedLogoUri,
                            currentLogoUrl = team.logoUrl,
                        )
                    }
                }
            )

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_team_cancel),
                    fontWeight = FontWeight.Bold,
                    color = AthloColors.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EditTeamHeader(
    isAdmin: Boolean,
    onBackClick: () -> Unit
) {
    val sportOptions = listOf(
        EditTeamChoiceOption("Futebol", stringResource(R.string.create_team_sport_football)),
        EditTeamChoiceOption("Basquetebol", stringResource(R.string.create_team_sport_basketball)),
        EditTeamChoiceOption("Ténis", stringResource(R.string.create_team_sport_tennis)),
        EditTeamChoiceOption("Voleibol", stringResource(R.string.create_team_sport_volleyball))
    )

    val levelOptions = listOf(
        EditTeamChoiceOption("Avançado", stringResource(R.string.create_team_level_advanced)),
        EditTeamChoiceOption("Médio", stringResource(R.string.create_team_level_medium)),
        EditTeamChoiceOption("Iniciante", stringResource(R.string.create_team_level_beginner))
    )

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
                Column {
                    Text(
                        text = stringResource(R.string.create_team_cancel_small),
                        color = Color(0xFF9CC8F2),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onBackClick() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.create_team_title),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = stringResource(R.string.edit_team_edit),
                                color = Color(0xFF8EC5F4),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        if (isAdmin) {
                            AdminBadge()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditTeamFormCard(
    teamName: String,
    onTeamNameChange: (String) -> Unit,
    selectedSport: String,
    onSportChange: (String) -> Unit,
    selectedLevel: String,
    onLevelChange: (String) -> Unit,
    sportOptions: List<EditTeamChoiceOption>,
    levelOptions: List<EditTeamChoiceOption>,
    selectedLogoUri: Uri?,
    onLogoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.ExtraLarge),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.edit_team_edit),
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.edit_team_subtitle),
                color = AthloColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            FieldLabel(stringResource(R.string.create_team_name))

            OutlinedTextField(
                value = teamName,
                onValueChange = onTeamNameChange,
                placeholder = { Text(stringResource(R.string.create_team_name_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = formTextFieldColors()
            )

            Spacer(modifier = Modifier.height(18.dp))

            FieldLabel(stringResource(R.string.create_team_sport))

            AthloDropdown(
                selectedValue = selectedSport,
                options = sportOptions,
                onValueSelected = onSportChange,
                icon = Icons.Default.SportsSoccer
            )

            Spacer(modifier = Modifier.height(18.dp))

            FieldLabel(stringResource(R.string.create_team_level))

            AthloDropdown(
                selectedValue = selectedLevel,
                options = levelOptions,
                onValueSelected = onLevelChange,
                icon = Icons.Default.Star
            )

            Spacer(modifier = Modifier.height(18.dp))

            FieldLabel(stringResource(R.string.create_team_logo))

            UploadShieldButton(
                selectedLogoUri = selectedLogoUri,
                onClick = onLogoClick
            )
        }
    }
}

@Composable
private fun UploadShieldButton(
    selectedLogoUri: Uri?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AthloColors.SoftBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(AthloColors.Blue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = stringResource(R.string.create_team_logo_cd),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = if (selectedLogoUri != null)
                        stringResource(R.string.create_team_logo_selected)
                    else
                        stringResource(R.string.create_team_upload_logo),
                    color = AthloColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (selectedLogoUri != null)
                        stringResource(R.string.create_team_image_ready)
                    else
                        stringResource(R.string.create_team_pick_image),
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = stringResource(R.string.create_team_upload_cd),
                tint = AthloColors.Blue
            )
        }
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AthloColors.Blue,
            disabledContainerColor = Color(0xFFBFD7EF)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = stringResource(R.string.edit_team_save_cd),
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.edit_team_save),
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
private fun AthloDropdown(
    selectedValue: String,
    options: List<EditTeamChoiceOption>,
    onValueSelected: (String) -> Unit,
    icon: ImageVector
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selectedValue }?.label ?: selectedValue

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(Color.White, RoundedCornerShape(18.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = selectedLabel,
                    tint = AthloColors.TextMuted,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = selectedLabel,
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.create_team_options_cd),
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
                            text = option.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = AthloColors.TextPrimary
                        )
                    },
                    onClick = {
                        onValueSelected(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EditTeamNotFoundScreen(
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
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
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