package com.example.athlodynamis.data.repository

import com.example.athlodynamis.domain.model.OrganizerStatsData

class OrganizerStatsRepository(

    private val tournamentRepository: TournamentRepository = TournamentRepository(),
    private val matchRepository: MatchRepository = MatchRepository(),
    private val playerRepository: PlayerRepository = PlayerRepository()

) {

    suspend fun getOrganizerStats(): OrganizerStatsData {

        val tournaments =
            tournamentRepository.getTournaments().size

        val matches =
            matchRepository.getAllMatches().size

        val athletes =
            playerRepository.getAllPlayers().size

        return OrganizerStatsData(
            tournaments = tournaments,
            matches = matches,
            athletes = athletes
        )
    }
}