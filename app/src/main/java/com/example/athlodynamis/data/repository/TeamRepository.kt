package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.mock.MockTeams
import com.example.athlodynamis.domain.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TeamRepository {
    private val _teams = MutableStateFlow(MockTeams.teams)
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    fun addTeam(team: Team) {
        _teams.value = _teams.value + team
    }

    fun getTeamById(teamId: Int): Team? {
        return _teams.value.firstOrNull { it.id == teamId }
    }

    fun updateTeam(updatedTeam: Team) {
        _teams.value = _teams.value.map { team ->
            if (team.id == updatedTeam.id) {
                updatedTeam
            } else {
                team
            }
        }
    }
}