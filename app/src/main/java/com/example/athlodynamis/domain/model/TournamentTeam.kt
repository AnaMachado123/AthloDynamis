package com.example.athlodynamis.domain.model

data class TournamentTeam(
    val id: String,
    val name: String,
    val acronym: String,
    val players: Int,
    val points: Int
)