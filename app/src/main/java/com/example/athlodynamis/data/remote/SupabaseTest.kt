package com.example.athlodynamis.data.remote

import android.util.Log
import com.example.athlodynamis.data.remote.dto.TeamDto
import io.github.jan.supabase.postgrest.from

object SupabaseTest {

    suspend fun testTeams() {
        Log.d("SUPABASE_TEST", "Teste iniciado")

        try {
            val result = SupabaseClientProvider.client
                .from("teams")
                .select()
                .decodeList<TeamDto>()

            Log.d("SUPABASE_TEST", "Equipas recebidas: $result")

        } catch (e: Exception) {
            Log.e("SUPABASE_TEST", "Erro ao buscar equipas", e)
        }
    }
}