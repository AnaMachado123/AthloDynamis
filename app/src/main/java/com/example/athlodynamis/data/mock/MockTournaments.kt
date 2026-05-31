package com.example.athlodynamis.data.mock

import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.domain.model.Standing
import com.example.athlodynamis.domain.model.Tournament
import com.example.athlodynamis.domain.model.TournamentTeam

object MockTournaments {

    val tournaments = listOf(
        Tournament(
            id = "1",
            name = "Torneio de Futsal",
            sport = "Futsal",
            dateRange = "16 abr - 25 abr",
            status = "Agendado",
            format = "Liga"
        ),
        Tournament(
            id = "2",
            name = "Torneio de Braga",
            sport = "Futebol",
            dateRange = "10 abr - 25 abr",
            status = "A decorrer",
            format = "Grupos"
        ),
        Tournament(
            id = "3",
            name = "Torneio Regional Basquetebol",
            sport = "Basquetebol",
            dateRange = "22 jul - 25 jul",
            status = "Em preparação",
            format = "Eliminatórias"
        )
    )

    val matches = listOf(
        Match(
            id = "1",
            time = "10:00",
            teamA = "Equipa 1",
            teamB = "Equipa 2",
            scoreA = 3,
            scoreB = 1,
            status = "Terminado"
        ),
        Match(
            id = "2",
            time = "12:00",
            teamA = "Equipa 3",
            teamB = "Equipa 4",
            scoreA = 2,
            scoreB = 2,
            status = "A decorrer",
            minute = "33'"
        )
    )

    val teams = listOf(
        TournamentTeam(
            id = "1",
            name = "Equipa 1",
            acronym = "EQP",
            players = 8,
            points = 9
        ),
        TournamentTeam(
            id = "2",
            name = "Equipa 2",
            acronym = "EQP",
            players = 7,
            points = 6
        ),
        TournamentTeam(
            id = "3",
            name = "Equipa 3",
            acronym = "EQP",
            players = 8,
            points = 3
        ),
        TournamentTeam(
            id = "4",
            name = "Equipa 4",
            acronym = "EQP",
            players = 6,
            points = 1
        )
    )

    val standings = listOf(
        Standing(
            position = 1,
            teamName = "Equipa 1",
            acronym = "EQP",
            games = 3,
            wins = 3,
            points = 9,
            form = listOf("W", "W", "W")
        ),
        Standing(
            position = 2,
            teamName = "Equipa 2",
            acronym = "EQP",
            games = 3,
            wins = 2,
            points = 6,
            form = listOf("L", "W", "W")
        ),
        Standing(
            position = 3,
            teamName = "Equipa 3",
            acronym = "EQP",
            games = 3,
            wins = 1,
            points = 4,
            form = listOf("L", "L", "W")
        ),
        Standing(
            position = 4,
            teamName = "Equipa 4",
            acronym = "EQP",
            games = 3,
            wins = 0,
            points = 1,
            form = listOf("L", "L", "D")
        )
    )

    fun getTournamentById(id: String): Tournament {
        return tournaments.find { it.id == id } ?: tournaments.first()
    }
}