package com.example.athlodynamis.domain.model

data class RecentGameData(
    val result: String,
    val matchTitle: String,
    val score: String,
    val subtitle: String
)

data class PlayerStatsData(
    val totalMatches: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goals: Int,
    val assists: Int,
    val yellowCards: Int,
    val redCards: Int,
    val teams: Int,
    val trophies: Int,
    val recentGames: List<RecentGameData> = emptyList()
) {
    val winPercentage: Int
        get() = if (totalMatches == 0) {
            0
        } else {
            ((wins.toFloat() / totalMatches) * 100).toInt()
        }
}