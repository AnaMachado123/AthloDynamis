package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.repository.StatsRepository
import com.example.athlodynamis.domain.model.PlayerStatsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StatsUiState(
    val isLoading: Boolean = false,
    val playerStats: PlayerStatsData? = null,
    val error: String? = null
)

class StatsViewModel(
    private val repository: StatsRepository = StatsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    fun loadPlayerStatsByUserId(userId: String) {
        viewModelScope.launch {
            _uiState.value = StatsUiState(isLoading = true)

            try {
                val stats = repository.getPlayerStatsByUserId(userId)

                _uiState.value = StatsUiState(
                    isLoading = false,
                    playerStats = stats
                )
            } catch (e: Exception) {
                _uiState.value = StatsUiState(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar estatísticas"
                )
            }
        }
    }

}