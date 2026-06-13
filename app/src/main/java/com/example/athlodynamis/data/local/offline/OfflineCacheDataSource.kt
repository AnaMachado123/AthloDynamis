package com.example.athlodynamis.data.local.offline

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.athlodynamis.data.remote.dto.MatchDto
import com.example.athlodynamis.data.remote.dto.TournamentDto
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.athlodynamis.data.remote.dto.PlayerDto
import com.example.athlodynamis.data.remote.dto.MatchEventDto

private val Context.offlineCacheDataStore by preferencesDataStore(
    name = "offline_cache_store"
)

class OfflineCacheDataSource(
    private val context: Context
) {
    private val matchesKey = stringPreferencesKey("cached_matches")
    private val tournamentsKey = stringPreferencesKey("cached_tournaments")

    private val playersKey = stringPreferencesKey("cached_players")
    private val matchEventsKey = stringPreferencesKey("cached_match_events")
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun saveMatches(matches: List<MatchDto>) {
        context.offlineCacheDataStore.edit { preferences ->
            preferences[matchesKey] = json.encodeToString(matches)
        }
    }

    suspend fun getCachedMatches(): List<MatchDto> {
        val preferences = context.offlineCacheDataStore.data.first()
        val rawJson = preferences[matchesKey] ?: return emptyList()

        return runCatching {
            json.decodeFromString<List<MatchDto>>(rawJson)
        }.getOrDefault(emptyList())
    }

    suspend fun saveTournaments(tournaments: List<TournamentDto>) {
        context.offlineCacheDataStore.edit { preferences ->
            preferences[tournamentsKey] = json.encodeToString(tournaments)
        }
    }

    suspend fun getCachedTournaments(): List<TournamentDto> {
        val preferences = context.offlineCacheDataStore.data.first()
        val rawJson = preferences[tournamentsKey] ?: return emptyList()

        return runCatching {
            json.decodeFromString<List<TournamentDto>>(rawJson)
        }.getOrDefault(emptyList())
    }

    suspend fun savePlayers(players: List<PlayerDto>) {
        context.offlineCacheDataStore.edit { preferences ->
            preferences[playersKey] = json.encodeToString(players)
        }
    }

    suspend fun getCachedPlayers(): List<PlayerDto> {
        val preferences = context.offlineCacheDataStore.data.first()
        val rawJson = preferences[playersKey] ?: return emptyList()

        return runCatching {
            json.decodeFromString<List<PlayerDto>>(rawJson)
        }.getOrDefault(emptyList())
    }

    suspend fun saveMatchEvents(events: List<MatchEventDto>) {
        context.offlineCacheDataStore.edit { preferences ->
            preferences[matchEventsKey] = json.encodeToString(events)
        }
    }

    suspend fun getCachedMatchEvents(): List<MatchEventDto> {
        val preferences = context.offlineCacheDataStore.data.first()
        val rawJson = preferences[matchEventsKey] ?: return emptyList()

        return runCatching {
            json.decodeFromString<List<MatchEventDto>>(rawJson)
        }.getOrDefault(emptyList())
    }
}