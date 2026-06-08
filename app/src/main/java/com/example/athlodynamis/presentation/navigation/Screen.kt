package com.example.athlodynamis.presentation.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")

    data object Home : Screen("home")
    data object Management : Screen("management")
    data object Events : Screen("events")
    data object Teams : Screen("teams")
    data object Stats : Screen("stats")
    data object Notifications : Screen("notifications")

    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")

    data object TeamDetail : Screen("team_detail/{teamId}") {
        fun createRoute(teamId: Int): String = "team_detail/$teamId"
    }

    data object CreateTeam : Screen("create_team")

    data object EditTeam : Screen("edit_team/{teamId}") {
        fun createRoute(teamId: Int): String = "edit_team/$teamId"
    }

    data object TournamentDetail : Screen("tournament_detail/{tournamentId}") {
        fun createRoute(tournamentId: String): String = "tournament_detail/$tournamentId"
    }

    data object MatchDetail : Screen("match_detail/{matchId}") {
        fun createRoute(matchId: String): String = "match_detail/$matchId"
    }

    data object CreateEvent : Screen("create_event")

    data object EditEvent : Screen("edit_event/{eventId}") {
        fun createRoute(eventId: String): String = "edit_event/$eventId"
    }

    data object AddMatch : Screen("add_match/{eventId}") {
        fun createRoute(eventId: String): String = "add_match/$eventId"
    }

    data object EditMatch : Screen("edit_match/{matchId}") {
        fun createRoute(matchId: String): String = "edit_match/$matchId"
    }

    data object ManageLiveMatch : Screen("manage_live_match/{matchId}") {
        fun createRoute(matchId: String): String = "manage_live_match/$matchId"
    }

    data object Offline : Screen("offline")

    data object PendingRequests : Screen("pending_requests")

    data object AddPlayers : Screen("add_players/{teamId}") {
        fun createRoute(teamId: Int): String = "add_players/$teamId"
    }
    data object AccountPending : Screen("account_pending")
}