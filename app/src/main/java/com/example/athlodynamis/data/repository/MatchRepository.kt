package com.example.athlodynamis.data.repository

import android.content.Context
import com.example.athlodynamis.data.local.offline.OfflineCacheDataSource
import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateMatchDto
import com.example.athlodynamis.data.remote.dto.MatchDto
import com.example.athlodynamis.data.remote.dto.UpdateMatchDto
import com.example.athlodynamis.data.remote.dto.toMatch
import com.example.athlodynamis.domain.model.Match
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MatchRepository(
    private val context: Context? = null
) {
    private val client = SupabaseClientProvider.client

    private val cacheDataSource =
        context?.let { OfflineCacheDataSource(it) }

    suspend fun getAllMatches(): List<Match> {
        return try {
            val remoteMatches = client
                .from("matches")
                .select()
                .decodeList<MatchDto>()

            cacheDataSource?.saveMatches(remoteMatches)

            remoteMatches.map { it.toMatch() }
        } catch (e: Exception) {
            cacheDataSource
                ?.getCachedMatches()
                ?.map { it.toMatch() }
                ?: emptyList()
        }
    }

    suspend fun getMatchesByTournament(
        tournamentId: Long
    ): List<Match> {
        return try {
            val remoteMatches = client
                .from("matches")
                .select {
                    filter {
                        eq("tournament_id", tournamentId)
                    }
                }
                .decodeList<MatchDto>()

            val cachedMatches = cacheDataSource?.getCachedMatches().orEmpty()

            val mergedCache = (
                    cachedMatches.filter { it.tournamentId != tournamentId } +
                            remoteMatches
                    ).distinctBy { it.id }

            cacheDataSource?.saveMatches(mergedCache)

            remoteMatches.map { it.toMatch() }
        } catch (e: Exception) {
            cacheDataSource
                ?.getCachedMatches()
                ?.filter { it.tournamentId == tournamentId }
                ?.map { it.toMatch() }
                ?: emptyList()
        }
    }

    suspend fun getMatchById(
        matchId: Long
    ): Match? {
        return try {
            val remoteMatch = client
                .from("matches")
                .select {
                    filter {
                        eq("id", matchId)
                    }
                }
                .decodeList<MatchDto>()
                .firstOrNull()

            if (remoteMatch != null) {
                val cachedMatches = cacheDataSource?.getCachedMatches().orEmpty()

                val mergedCache = (
                        cachedMatches.filter { it.id != matchId } +
                                remoteMatch
                        ).distinctBy { it.id }

                cacheDataSource?.saveMatches(mergedCache)
            }

            remoteMatch?.toMatch()
        } catch (e: Exception) {
            cacheDataSource
                ?.getCachedMatches()
                ?.firstOrNull { it.id == matchId }
                ?.toMatch()
        }
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
        return try {
            val remoteMatches = client
                .from("matches")
                .select()
                .decodeList<MatchDto>()

            cacheDataSource?.saveMatches(remoteMatches)

            remoteMatches
                .map { it.toMatch() }
                .filter { match ->
                    match.teamAId == teamId.toLong() ||
                            match.teamBId == teamId.toLong()
                }
        } catch (e: Exception) {
            cacheDataSource
                ?.getCachedMatches()
                ?.map { it.toMatch() }
                ?.filter { match ->
                    match.teamAId == teamId.toLong() ||
                            match.teamBId == teamId.toLong()
                }
                ?: emptyList()
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