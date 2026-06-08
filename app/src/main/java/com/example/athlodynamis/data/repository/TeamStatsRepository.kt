package com.example.athlodynamis.data.repository

import com.example.athlodynamis.domain.model.TeamStatsData

class TeamStatsRepository(
    private val matchRepository: MatchRepository = MatchRepository()
) {
    suspend fun getTeamStats(teamId: Int): TeamStatsData {
        val teamIdLong = teamId.toLong()

        val matches = matchRepository.getMatchesByTeamId(teamId)

        val finishedMatches = matches.filter {
            it.status.equals("Terminado", ignoreCase = true)
        }

        val wins = finishedMatches.count { match ->
            when {
                match.teamAId == teamIdLong -> match.scoreA > match.scoreB
                match.teamBId == teamIdLong -> match.scoreB > match.scoreA
                else -> false
            }
        }

        val goals = finishedMatches.sumOf { match ->
            when {
                match.teamAId == teamIdLong -> match.scoreA
                match.teamBId == teamIdLong -> match.scoreB
                else -> 0
            }
        }

        return TeamStatsData(
            games = finishedMatches.size,
            wins = wins,
            goals = goals
        )
    }
}