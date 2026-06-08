package com.example.athlodynamis.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteTeamDto(
    val id: Long? = null,

    @SerialName("user_id")
    val userId: String,

    @SerialName("team_id")
    val teamId: Int,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class CreateFavoriteTeamDto(
    @SerialName("user_id")
    val userId: String,

    @SerialName("team_id")
    val teamId: Int
)