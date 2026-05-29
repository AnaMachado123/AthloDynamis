package com.example.athlodynamis.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athlodynamis.presentation.screens.auth.LoginScreen
import com.example.athlodynamis.presentation.screens.auth.RegisterScreen
import com.example.athlodynamis.presentation.screens.events.EventsScreen
import com.example.athlodynamis.presentation.screens.home.HomeScreen
import com.example.athlodynamis.presentation.screens.notifications.NotificationsScreen
import com.example.athlodynamis.presentation.screens.onboarding.OnboardingScreen
import com.example.athlodynamis.presentation.screens.stats.StatsScreen
import com.example.athlodynamis.presentation.screens.teams.TeamsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onStartClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate(Screen.Home.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Events.route) {
            EventsScreen(navController = navController)
        }

        composable(Screen.Teams.route) {
            TeamsScreen(navController = navController)
        }

        composable(Screen.Stats.route) {
            StatsScreen(navController = navController)
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
    }
}