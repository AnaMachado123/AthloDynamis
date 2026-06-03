package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateMatchEventDto
import com.example.athlodynamis.data.remote.dto.MatchEventDto
import com.example.athlodynamis.data.remote.dto.toMatchEvent
import com.example.athlodynamis.domain.model.MatchEvent
import io.github.jan.supabase.postgrest.from

class MatchEventRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getEventsByMatch(matchId: Int): List<MatchEvent> {
        return client
            .from("match_events")
            .select {
                filter {
                    eq("match_id", matchId)
                }
            }
            .decodeList<MatchEventDto>()
            .map { it.toMatchEvent() }
            .sortedBy { it.minute ?: 0 }
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