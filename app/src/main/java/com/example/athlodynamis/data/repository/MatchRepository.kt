package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateMatchDto
import com.example.athlodynamis.data.remote.dto.MatchDto
import com.example.athlodynamis.data.remote.dto.toMatch
import com.example.athlodynamis.domain.model.Match
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.example.athlodynamis.data.remote.dto.UpdateMatchDto

class MatchRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getAllMatches(): List<Match> {
        return client
            .from("matches")
            .select()
            .decodeList<MatchDto>()
            .map { it.toMatch() }
    }

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

    suspend fun updateMatchScore(
        matchId: Long,
        scoreA: Int,
        scoreB: Int,
        minute: Int
    ) {
        client
            .from("matches")
            .update(
                UpdateMatchScoreDto(
                    scoreA = scoreA,
                    scoreB = scoreB,
                    minute = minute,
                    status = "A decorrer"
                )
            ) {
                filter {
                    eq("id", matchId)
                }
            }
    }

    suspend fun updateMatchStatus(
        matchId: Long,
        status: String,
        minute: Int?
    ) {
        client
            .from("matches")
            .update(
                UpdateMatchStatusDto(
                    status = status,
                    minute = minute
                )
            ) {
                filter {
                    eq("id", matchId)
                }
            }
    }
    suspend fun getMatchesByTeamId(teamId: Int): List<Match> {
        return client
            .from("matches")
            .select()
            .decodeList<MatchDto>()
            .map { it.toMatch() }
            .filter { match ->
                match.teamAId == teamId.toLong() || match.teamBId == teamId.toLong()
            }
    }

    suspend fun updateMatch(
        matchId: Long,
        match: UpdateMatchDto
    ) {
        client
            .from("matches")
            .update(match) {
                filter {
                    eq("id", matchId)
                }
            }
    }

    suspend fun deleteMatch(matchId: Long) {
        client
            .from("matches")
            .delete {
                filter {
                    eq("id", matchId)
                }
            }
    }

}

@Serializable
private data class UpdateMatchScoreDto(
    @SerialName("score_a")
    val scoreA: Int,

    @SerialName("score_b")
    val scoreB: Int,

    val minute: Int,

    val status: String
)

@Serializable
private data class UpdateMatchStatusDto(
    val status: String,

    val minute: Int?
)