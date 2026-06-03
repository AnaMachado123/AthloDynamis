package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.remote.dto.CreateMatchDto
import com.example.athlodynamis.data.repository.MatchRepository
import com.example.athlodynamis.domain.model.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MatchesViewModel : ViewModel() {

    private val repository = MatchRepository()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches

    private val _selectedMatch = MutableStateFlow<Match?>(null)
    val selectedMatch: StateFlow<Match?> = _selectedMatch

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadMatches(
        tournamentId: Long
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                _matches.value =
                    repository.getMatchesByTournament(
                        tournamentId
                    )
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar jogos"
            }
        }
    }

    fun loadMatchById(
        matchId: Long
    ) {
        viewModelScope.launch {
            _error.value = null

            try {
                _selectedMatch.value =
                    repository.getMatchById(
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
                repository.createMatch(match)

                loadMatches(match.tournamentId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao criar jogo"
            }
        }
    }

    fun updateMatchScore(
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
    }

    fun updateMatchStatus(
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
    }
}