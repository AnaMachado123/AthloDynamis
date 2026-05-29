package com.example.athlodynamis.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Entrar",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Palavra-passe") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Entrar")
        }

        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Criar conta")
        }
    }
}