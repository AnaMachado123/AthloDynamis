package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateMatchDto
import com.example.athlodynamis.data.remote.dto.MatchDto
import com.example.athlodynamis.data.remote.dto.toMatch
import com.example.athlodynamis.domain.model.Match
import io.github.jan.supabase.postgrest.from

class MatchRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getMatchesByTournament(
        tournamentId: Long
    ): List<Match> {
        return client
            .from("matches")
            .select {
                filter {
                    eq("tournament_id", tournamentId)
                }
            }
            .decodeList<MatchDto>()
            .map { it.toMatch() }
    }

    suspend fun getMatchById(
        matchId: Long
    ): Match? {
        return client
            .from("matches")
            .select {
                filter {
                    eq("id", matchId)
                }
            }
            .decodeList<MatchDto>()
            .firstOrNull()
            ?.toMatch()
    }

    suspend fun createMatch(
        match: CreateMatchDto
    ) {
        client
            .from("matches")
            .insert(match)
    }
}