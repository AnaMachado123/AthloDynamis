package com.example.athlodynamis.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String,

    @SerialName("photo_url")
    val photoUrl: String? = null,

    @SerialName("approval_status")
    val approvalStatus: String = "APPROVED",

    @SerialName("organizer_request_message")
    val organizerRequestMessage: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class CreateUserDto(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String,

    @SerialName("approval_status")
    val approvalStatus: String = "APPROVED",

    @SerialName("organizer_request_message")
    val organizerRequestMessage: String? = null
)

@Serializable
data class UpdateUserApprovalDto(
    @SerialName("approval_status")
    val approvalStatus: String
)