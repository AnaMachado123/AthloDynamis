package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.remote.dto.CreateMatchDto
import com.example.athlodynamis.data.repository.MatchRepository
import com.example.athlodynamis.domain.model.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import com.example.athlodynamis.data.repository.OfflineSyncRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.athlodynamis.data.remote.dto.UpdateMatchDto

class MatchesViewModel : ViewModel() {

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    private fun repository(context: Context? = null): MatchRepository {
        return MatchRepository(context)
    }

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    private val _selectedMatch = MutableStateFlow<Match?>(null)
    val selectedMatch: StateFlow<Match?> = _selectedMatch

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadMatches(
        tournamentId: Long,
        context: Context? = null
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                _matches.value =
                    repository(context).getMatchesByTournament(
                        tournamentId
                    )
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar jogos"
            }
        }
    }

    fun loadMatchById(
        matchId: Long,
        context: Context? = null
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                _selectedMatch.value =
                    repository(context).getMatchById(
                        matchId
                    )
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar jogo"
            }
        }
    }

    fun createMatch(
        match: CreateMatchDto
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                repository().createMatch(match)

                loadMatches(match.tournamentId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao criar jogo"
            }
        }
    }

    fun updateMatch(
        matchId: Long,
        match: UpdateMatchDto,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                repository().updateMatch(
                    matchId = matchId,
                    match = match
                )

                loadMatchById(matchId)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao atualizar jogo"
            }
        }
    }

    fun deleteMatch(
        matchId: Long,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                repository().deleteMatch(matchId)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao apagar jogo"
            }
        }
    }

    /*fun updateMatchScore(
        matchId: Long,
        scoreA: Int,
        scoreB: Int,
        minute: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                repository.updateMatchScore(
                    matchId = matchId,
                    scoreA = scoreA,
                    scoreB = scoreB,
                    minute = minute
                )

                loadMatchById(matchId)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao atualizar marcador"
            }
        }
    }*/

    fun updateMatchScore(
        context: Context,
        isOnline: Boolean,
        matchId: Long,
        scoreA: Int,
        scoreB: Int,
        minute: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                if (isOnline) {
                    repository().updateMatchScore(
                        matchId = matchId,
                        scoreA = scoreA,
                        scoreB = scoreB,
                        minute = minute
                    )

                    loadMatchById(matchId, context)
                } else {
                    OfflineSyncRepository(context).savePendingOperation(
                        operationType = "UPDATE_MATCH_SCORE",
                        entityName = "matches",
                        payloadJson = json.encodeToString(
                            OfflineUpdateMatchScorePayload(
                                matchId = matchId,
                                scoreA = scoreA,
                                scoreB = scoreB,
                                minute = minute
                            )
                        )
                    )
                }

                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao atualizar marcador"
            }
        }
    }

    /*fun updateMatchStatus(
        matchId: Long,
        status: String,
        minute: Int?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                repository.updateMatchStatus(
                    matchId = matchId,
                    status = status,
                    minute = minute
                )

                loadMatchById(matchId)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao atualizar estado do jogo"
            }
        }
    }*/

    fun updateMatchStatus(
        context: Context,
        isOnline: Boolean,
        matchId: Long,
        status: String,
        minute: Int?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                if (isOnline) {
                    repository().updateMatchStatus(
                        matchId = matchId,
                        status = status,
                        minute = minute
                    )

                    loadMatchById(matchId, context)
                } else {
                    OfflineSyncRepository(context).savePendingOperation(
                        operationType = "UPDATE_MATCH_STATUS",
                        entityName = "matches",
                        payloadJson = json.encodeToString(
                            OfflineUpdateMatchStatusPayload(
                                matchId = matchId,
                                status = status,
                                minute = minute
                            )
                        )
                    )
                }

                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao atualizar estado do jogo"
            }
        }
    }
}


@Serializable
data class OfflineUpdateMatchScorePayload(
    val matchId: Long,
    val scoreA: Int,
    val scoreB: Int,
    val minute: Int
)

@Serializable
data class OfflineUpdateMatchStatusPayload(
    val matchId: Long,
    val status: String,
    val minute: Int?
)