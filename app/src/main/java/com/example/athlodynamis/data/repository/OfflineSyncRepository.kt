package com.example.athlodynamis.data.repository

import android.content.Context
import com.example.athlodynamis.data.local.offline.DataStoreOfflineSyncLocalDataSource
import com.example.athlodynamis.data.local.offline.OfflineOperation
import com.example.athlodynamis.data.remote.dto.CreateMatchEventDto
import com.example.athlodynamis.presentation.viewmodel.OfflineUpdateMatchScorePayload
import com.example.athlodynamis.presentation.viewmodel.OfflineUpdateMatchStatusPayload
import kotlinx.serialization.json.Json
import java.util.UUID

class OfflineSyncRepository(
    context: Context
) {
    private val localDataSource =
        DataStoreOfflineSyncLocalDataSource(context)

    private val matchEventRepository = MatchEventRepository()
    private val matchRepository = MatchRepository()

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