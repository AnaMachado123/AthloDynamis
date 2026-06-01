package com.example.athlodynamis.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.athlodynamis.presentation.screens.teams.TeamDetailScreen
import com.example.athlodynamis.presentation.screens.teams.CreateTeamScreen
import com.example.athlodynamis.presentation.screens.teams.EditTeamScreen
import com.example.athlodynamis.presentation.screens.events.TournamentDetailScreen
import com.example.athlodynamis.presentation.screens.matches.MatchDetailScreen
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val sharedPreferences = remember {
        context.getSharedPreferences("athlo_preferences", Context.MODE_PRIVATE)
    }

    val hasSeenOnboarding = remember {
        sharedPreferences.getBoolean("has_seen_onboarding", false)
    }

    val startDestination = if (hasSeenOnboarding) {
        Screen.Login.route
    } else {
        Screen.Onboarding.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onStartClick = {
                    sharedPreferences
                        .edit()
                        .putBoolean("has_seen_onboarding", true)
                        .apply()

                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
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

        composable(Screen.TournamentDetail.route) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""

            TournamentDetailScreen(
                tournamentId = tournamentId,
                navController = navController
            )
        }

        composable(
            route = Screen.MatchDetail.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: "1"

            MatchDetailScreen(
                matchId = matchId,
                navController = navController
            )
        }
        composable(Screen.Teams.route) {
            TeamsScreen(navController = navController)
        }

        composable(
            route = Screen.TeamDetail.route,
            arguments = listOf(
                navArgument("teamId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 1

            TeamDetailScreen(
                navController = navController,
                teamId = teamId
            )
        }

        composable(Screen.CreateTeam.route) {
            CreateTeamScreen(
                navController = navController
            )
        }

        composable(
            route = Screen.EditTeam.route,
            arguments = listOf(
                navArgument("teamId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 1

            EditTeamScreen(
                navController = navController,
                teamId = teamId
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(navController = navController)
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
    }
}