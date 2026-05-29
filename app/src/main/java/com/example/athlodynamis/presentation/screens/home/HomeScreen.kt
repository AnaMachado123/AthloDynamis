package com.example.athlodynamis.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athlodynamis.presentation.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Início",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Bem-vinda ao painel principal da AthloDynamis.",
            modifier = Modifier.padding(top = 12.dp)
        )

        Button(
            onClick = { navController.navigate(Screen.Events.route) },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Ver eventos")
        }

        Button(
            onClick = { navController.navigate(Screen.Teams.route) },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Ver equipas")
        }

        Button(
            onClick = { navController.navigate(Screen.Stats.route) },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Ver estatísticas")
        }

        Button(
            onClick = { navController.navigate(Screen.Notifications.route) },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Ver notificações")
        }
    }
}