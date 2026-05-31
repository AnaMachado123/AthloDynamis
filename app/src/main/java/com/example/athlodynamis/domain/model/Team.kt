package com.example.athlodynamis.domain.model

data class Team(
    val id: Int,
    val name: String,
    val acronym: String,
    val sport: String,
    val playersCount: Int,
    val status: String,
    val wins: Int,
    val games: Int,
    val goals: Int
)