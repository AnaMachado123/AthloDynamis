package com.example.athlodynamis.data.remote.dto

import com.example.athlodynamis.domain.model.Tournament
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TournamentDto(
    val id: Long,
    val name: String,
    val sport: String,
    val format: String,
    val status: String,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("end_date")
    val endDate: String? = null,
    val rules: String? = null
)

@Serializable
data class CreateTournamentDto(
    val name: String,
    val sport: String,
    val format: String,
    val status: String,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("end_date")
    val endDate: String? = null,
    val rules: String? = null
)

fun TournamentDto.toTournament(): Tournament {
    return Tournament(
        id = id.toString(),
        name = name,
        sport = sport,
        dateRange = buildDateRange(startDate, endDate),
        status = status,
        format = format
    )
}

private fun buildDateRange(
    startDate: String?,
    endDate: String?
): String {
    return when {
        !startDate.isNullOrBlank() && !endDate.isNullOrBlank() -> "$startDate - $endDate"
        !startDate.isNullOrBlank() -> startDate
        else -> "Data por definir"
    }
}