package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.remote.dto.CreateTournamentDto
import com.example.athlodynamis.data.repository.TournamentRepository
import com.example.athlodynamis.domain.model.Tournament
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TournamentsViewModel : ViewModel() {

    private val repository = TournamentRepository()

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _tournamentCreated = MutableStateFlow(false)
    val tournamentCreated: StateFlow<Boolean> = _tournamentCreated

    private val _selectedTournament = MutableStateFlow<Tournament?>(null)
    val selectedTournament: StateFlow<Tournament?> = _selectedTournament

    fun loadTournaments() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _tournaments.value = repository.getTournaments()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar torneios"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createTournament(
        name: String,
        sport: String,
        startDate: String?,
        endDate: String?,
        status: String,
        format: String,
        rules: String? = null,
        organizerId: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _tournamentCreated.value = false

            try {
                repository.createTournament(
                    CreateTournamentDto(
                        name = name,
                        sport = sport,
                        format = format,
                        status = status,
                        startDate = startDate,
                        endDate = endDate,
                        rules = rules,
                        organizerId = organizerId
                    )
                )

                _tournamentCreated.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao criar torneio"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetTournamentCreated() {
        _tournamentCreated.value = false
    }

    fun deleteTournament(tournamentId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTournament(tournamentId)
                loadTournaments()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao apagar torneio"
            }
        }
    }

    fun loadTournamentById(tournamentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _selectedTournament.value = repository.getTournamentById(tournamentId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar torneio"
            } finally {
                _isLoading.value = false
            }
        }
    }
}