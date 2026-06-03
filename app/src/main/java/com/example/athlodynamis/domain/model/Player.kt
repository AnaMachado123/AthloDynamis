package com.example.athlodynamis.domain.model

data class Player(
    val id: Int,
    val teamId: Int?,
    val name: String,
    val position: String,
    val number: Int,
    val goals: Int,
    val assists: Int,
    val yellowCards: Int
)