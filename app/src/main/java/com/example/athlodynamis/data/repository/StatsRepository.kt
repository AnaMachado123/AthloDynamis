package com.example.athlodynamis.data.repository

import com.example.athlodynamis.domain.model.PlayerStatsData

class StatsRepository(
    private val playerRepository: PlayerRepository = PlayerRepository(),
    private val matchRepository: MatchRepository = MatchRepository(),
    private val matchEventRepository: MatchEventRepository = MatchEventRepository()
) {

    suspend fun getPlayerStatsByUserId(
        userId: String
    ): PlayerStatsData {

        val player = playerRepository.getPlayerByUserId(userId)

        if (player == null) {
            return emptyPlayerStats()
        }

        TeamRepository.fetchTeamsFromSupabase()

        val team = player.teamId?.let {
            TeamRepository.getTeamById(it)
        }

        val totalMatches = team?.games ?: 0
        val wins = team?.wins ?: 0
        val draws = 0
        val losses = (totalMatches - wins).coerceAtLeast(0)

        return PlayerStatsData(
            totalMatches = totalMatches,
            wins = wins,
            draws = draws,
            losses = losses,
            goals = player.goals,
            assists = player.assists,
            yellowCards = player.yellowCards,
            teams = if (player.teamId != null) 1 else 0,
            trophies = 0
        )
    }

    private fun emptyPlayerStats(): PlayerStatsData {
        return PlayerStatsData(
            totalMatches = 0,
            wins = 0,
            draws = 0,
            losses = 0,
            goals = 0,
            assists = 0,
            yellowCards = 0,
            teams = 0,
            trophies = 0
        )
    }
}