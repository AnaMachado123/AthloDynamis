package com.example.athlodynamis.data.remote.dto

import com.example.athlodynamis.domain.model.Match
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val id: Long,

    @SerialName("tournament_id")
    val tournamentId: Long,

    @SerialName("team_a_id")
    val teamAId: Long? = null,

    @SerialName("team_b_id")
    val teamBId: Long? = null,

    @SerialName("team_a_name")
    val teamAName: String,

    @SerialName("team_b_name")
    val teamBName: String,

    @SerialName("score_a")
    val scoreA: Int = 0,

    @SerialName("score_b")
    val scoreB: Int = 0,

    val status: String,

    @SerialName("match_time")
    val matchTime: String? = null,

    val minute: Int? = null,

    val location: String? = null
)

@Serializable
data class CreateMatchDto(
    @SerialName("tournament_id")
    val tournamentId: Long,

    @SerialName("team_a_id")
    val teamAId: Long? = null,

    @SerialName("team_b_id")
    val teamBId: Long? = null,

    @SerialName("team_a_name")
    val teamAName: String,

    @SerialName("team_b_name")
    val teamBName: String,

    @SerialName("score_a")
    val scoreA: Int = 0,

    @SerialName("score_b")
    val scoreB: Int = 0,

    val status: String = "Agendado",

    @SerialName("match_time")
    val matchTime: String? = null,

    val minute: Int? = null,

    val location: String? = null
)

@Serializable
data class UpdateMatchDto(
    @SerialName("team_a_id")
    val teamAId: Long? = null,

    @SerialName("team_b_id")
    val teamBId: Long? = null,

    @SerialName("team_a_name")
    val teamAName: String,

    @SerialName("team_b_name")
    val teamBName: String,

    @SerialName("match_time")
    val matchTime: String? = null,

    val location: String? = null,

    val status: String
)

fun MatchDto.toMatch(): Match {
    return Match(
        id = id,
        tournamentId = tournamentId,
        teamAId = teamAId,
        teamBId = teamBId,
        teamAName = teamAName,
        teamBName = teamBName,
        scoreA = scoreA,
        scoreB = scoreB,
        status = status,
        matchTime = matchTime,
        minute = minute,
        location = location
    )
}