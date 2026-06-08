package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.repository.FavoriteTeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteTeamsViewModel : ViewModel() {

    private val repository = FavoriteTeamRepository()

    private val _favoriteTeamIds = MutableStateFlow<List<Int>>(emptyList())
    val favoriteTeamIds: StateFlow<List<Int>> = _favoriteTeamIds.asStateFlow()

    fun loadFavorites(userId: String) {
        if (userId.isBlank()) return

        viewModelScope.launch {
            _favoriteTeamIds.value = repository.getFavoriteTeamIds(userId)
        }
    }

    fun toggleFavorite(
        userId: String,
        teamId: Int
    ) {
        if (userId.isBlank()) return

        viewModelScope.launch {
            val currentFavorites = _favoriteTeamIds.value

            if (teamId in currentFavorites) {
                repository.removeFavoriteTeam(
                    userId = userId,
                    teamId = teamId
                )

                _favoriteTeamIds.value = currentFavorites - teamId
            } else {
                repository.addFavoriteTeam(
                    userId = userId,
                    teamId = teamId
                )

                _favoriteTeamIds.value = currentFavorites + teamId
            }
        }
    }
}