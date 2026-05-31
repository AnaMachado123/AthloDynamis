package com.example.athlodynamis.presentation.screens.teams

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.athlodynamis.presentation.components.AthloColors
import com.example.athlodynamis.presentation.viewmodel.TeamsViewModel

@Composable
fun CreateTeamScreen(navController: NavController) {
    val viewModel: TeamsViewModel = viewModel()

    var teamName by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf("Futebol") }
    var selectedLevel by remember { mutableStateOf("Avançado") }

    Scaffold(
        containerColor = AthloColors.Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CreateTeamHeader(
                onBackClick = { navController.popBackStack() }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Criar equipa",
                    color = AthloColors.TextPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Personaliza a tua equipa",
                    color = AthloColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Nome")

                        OutlinedTextField(
                            value = teamName,
                            onValueChange = { teamName = it },
                            placeholder = { Text("Nome da equipa") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = formTextFieldColors()
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Modalidade")

                        AthloDropdown(
                            selectedValue = selectedSport,
                            options = listOf("Futebol", "Basquetebol", "Ténis", "Voleibol"),
                            onValueSelected = { selectedSport = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Nível")

                        AthloDropdown(
                            selectedValue = selectedLevel,
                            options = listOf("Avançado", "Médio", "Pivô", "Líbero", "Passador", "Base"),
                            onValueSelected = { selectedLevel = it }
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Escudo")

                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AthloColors.Blue
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = "Carregar escudo",
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Carregar",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (teamName.isNotBlank()) {
                            viewModel.createTeam(
                                name = teamName.trim(),
                                sport = selectedSport,
                                level = selectedLevel
                            )

                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(180.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AthloColors.Blue
                    )
                ) {
                    Text(
                        text = "Criar equipa",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun CreateTeamHeader(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .background(AthloColors.DarkNavy)
            .padding(horizontal = 24.dp, vertical = 22.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = "‹ cancelar",
                color = Color(0xFF8EC5F4),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Equipas",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Criar equipa",
                color = Color(0xFF8EC5F4),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        AdminBadge(
            modifier = Modifier.align(Alignment.CenterEnd)
        )
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
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFFF3F1E9), RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedValue,
                color = AthloColors.TextPrimary,
                style = MaterialTheme.typography.bodySmall
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Abrir opções",
                tint = AthloColors.DarkNavy
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFFF3F1E9))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodySmall
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
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFE5E7EB),
    unfocusedBorderColor = Color(0xFFE5E7EB),
    focusedContainerColor = Color(0xFFF3F1E9),
    unfocusedContainerColor = Color(0xFFF3F1E9),
    cursorColor = AthloColors.Blue,
    focusedTextColor = AthloColors.TextPrimary,
    unfocusedTextColor = AthloColors.TextPrimary
)