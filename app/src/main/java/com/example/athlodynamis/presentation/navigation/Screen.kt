package com.example.athlodynamis.presentation.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Events : Screen("events")
    data object Teams : Screen("teams")
    data object Stats : Screen("stats")
    data object Notifications : Screen("notifications")
}