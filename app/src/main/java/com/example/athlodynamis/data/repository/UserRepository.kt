package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateUserDto
import com.example.athlodynamis.data.remote.dto.UserDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.util.UUID

class UserRepository {

    private val client = SupabaseClientProvider.client

    suspend fun createUser(user: CreateUserDto) {
        client
            .from("users")
            .insert(user)
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
                mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
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
                {
                    set("photo_url", photoUrl)
                }
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