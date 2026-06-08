package com.example.athlodynamis.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun AthloLogoutButton(
    color: Color = Color.White,
    onClick: () -> Unit
) {
    Text(
        text = "Terminar Sessão",
        color = color,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.clickable {
            onClick()
        }
    )
}