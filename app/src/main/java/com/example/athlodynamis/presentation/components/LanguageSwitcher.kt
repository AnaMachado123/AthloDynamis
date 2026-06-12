package com.example.athlodynamis.presentation.components

import android.app.Activity
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.athlodynamis.presentation.viewmodel.LanguageFlagsViewModel
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

@Composable
fun LanguageSwitcher(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val flagsViewModel: LanguageFlagsViewModel = viewModel()
    val uiState by flagsViewModel.uiState.collectAsState()

    fun changeLanguage(languageTag: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales = LocaleList.forLanguageTags(languageTag)
            (context as? Activity)?.recreate()
        }
    }

    Row(modifier = modifier) {
        if (uiState.portugalFlagUrl != null) {
            AsyncImage(
                model = uiState.portugalFlagUrl,
                contentDescription = "Português",
                modifier = Modifier
                    .size(width = 32.dp, height = 22.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { changeLanguage("pt") }
            )
        } else {
            Text(
                text = "PT",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { changeLanguage("pt") }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (uiState.englishFlagUrl != null) {
            AsyncImage(
                model = uiState.englishFlagUrl,
                contentDescription = "English",
                modifier = Modifier
                    .size(width = 32.dp, height = 22.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { changeLanguage("en") }
            )
        } else {
            Text(
                text = "EN",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { changeLanguage("en") }
            )
        }
    }
}