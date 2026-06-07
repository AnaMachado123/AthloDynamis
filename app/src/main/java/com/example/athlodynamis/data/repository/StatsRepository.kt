package com.example.athlodynamis.data.repository

import coil.util.CoilUtils.result
import com.example.athlodynamis.domain.model.PlayerStatsData
import com.example.athlodynamis.domain.model.RecentGameData

class StatsRepository(
    private val playerRepository: PlayerRepository = PlayerRepository(),
    private val matchRepository: MatchRepository = MatchRepository(),
    private val matchEventRepository: MatchEventRepository = MatchEventRepository()
) {

    suspend fun getPlayerStatsByUserId(
        userId: String
    ): PlayerStatsData {

        val player = playerRepository.getPlayerByUserId(userId)

        if (player == null || player.teamId == null) {
            return emptyPlayerStats()
        }

        val teamId = player.teamId
        val teamIdLong = teamId.toLong()

        val matches = matchRepository.getMatchesByTeamId(teamId)

        val finishedMatches = matches.filter {
            it.status == "Terminado"
        }

        val totalMatches = finishedMatches.size

        val wins = finishedMatches.count { match ->
            when {
                match.teamAId == teamIdLong -> match.scoreA > match.scoreB
                match.teamBId == teamIdLong -> match.scoreB > match.scoreA
                else -> false
            }
        }

        val draws = finishedMatches.count { match ->
            match.scoreA == match.scoreB &&
                    (match.teamAId == teamIdLong || match.teamBId == teamIdLong)
        }

        val losses = finishedMatches.count { match ->
            when {
                match.teamAId == teamIdLong -> match.scoreA < match.scoreB
                match.teamBId == teamIdLong -> match.scoreB < match.scoreA
                else -> false
            }
        }

        val playerEvents = matches.flatMap { match ->
            matchEventRepository.getEventsByMatch(match.id.toInt())
        }.filter { event ->
            event.playerId == player.id
        }

        val goals = playerEvents.count {
            it.eventType == "Golo"
        }

        val assists = playerEvents.count {
            it.eventType == "Assistência"
        }

        val yellowCards = playerEvents.count {
            it.eventType == "Cartão amarelo"
        }

        val redCards = playerEvents.count {
            it.eventType == "Cartão vermelho"
        }

        TeamRepository.fetchTeamsFromSupabase()

        val recentGames = finishedMatches
            .sortedByDescending { it.id }
            .take(4)
            .map { match ->
                val isTeamA = match.teamAId == teamIdLong

                val teamScore = if (isTeamA) {
                    match.scoreA
                } else {
                    match.scoreB
                }

                val opponentScore = if (isTeamA) {
                    match.scoreB
                } else {
                    match.scoreA
                }

                val gameResult = when {
                    teamScore > opponentScore -> "V"
                    teamScore < opponentScore -> "D"
                    else -> "E"
                }

                val teamA = match.teamAId?.let {
                    TeamRepository.getTeamById(it.toInt())
                }

                val teamB = match.teamBId?.let {
                    TeamRepository.getTeamById(it.toInt())
                }

                val teamAAcronym = teamA?.acronym ?: match.teamAName
                val teamBAcronym = teamB?.acronym ?: match.teamBName

                RecentGameData(
                    result = gameResult,
                    matchTitle = "$teamAAcronym vs $teamBAcronym",
                    score = "$teamScore-$opponentScore",
                    subtitle = match.matchTime ?: "Data não definida"
                )
            }

        return PlayerStatsData(
            totalMatches = totalMatches,
            wins = wins,
            draws = draws,
            losses = losses,
            goals = goals,
            assists = assists,
            yellowCards = yellowCards,
            redCards = redCards,
            teams = 1,
            trophies = 0,
            recentGames = recentGames
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
            redCards = 0,
            teams = 0,
            trophies = 0
        )
    }
}