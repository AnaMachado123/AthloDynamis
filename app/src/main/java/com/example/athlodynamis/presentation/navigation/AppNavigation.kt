package com.example.athlodynamis.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athlodynamis.presentation.components.AthloUserRole
import com.example.athlodynamis.presentation.screens.auth.LoginScreen
import com.example.athlodynamis.presentation.screens.auth.RegisterScreen
import com.example.athlodynamis.presentation.screens.events.AddMatchScreen
import com.example.athlodynamis.presentation.screens.events.CreateEventScreen
import com.example.athlodynamis.presentation.screens.events.EditEventScreen
import com.example.athlodynamis.presentation.screens.events.EditMatchScreen
import com.example.athlodynamis.presentation.screens.events.EventsScreen
import com.example.athlodynamis.presentation.screens.events.ManageLiveMatchScreen
import com.example.athlodynamis.presentation.screens.events.TournamentDetailScreen
import com.example.athlodynamis.presentation.screens.home.HomeScreen
import com.example.athlodynamis.presentation.screens.management.ManagementScreen
import com.example.athlodynamis.presentation.screens.matches.MatchDetailScreen
import com.example.athlodynamis.presentation.screens.notifications.NotificationsScreen
import com.example.athlodynamis.presentation.screens.offline.OfflineScreen
import com.example.athlodynamis.presentation.screens.onboarding.OnboardingScreen
import com.example.athlodynamis.presentation.screens.profile.EditProfileScreen
import com.example.athlodynamis.presentation.screens.profile.ProfileScreen
import com.example.athlodynamis.presentation.screens.stats.StatsScreen
import com.example.athlodynamis.presentation.screens.teams.CreateTeamScreen
import com.example.athlodynamis.presentation.screens.teams.EditTeamScreen
import com.example.athlodynamis.presentation.screens.teams.TeamDetailScreen
import com.example.athlodynamis.presentation.screens.teams.TeamsScreen
import com.example.athlodynamis.presentation.screens.management.PendingRequestsScreen
import com.example.athlodynamis.presentation.screens.teams.AddPlayersScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    /*
     * TEMPORÁRIO PARA TESTAR:
     *
     * ADMIN -> vê Home/Admin, Eventos/Admin e tab Gestão
     * ORGANIZER -> vê Home/Organizador e Eventos/Organizador
     * PLAYER -> vê Home/Jogador e Eventos/Jogador
     *
     * Depois isto vem do login/ViewModel.
     */
    val currentUserRole = AthloUserRole.PLAYER

    /*
     * TEMPORÁRIO:
     * Se quiseres testar a Home offline do jogador:
     *
     * val currentUserRole = AthloUserRole.PLAYER
     * val isOffline = true
     */
    val isOffline = false

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
            if (isOffline && currentUserRole == AthloUserRole.PLAYER) {
                OfflineScreen(navController = navController)
            } else {
                HomeScreen(
                    navController = navController,
                    userRole = currentUserRole
                )
            }
        }

        composable(Screen.Offline.route) {
            OfflineScreen(navController = navController)
        }

        composable(Screen.Management.route) {
            ManagementScreen(navController = navController)
        }

        composable(Screen.PendingRequests.route) {
            PendingRequestsScreen(navController = navController)
        }

        composable(Screen.Events.route) {
            EventsScreen(
                navController = navController,
                userRole = currentUserRole
            )
        }

        composable(Screen.CreateEvent.route) {
            CreateEventScreen(navController = navController)
        }

        composable(
            route = Screen.EditEvent.route,
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: "1"

            EditEventScreen(
                navController = navController,
                eventId = eventId,
                userRole = currentUserRole
            )
        }

        composable(
            route = Screen.TournamentDetail.route,
            arguments = listOf(
                navArgument("tournamentId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: "1"

            TournamentDetailScreen(
                tournamentId = tournamentId,
                navController = navController,
                userRole = currentUserRole
            )
        }

        composable(
            route = Screen.AddMatch.route,
            arguments = listOf(
                navArgument("eventId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: "1"

            AddMatchScreen(
                navController = navController,
                eventId = eventId,
                userRole = currentUserRole
            )
        }

        composable(
            route = Screen.EditMatch.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: "1"

            EditMatchScreen(
                navController = navController,
                matchId = matchId,
                userRole = currentUserRole
            )
        }

        composable(
            route = Screen.ManageLiveMatch.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: "1"

            ManageLiveMatchScreen(
                navController = navController,
                matchId = matchId,
                userRole = currentUserRole
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
                navController = navController,
                userRole = currentUserRole
            )
        }

        composable(Screen.Teams.route) {
            TeamsScreen(
                navController = navController,
                userRole = currentUserRole
            )
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
                teamId = teamId,
                userRole = currentUserRole
            )
        }

        composable(Screen.CreateTeam.route) {
            CreateTeamScreen(navController = navController)
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
            StatsScreen(
                navController = navController,
                userRole = currentUserRole
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                userRole = currentUserRole
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }

        composable(
            route = Screen.AddPlayers.route,
            arguments = listOf(
                navArgument("teamId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 1

            AddPlayersScreen(
                navController = navController,
                teamId = teamId,
                userRole = currentUserRole
            )
        }
    }
}