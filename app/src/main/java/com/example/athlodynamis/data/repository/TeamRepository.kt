package com.example.athlodynamis.data.repository

import android.R.attr.level
import android.util.Log
import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateTeamDto
import com.example.athlodynamis.data.remote.dto.TeamDto
import com.example.athlodynamis.data.remote.dto.toDomain
import com.example.athlodynamis.domain.model.Team
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.athlodynamis.data.remote.dto.UpdateTeamDto
import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.storage
import java.util.UUID
object TeamRepository {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    suspend fun uploadTeamLogo(
        context: Context,
        logoUri: Uri
    ): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(logoUri)
                ?: return null

            val bytes = inputStream.readBytes()
            inputStream.close()

            val fileName = "team_${UUID.randomUUID()}.jpg"

            SupabaseClientProvider.client
                .storage
                .from("team-logos")
                .upload(
                    path = fileName,
                    data = bytes
                ) {
                    upsert = true
                }

            SupabaseClientProvider.client
                .storage
                .from("team-logos")
                .publicUrl(fileName)

        } catch (e: Exception) {
            Log.e("TEAM_REPOSITORY", "Erro ao fazer upload do escudo", e)
            null
        }
    }
    suspend fun fetchTeamsFromSupabase() {
        try {
            val remoteTeams = SupabaseClientProvider.client
                .from("teams")
                .select()
                .decodeList<TeamDto>()
                .map { it.toDomain() }

            _teams.value = remoteTeams

            Log.d("TEAM_REPOSITORY", "Equipas carregadas da Supabase: $remoteTeams")
        } catch (e: Exception) {
            Log.e("TEAM_REPOSITORY", "Erro ao carregar equipas da Supabase", e)
        }
    }

    suspend fun createTeamInSupabase(
        name: String,
        sport: String,
        level: String,
        logoUrl: String? = null
    ) {
        try {
            val acronym = name
                .split(" ")
                .mapNotNull { word -> word.firstOrNull()?.uppercase() }
                .take(3)
                .joinToString("")

            val newTeam = CreateTeamDto(
                name = name,
                acronym = acronym,
                sport = sport,
                players_count = 0,
                status = level,
                wins = 0,
                games = 0,
                goals = 0,
                logo_url = logoUrl
            )

            SupabaseClientProvider.client
                .from("teams")
                .insert(newTeam)

            fetchTeamsFromSupabase()

            Log.d("TEAM_REPOSITORY", "Equipa criada na Supabase: $name")
        } catch (e: Exception) {
            Log.e("TEAM_REPOSITORY", "Erro ao criar equipa na Supabase", e)
        }
    }

    fun addTeam(team: Team) {
        _teams.value = _teams.value + team
    }

    fun getTeamById(teamId: Int): Team? {
        return _teams.value.firstOrNull { it.id == teamId }
    }

    fun updateTeam(updatedTeam: Team) {
        _teams.value = _teams.value.map { team ->
            if (team.id == updatedTeam.id) updatedTeam else team
        }
    }
    suspend fun updateTeamInSupabase(
        teamId: Int,
        name: String,
        sport: String,
        level: String,
        logoUrl: String? = null
    ) {
        try {
            val acronym = name
                .split(" ")
                .mapNotNull { word -> word.firstOrNull()?.uppercase() }
                .take(3)
                .joinToString("")

            val updatedTeam = UpdateTeamDto(
                name = name,
                acronym = acronym,
                sport = sport,
                status = level,
                logo_url = logoUrl
            )

            SupabaseClientProvider.client
                .from("teams")
                .update(updatedTeam) {
                    filter {
                        eq("id", teamId)
                    }
                }

            fetchTeamsFromSupabase()

            Log.d("TEAM_REPOSITORY", "Equipa atualizada na Supabase: $name")
        } catch (e: Exception) {
            Log.e("TEAM_REPOSITORY", "Erro ao atualizar equipa na Supabase", e)
        }
    }
    suspend fun deleteTeamInSupabase(teamId: Int) {
        try {
            SupabaseClientProvider.client
                .from("teams")
                .delete {
                    filter {
                        eq("id", teamId)
                    }
                }

            fetchTeamsFromSupabase()

            Log.d("TEAM_REPOSITORY", "Equipa removida: $teamId")

        } catch (e: Exception) {
            Log.e("TEAM_REPOSITORY", "Erro ao remover equipa", e)
        }
    }
}