package com.example.athlodynamis.data.mock

import com.example.athlodynamis.domain.model.Team

object MockTeams {
    val teams = listOf(
        Team(
            id = 1,
            name = "SL Benfica",
            acronym = "SLB",
            sport = "Futebol",
            playersCount = 10,
            status = "A decorrer",
            wins = 18,
            games = 24,
            goals = 32
        ),
        Team(
            id = 2,
            name = "Põe te Fino",
            acronym = "PTF",
            sport = "Voleibol",
            playersCount = 12,
            status = "Inscrito",
            wins = 9,
            games = 14,
            goals = 21
        ),
        Team(
            id = 3,
            name = "Viana FC",
            acronym = "VFC",
            sport = "Futebol",
            playersCount = 8,
            status = "Em preparação",
            wins = 5,
            games = 10,
            goals = 15
        )
    )
}