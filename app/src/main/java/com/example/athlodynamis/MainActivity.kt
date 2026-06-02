package com.example.athlodynamis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.athlodynamis.presentation.navigation.AppNavigation
import com.example.athlodynamis.ui.theme.AthloDynamisTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.athlodynamis.data.remote.SupabaseTest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            SupabaseTest.testTeams()
        }
        setContent {
            AthloDynamisTheme {
                AppNavigation()
            }
        }
    }
}