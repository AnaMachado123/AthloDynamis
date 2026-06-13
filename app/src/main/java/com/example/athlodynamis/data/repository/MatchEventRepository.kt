package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateMatchEventDto
import com.example.athlodynamis.data.remote.dto.MatchEventDto
import com.example.athlodynamis.data.remote.dto.toMatchEvent
import com.example.athlodynamis.domain.model.MatchEvent
import io.github.jan.supabase.postgrest.from

class MatchEventRepository(
    private val context: android.content.Context? = null
) {
    private val client = SupabaseClientProvider.client

    private val cacheDataSource =
        context?.let { com.example.athlodynamis.data.local.offline.OfflineCacheDataSource(it) }
    /*suspend fun getEventsByMatch(matchId: Int): List<MatchEvent> {
        return client
            .from("match_events")
            .select {
                filter {
                    eq("match_id", matchId)
                }
            }
            .decodeList<MatchEventDto>()
            .map { it.toMatchEvent() }
            .sortedWith(
                compareByDescending<MatchEvent> { it.minute ?: 0 }
                    .thenByDescending { it.id }
            )
    }*/
    suspend fun getEventsByMatch(matchId: Int): List<MatchEvent> {
        return try {
            val remoteEvents = client
                .from("match_events")
                .select {
                    filter {
                        eq("match_id", matchId)
                    }
                }
                .decodeList<MatchEventDto>()

            val cachedEvents = cacheDataSource?.getCachedMatchEvents().orEmpty()

            val mergedCache = (
                    cachedEvents.filter { it.matchId != matchId } +
                            remoteEvents
                    ).distinctBy { it.id }

            cacheDataSource?.saveMatchEvents(mergedCache)

            remoteEvents
                .map { it.toMatchEvent() }
                .sortedWith(
                    compareByDescending<MatchEvent> { it.minute ?: 0 }
                        .thenByDescending { it.id }
                )
        } catch (e: Exception) {
            cacheDataSource
                ?.getCachedMatchEvents()
                ?.filter { it.matchId == matchId }
                ?.map { it.toMatchEvent() }
                ?.sortedWith(
                    compareByDescending<MatchEvent> { it.minute ?: 0 }
                        .thenByDescending { it.id }
                )
                ?: emptyList()
        }
    }

    suspend fun createMatchEvent(event: CreateMatchEventDto) {
        client
            .from("match_events")
            .insert(event)
    }

    suspend fun deleteMatchEvent(eventId: Int) {
        client
            .from("match_events")
            .delete {
                filter {
                    eq("id", eventId)
                }
            }
    }
}