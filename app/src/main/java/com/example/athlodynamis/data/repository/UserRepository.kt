package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateUserDto
import com.example.athlodynamis.data.remote.dto.UpdateUserApprovalDto
import com.example.athlodynamis.data.remote.dto.UserDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

class UserRepository {

    private val client = SupabaseClientProvider.client

    suspend fun createUser(user: CreateUserDto) {
        client
            .from("users")
            .insert(user)
    }

    suspend fun getAllUsers(): List<UserDto> {
        return client
            .from("users")
            .select()
            .decodeList<UserDto>()
    }

    suspend fun getPendingOrganizerRequests(): List<UserDto> {
        return client
            .from("users")
            .select {
                filter {
                    eq("role", "ORGANIZER")
                    eq("approval_status", "PENDING")
                }
            }
            .decodeList<UserDto>()
            .sortedBy { it.createdAt ?: "" }
    }

    suspend fun updateOrganizerApproval(
        userId: String,
        approvalStatus: String,
        approvedAt: String? = null
    ) {
        client
            .from("users")
            .update(
                UpdateUserApprovalDto(
                    approvalStatus = approvalStatus,
                    approvedAt = approvedAt
                )
            ) {
                filter {
                    eq("id", userId)
                }
            }
    }

    suspend fun getUserByEmail(email: String): UserDto? {
        return client
            .from("users")
            .select {
                filter {
                    eq("email", email)
                }
            }
            .decodeList<UserDto>()
            .firstOrNull()
    }

    suspend fun updateUser(
        userId: String,
        name: String,
        email: String,
        password: String
    ) {
        client
            .from("users")
            .update(
                UpdateUserDto(
                    name = name,
                    email = email,
                    password = password
                )
            ) {
                filter {
                    eq("id", userId)
                }
            }
    }

    suspend fun updateUserPhoto(
        userId: String,
        photoUrl: String
    ) {
        client
            .from("users")
            .update(
                UpdateUserPhotoDto(
                    photoUrl = photoUrl
                )
            ) {
                filter {
                    eq("id", userId)
                }
            }
    }

    suspend fun uploadProfilePhoto(
        userId: String,
        bytes: ByteArray
    ): String {
        val fileName = "${userId}_${UUID.randomUUID()}.jpg"

        client.storage
            .from("profile-images")
            .upload(
                path = fileName,
                data = bytes
            ) {
                upsert = true
            }

        return client.storage
            .from("profile-images")
            .publicUrl(fileName)
    }
}

@Serializable
private data class UpdateUserDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
private data class UpdateUserPhotoDto(
    @SerialName("photo_url")
    val photoUrl: String
)