package com.example.athlodynamis.presentation.screens.events


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun EventsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Eventos",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Torneio Universitário IPVC")
                Text("Modalidade: Futsal")
                Text("Estado: A decorrer")
            }
        }

        Card(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Campeonato Interturmas")
                Text("Modalidade: Andebol")
                Text("Estado: Agendado")
            }
        }
    }
}