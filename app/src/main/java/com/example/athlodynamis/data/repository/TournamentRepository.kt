package com.example.athlodynamis.data.repository

import android.content.Context
import com.example.athlodynamis.data.local.offline.OfflineCacheDataSource
import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateTournamentDto
import com.example.athlodynamis.data.remote.dto.TournamentDto
import com.example.athlodynamis.data.remote.dto.UpdateTournamentDto
import com.example.athlodynamis.data.remote.dto.toTournament
import com.example.athlodynamis.domain.model.Tournament
import io.github.jan.supabase.postgrest.from

class TournamentRepository(
    private val context: Context? = null
) {
    private val client = SupabaseClientProvider.client

    private val cacheDataSource =
        context?.let { OfflineCacheDataSource(it) }

    suspend fun getTournaments(): List<Tournament> {
        return try {
            val remoteTournaments = client
                .from("tournaments")
                .select()
                .decodeList<TournamentDto>()

            cacheDataSource?.saveTournaments(remoteTournaments)

            remoteTournaments.map { it.toTournament() }
        } catch (e: Exception) {
            cacheDataSource
                ?.getCachedTournaments()
                ?.map { it.toTournament() }
                ?: emptyList()
        }
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
        return try {
            val remoteTournament = client
                .from("tournaments")
                .select {
                    filter {
                        eq("id", tournamentId.toLong())
                    }
                }
                .decodeList<TournamentDto>()
                .firstOrNull()

            if (remoteTournament != null) {
                val cachedTournaments =
                    cacheDataSource?.getCachedTournaments().orEmpty()

                val mergedCache = (
                        cachedTournaments.filter {
                            it.id.toString() != tournamentId
                        } + remoteTournament
                        ).distinctBy { it.id }

                cacheDataSource?.saveTournaments(mergedCache)
            }

            remoteTournament?.toTournament()
        } catch (e: Exception) {
            cacheDataSource
                ?.getCachedTournaments()
                ?.firstOrNull { it.id.toString() == tournamentId }
                ?.toTournament()
        }
    }

    suspend fun updateTournament(
        tournamentId: String,
        tournament: UpdateTournamentDto
    ) {
        client
            .from("tournaments")
            .update(tournament) {
                filter {
                    eq("id", tournamentId.toLong())
                }
            }
    }
}