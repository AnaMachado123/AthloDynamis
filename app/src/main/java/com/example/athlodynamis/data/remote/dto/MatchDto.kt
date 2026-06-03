package com.example.athlodynamis.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.example.athlodynamis.domain.model.Match

@Serializable
data class MatchDto(

    val id: Long,

    @SerialName("tournament_id")
    val tournamentId: Long,

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

    val minute: String? = null,

    val location: String? = null
)

fun MatchDto.toMatch(): Match {
    return Match(
        id = id.toString(),
        tournamentId = tournamentId.toString(),
        time = matchTime ?: "",
        teamA = teamAName,
        teamB = teamBName,
        scoreA = scoreA,
        scoreB = scoreB,
        status = status,
        minute = minute,
        location = location
    )
}

@Serializable
data class CreateMatchDto(

    @SerialName("tournament_id")
    val tournamentId: Long,

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

    val minute: String? = null,

    val location: String? = null
)