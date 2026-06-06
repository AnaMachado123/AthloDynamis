package com.example.athlodynamis.data.mock

import com.example.athlodynamis.domain.model.Player

object MockPlayers {

    val players = listOf(
        Player(1, null, 1, "Ana Maria", "Avançado", 9, 12, 4, 1),
        Player(2, null, 1, "Gonçalo Martins", "Médio", 8, 5, 7, 2),
        Player(3, null, 1, "José Alves", "Defesa", 4, 1, 2, 3),
        Player(4, null, 2, "Carlos Silva", "Líbero", 6, 3, 5, 0),
        Player(5, null, 2, "Miguel Pinto", "Passador", 10, 7, 9, 1)
    )

    fun getPlayersByTeam(teamId: Int): List<Player> {
        return players.filter { it.teamId == teamId }
    }
}