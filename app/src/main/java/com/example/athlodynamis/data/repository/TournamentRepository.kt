package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateTournamentDto
import com.example.athlodynamis.data.remote.dto.TournamentDto
import com.example.athlodynamis.data.remote.dto.toTournament
import com.example.athlodynamis.domain.model.Tournament
import io.github.jan.supabase.postgrest.from

class TournamentRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getTournaments(): List<Tournament> {
        return client
            .from("tournaments")
            .select()
            .decodeList<TournamentDto>()
            .map { it.toTournament() }
    }

    suspend fun createTournament(tournament: CreateTournamentDto) {
        client
            .from("tournaments")
            .insert(tournament)
    }

    suspend fun deleteTournament(tournamentId: String) {
        client
            .from("tournaments")
            .delete {
                filter {
                    eq("id", tournamentId.toLong())
                }
            }
    }

    suspend fun getTournamentById(tournamentId: String): Tournament? {
        return client
            .from("tournaments")
            .select {
                filter {
                    eq("id", tournamentId.toLong())
                }
            }
            .decodeList<TournamentDto>()
            .firstOrNull()
            ?.toTournament()
    }
}