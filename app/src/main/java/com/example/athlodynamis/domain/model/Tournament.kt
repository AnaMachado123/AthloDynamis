package com.example.athlodynamis.domain.model

data class Tournament(
    val id: String,
    val name: String,
    val sport: String,
    val dateRange: String,
    val status: String,
    val format: String
)