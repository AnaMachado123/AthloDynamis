package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.remote.dto.CreatePlayerDto
import com.example.athlodynamis.data.repository.PlayerRepository
import com.example.athlodynamis.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayersViewModel : ViewModel() {

    private val repository = PlayerRepository()

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players

    private val _availablePlayers = MutableStateFlow<List<Player>>(emptyList())
    val availablePlayers: StateFlow<List<Player>> = _availablePlayers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPlayersByTeam(teamId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _players.value = repository.getPlayersByTeam(teamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar jogadores"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAvailablePlayers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _availablePlayers.value = repository.getAvailablePlayers()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar jogadores disponíveis"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPlayer(
        teamId: Int,
        name: String,
        position: String,
        number: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.createPlayer(
                    CreatePlayerDto(
                        teamId = teamId,
                        name = name,
                        position = position,
                        number = number
                    )
                )

                loadPlayersByTeam(teamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao criar jogador"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun assignPlayerToTeam(
        playerId: Int,
        teamId: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.assignPlayerToTeam(
                    playerId = playerId,
                    teamId = teamId
                )

                loadPlayersByTeam(teamId)
                loadAvailablePlayers()

                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao associar jogador à equipa"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removePlayerFromTeam(
        playerId: Int,
        teamId: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.removePlayerFromTeam(playerId)
                loadPlayersByTeam(teamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao remover jogador da equipa"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePlayer(
        playerId: Int,
        teamId: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.deletePlayer(playerId)
                loadPlayersByTeam(teamId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao apagar jogador"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPlayersByTeams(
        teamAId: Int?,
        teamBId: Int?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val teamAPlayers = if (teamAId != null) {
                    repository.getPlayersByTeam(teamAId)
                } else {
                    emptyList()
                }

                val teamBPlayers = if (teamBId != null) {
                    repository.getPlayersByTeam(teamBId)
                } else {
                    emptyList()
                }

                _players.value = (teamAPlayers + teamBPlayers)
                    .distinctBy { it.id }

            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar jogadores das equipas"
            } finally {
                _isLoading.value = false
            }
        }
    }
}