package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreatePlayerDto
import com.example.athlodynamis.data.remote.dto.PlayerDto
import com.example.athlodynamis.data.remote.dto.toPlayer
import com.example.athlodynamis.domain.model.Player
import io.github.jan.supabase.postgrest.from

class PlayerRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getPlayerById(playerId: Int): Player? {
        return client
            .from("players")
            .select {
                filter {
                    eq("id", playerId)
                }
            }
            .decodeList<PlayerDto>()
            .firstOrNull()
            ?.toPlayer()
    }
    suspend fun getPlayerByUserId(userId: String): Player? {
        return client
            .from("players")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<PlayerDto>()
            .firstOrNull()
            ?.toPlayer()
    }

    suspend fun getPlayersByTeam(teamId: Int): List<Player> {
        return client
            .from("players")
            .select {
                filter {
                    eq("team_id", teamId)
                }
            }
            .decodeList<PlayerDto>()
            .map { it.toPlayer() }
    }

    suspend fun getAvailablePlayers(): List<Player> {
        return client
            .from("players")
            .select()
            .decodeList<PlayerDto>()
            .map { it.toPlayer() }
            .filter { it.teamId == null }
    }

    suspend fun createPlayer(player: CreatePlayerDto) {
        client
            .from("players")
            .insert(player)
    }

    suspend fun assignPlayerToTeam(
        playerId: Int,
        teamId: Int
    ) {
        client
            .from("players")
            .update(
                mapOf(
                    "team_id" to teamId
                )
            ) {
                filter {
                    eq("id", playerId)
                }
            }
    }

    suspend fun removePlayerFromTeam(playerId: Int) {
        client
            .from("players")
            .update(
                mapOf(
                    "team_id" to null
                )
            ) {
                filter {
                    eq("id", playerId)
                }
            }
    }

    suspend fun deletePlayer(playerId: Int) {
        client
            .from("players")
            .delete {
                filter {
                    eq("id", playerId)
                }
            }
    }


}