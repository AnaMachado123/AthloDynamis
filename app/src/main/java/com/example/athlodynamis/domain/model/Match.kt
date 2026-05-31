package com.example.athlodynamis.domain.model

data class Match(
    val id: String,
    val time: String,
    val teamA: String,
    val teamB: String,
    val scoreA: Int?,
    val scoreB: Int?,
    val status: String,
    val minute: String? = null
)