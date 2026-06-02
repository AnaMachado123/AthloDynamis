package com.example.athlodynamis.data.remote.dto

import com.example.athlodynamis.domain.model.Team
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val acronym: String,
    val sport: String,
    val players_count: Int,
    val status: String,
    val wins: Int,
    val games: Int,
    val goals: Int,
    val logo_url: String? = null
)

@Serializable
data class CreateTeamDto(
    val name: String,
    val acronym: String,
    val sport: String,
    val players_count: Int,
    val status: String,
    val wins: Int,
    val games: Int,
    val goals: Int,
    val logo_url: String? = null
)

@Serializable
data class UpdateTeamDto(
    val name: String,
    val acronym: String,
    val sport: String,
    val status: String
)
fun TeamDto.toDomain(): Team {
    return Team(
        id = id,
        name = name,
        acronym = acronym,
        sport = sport,
        playersCount = players_count,
        status = status,
        wins = wins,
        games = games,
        goals = goals
    )
}