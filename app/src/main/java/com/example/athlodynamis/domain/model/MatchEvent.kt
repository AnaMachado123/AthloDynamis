package com.example.athlodynamis.domain.model

data class MatchEvent(
    val id: Int,
    val matchId: Int,
    val playerId: Int?,
    val secondaryPlayerId: Int?,
    val eventType: String,
    val minute: Int?,
    val teamSide: String?,
    val createdAt: String?
)