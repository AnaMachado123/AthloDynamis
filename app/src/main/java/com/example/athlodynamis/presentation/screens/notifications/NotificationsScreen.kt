package com.example.athlodynamis.presentation.screens.notifications

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
fun NotificationsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Notificações",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Novo jogo agendado")
                Text("Engenharia FC joga amanhã às 18:00.")
            }
        }

        Card(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Resultado atualizado")
                Text("Code Warriors venceu por 3-1.")
            }
        }
    }
}