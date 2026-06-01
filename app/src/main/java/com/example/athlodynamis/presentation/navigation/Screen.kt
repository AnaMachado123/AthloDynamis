package com.example.athlodynamis.presentation.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Events : Screen("events")
    data object Teams : Screen("teams")

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

    data object Stats : Screen("stats")
    data object Notifications : Screen("notifications")
}