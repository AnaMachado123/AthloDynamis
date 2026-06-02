package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreatePlayerDto
import com.example.athlodynamis.data.remote.dto.PlayerDto
import com.example.athlodynamis.data.remote.dto.toPlayer
import com.example.athlodynamis.domain.model.Player
import io.github.jan.supabase.postgrest.from

class PlayerRepository {

    private val client = SupabaseClientProvider.client

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

    suspend fun createPlayer(player: CreatePlayerDto) {
        client
            .from("players")
            .insert(player)
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