package com.example.athlodynamis.data.remote.dto

import com.example.athlodynamis.domain.model.Player
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: Int,
    @SerialName("team_id")
    val teamId: Int,
    val name: String,
    val position: String,
    val number: Int,
    val goals: Int,
    val assists: Int,
    @SerialName("yellow_cards")
    val yellowCards: Int
)

@Serializable
data class CreatePlayerDto(
    @SerialName("team_id")
    val teamId: Int,
    val name: String,
    val position: String,
    val number: Int,
    val goals: Int = 0,
    val assists: Int = 0,
    @SerialName("yellow_cards")
    val yellowCards: Int = 0
)

fun PlayerDto.toPlayer(): Player {
    return Player(
        id = id,
        teamId = teamId,
        name = name,
        position = position,
        number = number,
        goals = goals,
        assists = assists,
        yellowCards = yellowCards
    )
}