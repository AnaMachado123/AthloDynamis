package com.example.athlodynamis.data.remote.dto

import com.example.athlodynamis.domain.model.Team
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val acronym: String,
    val sport: String,

    @SerialName("players_count")
    val playersCount: Int,

    val status: String,
    val wins: Int,
    val games: Int,
    val goals: Int,

    @SerialName("logo_url")
    val logoUrl: String? = null,

    @SerialName("created_by")
    val createdBy: String? = null
)

@Serializable
data class CreateTeamDto(
    val name: String,
    val acronym: String,
    val sport: String,

    @SerialName("players_count")
    val playersCount: Int,

    val status: String,
    val wins: Int,
    val games: Int,
    val goals: Int,

    @SerialName("logo_url")
    val logoUrl: String? = null,

    @SerialName("created_by")
    val createdBy: String? = null
)

@Serializable
data class UpdateTeamDto(
    val name: String,
    val acronym: String,
    val sport: String,
    val status: String,

    @SerialName("logo_url")
    val logoUrl: String? = null
)

fun TeamDto.toDomain(): Team {
    return Team(
        id = id,
        name = name,
        acronym = acronym,
        sport = sport,
        playersCount = playersCount,
        status = status,
        wins = wins,
        games = games,
        goals = goals,
        logoUrl = logoUrl,
        createdBy = createdBy
    )
}