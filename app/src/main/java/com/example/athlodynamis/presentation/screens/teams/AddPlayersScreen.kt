package com.example.athlodynamis.presentation.screens.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.athlodynamis.presentation.components.*
import com.example.athlodynamis.presentation.navigation.Screen
import com.example.athlodynamis.presentation.viewmodel.PlayersViewModel

@Composable
fun AddPlayersScreen(
    navController: NavController,
    teamId: Int,
    userRole: AthloUserRole
) {
    val playersViewModel: PlayersViewModel = viewModel()

    val isAdmin = userRole == AthloUserRole.ADMIN
    val isLoading by playersViewModel.isLoading.collectAsState()
    val error by playersViewModel.error.collectAsState()

    var name by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    val canSave = name.isNotBlank() &&
            position.isNotBlank() &&
            number.toIntOrNull() != null

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
            verticalArrangement = Arrangement.spacedBy(22.dp),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                AddPlayersHeader(
                    isAdmin = isAdmin,
                    onBackClick = { navController.popBackStack() }
                )
            }

            item {
                Column {
                    Text(
                        text = "Novo jogador",
                        color = AthloColors.TextPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Adiciona um jogador a esta equipa",
                        color = AthloColors.TextSecondary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            item {
                AddPlayerFormCard(
                    name = name,
                    onNameChange = { name = it },
                    position = position,
                    onPositionChange = { position = it },
                    number = number,
                    onNumberChange = { number = it }
                )
            }

            if (error != null) {
                item {
                    Text(
                        text = error ?: "",
                        color = Color(0xFFCC1F2F),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        val playerNumber = number.toIntOrNull()

                        if (playerNumber != null) {
                            playersViewModel.createPlayer(
                                teamId = teamId,
                                name = name.trim(),
                                position = position.trim(),
                                number = playerNumber
                            )

                            navController.popBackStack()
                        }
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
                    Text(
                        text = if (isLoading) "A guardar..." else "Adicionar jogador",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AddPlayersHeader(
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
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = "‹ cancelar",
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Jogadores",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Adicionar jogador",
                    color = Color(0xFF8EC5F4),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (isAdmin) {
                AdminBadge(
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
private fun AddPlayerFormCard(
    name: String,
    onNameChange: (String) -> Unit,
    position: String,
    onPositionChange: (String) -> Unit,
    number: String,
    onNumberChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AthloRadius.Large),
        colors = CardDefaults.cardColors(containerColor = AthloColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(AthloColors.SoftBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Adicionar jogador",
                    tint = AthloColors.Blue,
                    modifier = Modifier.size(30.dp)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nome do jogador") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = position,
                onValueChange = onPositionChange,
                label = { Text("Posição") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Ex: Guarda-redes, Defesa, Avançado") }
            )

            OutlinedTextField(
                value = number,
                onValueChange = { value ->
                    onNumberChange(value.filter { it.isDigit() })
                },
                label = { Text("Número") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
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