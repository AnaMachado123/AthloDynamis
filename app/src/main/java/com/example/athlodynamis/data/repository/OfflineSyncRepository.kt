package com.example.athlodynamis.data.repository

import android.content.Context
import com.example.athlodynamis.R
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
    private val appContext = context.applicationContext

    private val localDataSource =
        DataStoreOfflineSyncLocalDataSource(appContext)

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
                                    context = appContext,
                                    eventType = payload.eventType
                                ),
                                message = notificationMessageForOfflineEvent(
                                    context = appContext,
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
                                    val isDraw = match.scoreA == match.scoreB

                                    if (isDraw) {
                                        notificationRepository.createNotification(
                                            title = "",
                                            message = "",
                                            notificationType = "MATCH_DRAW",
                                            data = mapOf(
                                                "teamAName" to match.teamAName,
                                                "teamBName" to match.teamBName,
                                                "scoreA" to match.scoreA.toString(),
                                                "scoreB" to match.scoreB.toString()
                                            )
                                        )
                                    } else {
                                        val winnerName: String
                                        val loserName: String
                                        val winnerScore: Int
                                        val loserScore: Int

                                        if (match.scoreA > match.scoreB) {
                                            winnerName = match.teamAName
                                            loserName = match.teamBName
                                            winnerScore = match.scoreA
                                            loserScore = match.scoreB
                                        } else {
                                            winnerName = match.teamBName
                                            loserName = match.teamAName
                                            winnerScore = match.scoreB
                                            loserScore = match.scoreA
                                        }

                                        notificationRepository.createNotification(
                                            title = "",
                                            message = "",
                                            notificationType = "MATCH_WIN",
                                            data = mapOf(
                                                "winnerName" to winnerName,
                                                "loserName" to loserName,
                                                "winnerScore" to winnerScore.toString(),
                                                "loserScore" to loserScore.toString()
                                            )
                                        )
                                    }
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
                        message = e.message ?: appContext.getString(R.string.offline_sync_error)
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
    context: Context,
    eventType: String
): String {
    return when (eventType) {
        "Golo" -> context.getString(R.string.offline_sync_goal_title)
        "Assistência" -> context.getString(R.string.offline_sync_assist_title)
        "Cartão amarelo" -> context.getString(R.string.offline_sync_yellow_card_title)
        "Cartão vermelho" -> context.getString(R.string.offline_sync_red_card_title)
        "Substituição" -> context.getString(R.string.offline_sync_substitution_title)
        else -> context.getString(R.string.offline_sync_event_title)
    }
}

private fun notificationMessageForOfflineEvent(
    context: Context,
    payload: CreateMatchEventDto,
    teamAName: String?,
    teamBName: String?
): String {
    val teamName = when (payload.teamSide) {
        "A" -> teamAName ?: context.getString(R.string.offline_sync_team_a)
        "B" -> teamBName ?: context.getString(R.string.offline_sync_team_b)
        else -> context.getString(R.string.offline_sync_unknown_team)
    }

    val minuteText = payload.minute?.let { minute ->
        context.getString(R.string.offline_sync_minute_text, minute)
    } ?: ""

    return when (payload.eventType) {
        "Golo" -> {
            context.getString(
                R.string.offline_sync_goal_message,
                teamName,
                minuteText
            )
        }

        "Assistência" -> {
            context.getString(
                R.string.offline_sync_assist_message,
                teamName,
                minuteText
            )
        }

        "Cartão amarelo" -> {
            context.getString(
                R.string.offline_sync_yellow_card_message,
                teamName,
                minuteText
            )
        }

        "Cartão vermelho" -> {
            context.getString(
                R.string.offline_sync_red_card_message,
                teamName,
                minuteText
            )
        }

        "Substituição" -> {
            context.getString(
                R.string.offline_sync_substitution_message,
                teamName,
                minuteText
            )
        }

        else -> {
            context.getString(
                R.string.offline_sync_event_message,
                teamName,
                minuteText
            )
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