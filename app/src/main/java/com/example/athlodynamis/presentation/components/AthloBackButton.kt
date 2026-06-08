package com.example.athlodynamis.presentation.components

import android.R.attr.fontWeight
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun AthloBackButton(
    text: String = "voltar",
    onClick: () -> Unit
) {

    Text(
        text = "‹ $text",
        color = Color(0xFF9CC8F2),
        fontSize = 19.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.clickable {
            onClick()
        }
    )
}