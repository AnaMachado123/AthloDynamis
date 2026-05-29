package com.example.athlodynamis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.athlodynamis.presentation.navigation.AppNavigation
import com.example.athlodynamis.ui.theme.AthloDynamisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AthloDynamisTheme {
                AppNavigation()
            }
        }
    }
}