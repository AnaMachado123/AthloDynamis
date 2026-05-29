package com.example.athlodynamis.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AthloDynamis",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Gestão simples de torneios, equipas e jogos.",
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = onStartClick,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Começar")
        }
    }
}