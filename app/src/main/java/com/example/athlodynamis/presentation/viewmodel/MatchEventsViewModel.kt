package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.remote.dto.CreateMatchEventDto
import com.example.athlodynamis.data.repository.MatchEventRepository
import com.example.athlodynamis.domain.model.MatchEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import com.example.athlodynamis.data.repository.OfflineSyncRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MatchEventsViewModel : ViewModel() {

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    private val repository = MatchEventRepository()

    private val _events = MutableStateFlow<List<MatchEvent>>(emptyList())
    val events: StateFlow<List<MatchEvent>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadEventsByMatch(matchId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _events.value = repository.getEventsByMatch(matchId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar eventos do jogo"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /*fun createMatchEvent(
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

            try {
                repository.createMatchEvent(
                    CreateMatchEventDto(
                        matchId = matchId,
                        playerId = playerId,
                        secondaryPlayerId = secondaryPlayerId,
                        eventType = eventType,
                        minute = minute,
                        teamSide = teamSide
                    )
                )

                loadEventsByMatch(matchId)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao criar evento do jogo"
            } finally {
                _isLoading.value = false
            }
        }
    }*/

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
                    repository.createMatchEvent(dto)
                    loadEventsByMatch(matchId)
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
        matchId: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.deleteMatchEvent(eventId)
                loadEventsByMatch(matchId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao apagar evento do jogo"
            } finally {
                _isLoading.value = false
            }
        }
    }
}