package com.example.athlodynamis.data.repository

import android.content.Context
import com.example.athlodynamis.data.local.offline.DataStoreOfflineSyncLocalDataSource
import com.example.athlodynamis.data.local.offline.OfflineOperation
import com.example.athlodynamis.data.remote.dto.CreateMatchEventDto
import com.example.athlodynamis.domain.model.Match
import com.example.athlodynamis.presentation.viewmodel.OfflineUpdateMatchScorePayload
import com.example.athlodynamis.presentation.viewmodel.OfflineUpdateMatchStatusPayload
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

private object OfflineSyncLock {
    var isSyncing: Boolean = false
}

class OfflineSyncRepository(
    context: Context
) {
    private val localDataSource =
        DataStoreOfflineSyncLocalDataSource(context)

    private val matchEventRepository = MatchEventRepository()
    private val matchRepository = MatchRepository()
    private val userRepository = UserRepository()
    private val notificationRepository = NotificationRepository()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun savePendingOperation(
        operationType: String,
        entityName: String,
        payloadJson: String
    ) {
        localDataSource.saveOperation(
            OfflineOperation(
                id = UUID.randomUUID().toString(),
                operationType = operationType,
                entityName = entityName,
                payloadJson = payloadJson,
                status = "PENDING"
            )
        )
    }

    suspend fun getPendingOperations(): List<OfflineOperation> {
        return localDataSource.getPendingOperations()
    }

    suspend fun syncPendingOperations() {
        if (OfflineSyncLock.isSyncing) {
            return
        }

        OfflineSyncLock.isSyncing = true

        try {
            val pendingOperations = getPendingOperations()

            pendingOperations.forEach { operation ->
                try {
                    when (operation.operationType) {
                        "CREATE_MATCH_EVENT" -> {
                            val payload =
                                json.decodeFromString<CreateMatchEventDto>(
                                    operation.payloadJson
                                )

                            matchEventRepository.createMatchEvent(payload)

                            val match = matchRepository.getMatchById(
                                matchId = payload.matchId.toLong()
                            )

                            notificationRepository.createNotification(
                                title = notificationTitleForOfflineEvent(
                                    eventType = payload.eventType
                                ),
                                message = notificationMessageForOfflineEvent(
                                    payload = payload,
                                    teamAName = match?.teamAName,
                                    teamBName = match?.teamBName
                                )
                            )
                        }

                        "UPDATE_MATCH_SCORE" -> {
                            val payload =
                                json.decodeFromString<OfflineUpdateMatchScorePayload>(
                                    operation.payloadJson
                                )

                            matchRepository.updateMatchScore(
                                matchId = payload.matchId,
                                scoreA = payload.scoreA,
                                scoreB = payload.scoreB,
                                minute = payload.minute
                            )
                        }

                        "UPDATE_MATCH_STATUS" -> {
                            val payload =
                                json.decodeFromString<OfflineUpdateMatchStatusPayload>(
                                    operation.payloadJson
                                )

                            matchRepository.updateMatchStatus(
                                matchId = payload.matchId,
                                status = payload.status,
                                minute = payload.minute
                            )

                            if (payload.status.equals("Terminado", ignoreCase = true)) {
                                val match = matchRepository.getMatchById(
                                    matchId = payload.matchId
                                )

                                if (match != null) {
                                    notificationRepository.createNotification(
                                        title = finishMatchNotificationTitle(match),
                                        message = finishMatchNotificationMessage(match)
                                    )
                                }
                            }
                        }

                        "UPDATE_USER_PROFILE" -> {
                            val payload =
                                json.decodeFromString<OfflineProfileUpdatePayload>(
                                    operation.payloadJson
                                )

                            userRepository.updateUser(
                                userId = payload.userId,
                                name = payload.name,
                                email = payload.email,
                                password = payload.password
                            )
                        }
                    }

                    markAsSynced(operation.id)

                } catch (e: Exception) {
                    markAsError(
                        id = operation.id,
                        message = e.message ?: "Erro ao sincronizar operação offline"
                    )
                }
            }

            clearSyncedOperations()

        } finally {
            OfflineSyncLock.isSyncing = false
        }
    }

    suspend fun markAsSynced(id: String) {
        localDataSource.markAsSynced(id)
    }

    suspend fun markAsError(id: String, message: String) {
        localDataSource.markAsError(id, message)
    }

    suspend fun clearSyncedOperations() {
        localDataSource.clearSyncedOperations()
    }
}

private fun notificationTitleForOfflineEvent(
    eventType: String
): String {
    return when (eventType) {
        "Golo" -> "Golo sincronizado"
        "Assistência" -> "Assistência sincronizada"
        "Cartão amarelo" -> "Cartão amarelo sincronizado"
        "Cartão vermelho" -> "Cartão vermelho sincronizado"
        "Substituição" -> "Substituição sincronizada"
        else -> "Evento sincronizado"
    }
}

private fun notificationMessageForOfflineEvent(
    payload: CreateMatchEventDto,
    teamAName: String?,
    teamBName: String?
): String {
    val teamName = when (payload.teamSide) {
        "A" -> teamAName ?: "equipa A"
        "B" -> teamBName ?: "equipa B"
        else -> "equipa desconhecida"
    }

    val minuteText = payload.minute?.let { " ao minuto $it" } ?: ""

    return when (payload.eventType) {
        "Golo" -> {
            "Foi sincronizado um golo do(a) $teamName$minuteText que tinha sido registado offline."
        }

        "Assistência" -> {
            "Foi sincronizada uma assistência do(a) $teamName$minuteText que tinha sido registada offline."
        }

        "Cartão amarelo" -> {
            "Foi sincronizado um cartão amarelo do(a) $teamName$minuteText que tinha sido registado offline."
        }

        "Cartão vermelho" -> {
            "Foi sincronizado um cartão vermelho do(a) $teamName$minuteText que tinha sido registado offline."
        }

        "Substituição" -> {
            "Foi sincronizada uma substituição do(a) $teamName$minuteText que tinha sido registada offline."
        }

        else -> {
            "Foi sincronizado um evento de jogo do(a) $teamName$minuteText que tinha sido registado offline."
        }
    }
}

private fun finishMatchNotificationTitle(
    match: Match
): String {
    return when {
        match.scoreA > match.scoreB -> {
            "Vitória de ${match.teamAName}"
        }

        match.scoreB > match.scoreA -> {
            "Vitória de ${match.teamBName}"
        }

        else -> {
            "Empate no jogo"
        }
    }
}

private fun finishMatchNotificationMessage(
    match: Match
): String {
    return when {
        match.scoreA > match.scoreB -> {
            "${match.teamAName} venceu ${match.teamBName} por ${match.scoreA} - ${match.scoreB}. Jogo terminado offline e sincronizado."
        }

        match.scoreB > match.scoreA -> {
            "${match.teamBName} venceu ${match.teamAName} por ${match.scoreB} - ${match.scoreA}. Jogo terminado offline e sincronizado."
        }

        else -> {
            "${match.teamAName} e ${match.teamBName} empataram ${match.scoreA} - ${match.scoreB}. Jogo terminado offline e sincronizado."
        }
    }
}

@Serializable
data class OfflineProfileUpdatePayload(
    val userId: String,
    val name: String,
    val email: String,
    val password: String
)