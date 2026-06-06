package com.example.athlodynamis.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.athlodynamis.presentation.screens.teams.AddPlayersScreen
import com.example.athlodynamis.presentation.screens.teams.CreateTeamScreen
import com.example.athlodynamis.presentation.screens.teams.EditTeamScreen
import com.example.athlodynamis.presentation.screens.teams.TeamDetailScreen
import com.example.athlodynamis.presentation.screens.teams.TeamsScreen
import com.example.athlodynamis.presentation.screens.management.PendingRequestsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import com.example.athlodynamis.presentation.viewmodel.AuthViewModel
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    val currentUserRole = when (authState.userRole) {
        "ADMIN" -> AthloUserRole.ADMIN
        "ORGANIZER" -> AthloUserRole.ORGANIZER
        else -> AthloUserRole.PLAYER
    }

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

            LaunchedEffect(authState.isSuccess) {
                if (authState.isSuccess) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            }

            LoginScreen(
                errorMessage = authState.error,
                onLoginClick = { email, password ->
                    authViewModel.login(
                        email = email,
                        password = password
                    )
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            val authViewModel: AuthViewModel = viewModel()
            val authState by authViewModel.uiState.collectAsState()

            LaunchedEffect(authState.isSuccess) {
                if (authState.isSuccess) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                }
            }

            RegisterScreen(
                isLoading = authState.isLoading,
                errorMessage = authState.error,
                onRegisterClick = { name, email, password, shirtNumber, position ->
                    authViewModel.registerPlayer(
                        name = name,
                        email = email,
                        password = password,
                        shirtNumber = shirtNumber,
                        position = position
                    )
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
                    userRole = currentUserRole,
                    userName = authState.userName ?: "Utilizador",
                    playerTeamId = authState.playerTeamId
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
            CreateEventScreen(
                navController = navController,
                userRole = currentUserRole
            )
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
            CreateTeamScreen(
                navController = navController,
                userRole = currentUserRole
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
            StatsScreen(
                navController = navController,
                userRole = currentUserRole,
                userId = authState.userId ?: ""
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                navController = navController,
                userRole = currentUserRole
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                userRole = currentUserRole,
                userName = authState.userName ?: "Utilizador",
                playerTeamId = authState.playerTeamId,
                onLogoutClick = {
                    authViewModel.logout()

                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }


        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                navController = navController,
                userId = authState.userId ?: "",
                userName = authState.userName ?: "",
                userEmail = authState.userEmail ?: "",
                userPassword = authState.userPassword ?: "",
                onSaveClick = { name, email, password ->
                    authViewModel.updateProfile(
                        name = name,
                        email = email,
                        password = password
                    )

                    navController.popBackStack()
                },
                onPhotoSelected = { userId, imageBytes ->
                    authViewModel.uploadProfilePhoto(
                        userId = userId,
                        imageBytes = imageBytes,
                        onSuccess = { },
                        onError = { }
                    )
                }
            )
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