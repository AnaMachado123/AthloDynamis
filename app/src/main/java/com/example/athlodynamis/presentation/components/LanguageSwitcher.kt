package com.example.athlodynamis.presentation.components

import android.app.Activity
import android.app.LocaleManager
import android.os.LocaleList
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LanguageSwitcher(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun changeLanguage(languageTag: String) {
        val localeManager = context.getSystemService(LocaleManager::class.java)
        localeManager.applicationLocales = LocaleList.forLanguageTags(languageTag)

        (context as? Activity)?.recreate()
    }

    Row(modifier = modifier) {
        Text(
            text = "PT",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                changeLanguage("pt")
            }
        )

        Text(" | ", color = Color.White.copy(alpha = 0.7f))

        Text(
            text = "EN",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                changeLanguage("en")
            }
        )
    }
}