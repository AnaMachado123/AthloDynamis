package com.example.athlodynamis.data.remote.dto

import com.example.athlodynamis.domain.model.MatchEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchEventDto(
    val id: Int,

    @SerialName("match_id")
    val matchId: Int,

    @SerialName("player_id")
    val playerId: Int? = null,

    @SerialName("event_type")
    val eventType: String,

    val minute: Int? = null,

    @SerialName("team_side")
    val teamSide: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class CreateMatchEventDto(
    @SerialName("match_id")
    val matchId: Int,

    @SerialName("player_id")
    val playerId: Int?,

    @SerialName("event_type")
    val eventType: String,

    val minute: Int?,

    @SerialName("team_side")
    val teamSide: String?
)

fun MatchEventDto.toMatchEvent(): MatchEvent {
    return MatchEvent(
        id = id,
        matchId = matchId,
        playerId = playerId,
        eventType = eventType,
        minute = minute,
        teamSide = teamSide,
        createdAt = createdAt
    )
}