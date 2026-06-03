package com.example.athlodynamis.domain.model

data class Match(
    val id: Long,
    val tournamentId: Long,
    val teamAId: Long?,
    val teamBId: Long?,
    val teamAName: String,
    val teamBName: String,
    val scoreA: Int,
    val scoreB: Int,
    val status: String,
    val matchTime: String?,
    val minute: Int?,
    val location: String?
)