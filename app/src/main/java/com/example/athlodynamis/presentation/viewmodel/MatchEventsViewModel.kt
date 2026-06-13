package com.example.athlodynamis.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.remote.dto.CreateMatchEventDto
import com.example.athlodynamis.data.repository.MatchEventRepository
import com.example.athlodynamis.data.repository.OfflineSyncRepository
import com.example.athlodynamis.domain.model.MatchEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MatchEventsViewModel : ViewModel() {

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    private fun repository(context: Context? = null): MatchEventRepository {
        return MatchEventRepository(context)
    }

    private val _events = MutableStateFlow<List<MatchEvent>>(emptyList())
    val events: StateFlow<List<MatchEvent>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadEventsByMatch(
        matchId: Int,
        context: Context? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _events.value = repository(context).getEventsByMatch(matchId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar eventos do jogo"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createMatchEvent(
        context: Context,
        isOnline: Boolean,
        matchId: Int,
        playerId: Int?,
        secondaryPlayerId: Int? = null,
        eventType: String,
        minute: Int?,
        teamSide: String?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val dto = CreateMatchEventDto(
                matchId = matchId,
                playerId = playerId,
                secondaryPlayerId = secondaryPlayerId,
                eventType = eventType,
                minute = minute,
                teamSide = teamSide
            )

            try {
                if (isOnline) {
                    repository(context).createMatchEvent(dto)
                    loadEventsByMatch(matchId, context)
                } else {
                    OfflineSyncRepository(context).savePendingOperation(
                        operationType = "CREATE_MATCH_EVENT",
                        entityName = "match_events",
                        payloadJson = json.encodeToString(dto)
                    )
                }

                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao criar evento do jogo"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMatchEvent(
        eventId: Int,
        matchId: Int,
        context: Context? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository(context).deleteMatchEvent(eventId)
                loadEventsByMatch(matchId, context)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao apagar evento do jogo"
            } finally {
                _isLoading.value = false
            }
        }
    }
}