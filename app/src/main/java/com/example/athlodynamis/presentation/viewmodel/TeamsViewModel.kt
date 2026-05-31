package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.athlodynamis.data.repository.TeamRepository
import com.example.athlodynamis.domain.model.Team
import kotlinx.coroutines.flow.StateFlow

class TeamsViewModel : ViewModel() {
    val teams: StateFlow<List<Team>> = TeamRepository.teams

    fun createTeam(
        name: String,
        sport: String,
        level: String
    ) {
        val currentTeams = teams.value

        val newTeam = Team(
            id = (currentTeams.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            acronym = name
                .split(" ")
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .take(3)
                .joinToString(""),
            sport = sport,
            playersCount = 0,
            status = "Em preparação",
            wins = 0,
            games = 0,
            goals = 0
        )

        TeamRepository.addTeam(newTeam)
    }

    fun updateTeam(
        teamId: Int,
        name: String,
        sport: String,
        level: String
    ) {
        val currentTeam = TeamRepository.getTeamById(teamId) ?: return

        val updatedTeam = currentTeam.copy(
            name = name,
            acronym = name
                .split(" ")
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .take(3)
                .joinToString(""),
            sport = sport
        )

        TeamRepository.updateTeam(updatedTeam)
    }
}