package com.example.athlodynamis.domain.model

data class PlayerStatsData(
    val totalMatches: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goals: Int,
    val assists: Int,
    val yellowCards: Int,
    val teams: Int,
    val trophies: Int
) {
    val winPercentage: Int
        get() = if (totalMatches == 0) {
            0
        } else {
            ((wins.toFloat() / totalMatches) * 100).toInt()
        }
}