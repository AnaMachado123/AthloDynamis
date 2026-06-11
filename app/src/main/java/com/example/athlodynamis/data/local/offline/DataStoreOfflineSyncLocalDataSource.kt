package com.example.athlodynamis.data.local.offline

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.offlineSyncDataStore by preferencesDataStore(
    name = "offline_sync_store"
)

class DataStoreOfflineSyncLocalDataSource(
    private val context: Context
) : OfflineSyncLocalDataSource {

    private val operationsKey = stringPreferencesKey("offline_operations")

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun saveOperation(operation: OfflineOperation) {
        val currentOperations = getAllOperations()

        val updatedOperations = currentOperations + operation

        context.offlineSyncDataStore.edit { preferences ->
            preferences[operationsKey] = json.encodeToString(updatedOperations)
        }
    }

    override suspend fun getAllOperations(): List<OfflineOperation> {
        val preferences = context.offlineSyncDataStore.data.first()
        val rawJson = preferences[operationsKey] ?: return emptyList()

        return runCatching {
            json.decodeFromString<List<OfflineOperation>>(rawJson)
        }.getOrDefault(emptyList())
    }

    override suspend fun getPendingOperations(): List<OfflineOperation> {
        return getAllOperations()
            .filter { it.status == "PENDING" }
            .sortedBy { it.createdAt }
    }

    override suspend fun markAsSynced(id: String) {
        val updatedOperations = getAllOperations().map { operation ->
            if (operation.id == id) {
                operation.copy(
                    status = "SYNCED",
                    syncedAt = System.currentTimeMillis(),
                    errorMessage = null
                )
            } else {
                operation
            }
        }

        saveAll(updatedOperations)
    }

    override suspend fun markAsError(id: String, message: String) {
        val updatedOperations = getAllOperations().map { operation ->
            if (operation.id == id) {
                operation.copy(
                    status = "ERROR",
                    errorMessage = message
                )
            } else {
                operation
            }
        }

        saveAll(updatedOperations)
    }

    override suspend fun clearSyncedOperations() {
        val pendingOrErrorOperations = getAllOperations()
            .filter { it.status != "SYNCED" }

        saveAll(pendingOrErrorOperations)
    }

    private suspend fun saveAll(operations: List<OfflineOperation>) {
        context.offlineSyncDataStore.edit { preferences ->
            preferences[operationsKey] = json.encodeToString(operations)
        }
    }
}