package com.example.athlodynamis.data.local.offline

interface OfflineSyncLocalDataSource {

    suspend fun saveOperation(operation: OfflineOperation)

    suspend fun getAllOperations(): List<OfflineOperation>

    suspend fun getPendingOperations(): List<OfflineOperation>

    suspend fun markAsSynced(id: String)

    suspend fun markAsError(id: String, message: String)

    suspend fun clearSyncedOperations()
}