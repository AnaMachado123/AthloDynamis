package com.example.athlodynamis.domain.model

data class Standing(
    val position: Int,
    val teamName: String,
    val acronym: String,
    val games: Int,
    val wins: Int,
    val points: Int,
    val form: List<String>
)