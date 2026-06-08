package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateFavoriteTeamDto
import com.example.athlodynamis.data.remote.dto.FavoriteTeamDto
import io.github.jan.supabase.postgrest.from

class FavoriteTeamRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getFavoriteTeamIds(userId: String): List<Int> {
        return client
            .from("favorite_teams")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<FavoriteTeamDto>()
            .map { it.teamId }
    }

    suspend fun addFavoriteTeam(
        userId: String,
        teamId: Int
    ) {
        client
            .from("favorite_teams")
            .insert(
                CreateFavoriteTeamDto(
                    userId = userId,
                    teamId = teamId
                )
            )
    }

    suspend fun removeFavoriteTeam(
        userId: String,
        teamId: Int
    ) {
        client
            .from("favorite_teams")
            .delete {
                filter {
                    eq("user_id", userId)
                    eq("team_id", teamId)
                }
            }
    }
}