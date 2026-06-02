package com.example.athlodynamis.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.repository.TeamRepository
import com.example.athlodynamis.domain.model.Team
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamsViewModel : ViewModel() {
    val teams: StateFlow<List<Team>> = TeamRepository.teams

    var teamCreated by mutableStateOf(false)
        private set

    var teamUpdated by mutableStateOf(false)
        private set
    var teamDeleted by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            TeamRepository.fetchTeamsFromSupabase()
        }
    }

    fun createTeam(
        name: String,
        sport: String,
        level: String
    ) {
        viewModelScope.launch {
            TeamRepository.createTeamInSupabase(
                name = name,
                sport = sport,
                level = level
            )

            teamCreated = true
        }
    }

    fun resetTeamCreated() {
        teamCreated = false
    }

    fun updateTeam(
        teamId: Int,
        name: String,
        sport: String,
        level: String
    ) {
        viewModelScope.launch {
            TeamRepository.updateTeamInSupabase(
                teamId = teamId,
                name = name,
                sport = sport,
                level = level
            )

            teamUpdated = true
        }
    }
    fun deleteTeam(teamId: Int) {
        viewModelScope.launch {
            TeamRepository.deleteTeamInSupabase(teamId)

            teamDeleted = true
        }
    }

    fun resetTeamUpdated() {
        teamUpdated = false
    }
    fun resetTeamDeleted() {
        teamDeleted = false
    }
}