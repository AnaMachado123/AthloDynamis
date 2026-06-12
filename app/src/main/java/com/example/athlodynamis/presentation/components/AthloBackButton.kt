package com.example.athlodynamis.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.athlodynamis.R

@Composable
fun AthloBackButton(
    text: String? = null,
    onClick: () -> Unit
) {
    val buttonText = text ?: stringResource(R.string.action_back)

    Text(
        text = "‹ $buttonText",
        color = Color(0xFF9CC8F2),
        fontSize = 19.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.clickable {
            onClick()
        }
    )
}