package com.example.athlodynamis.presentation.screens.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun StatsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Estatísticas",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Aqui serão apresentadas classificações, vitórias, derrotas, golos e estatísticas dos jogadores.",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}