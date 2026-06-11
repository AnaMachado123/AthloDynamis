package com.example.athlodynamis.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.network.NetworkMonitor
import com.example.athlodynamis.data.repository.OfflineSyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OfflineViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val networkMonitor = NetworkMonitor(application)
    private val offlineSyncRepository = OfflineSyncRepository(application)

    val isOnline = networkMonitor.isOnline

    private val _pendingOperationsCount = MutableStateFlow(0)
    val pendingOperationsCount: StateFlow<Int> = _pendingOperationsCount

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage

    private var wasOffline = false

    init {
        refreshPendingOperationsCount()

        viewModelScope.launch {
            isOnline.collect { online ->
                if (!online) {
                    wasOffline = true
                    refreshPendingOperationsCount()
                }

                if (online && wasOffline) {
                    syncPendingOperations()
                    wasOffline = false
                }
            }
        }
    }

    fun refreshPendingOperationsCount() {
        viewModelScope.launch {
            _pendingOperationsCount.value =
                offlineSyncRepository.getPendingOperations().size
        }
    }

    fun syncPendingOperations() {
        viewModelScope.launch {
            val pendingCount = offlineSyncRepository.getPendingOperations().size

            if (pendingCount == 0) {
                _syncMessage.value = null
                return@launch
            }

            _isSyncing.value = true
            _syncMessage.value = "A sincronizar dados offline..."

            try {
                offlineSyncRepository.syncPendingOperations()
                refreshPendingOperationsCount()
                _syncMessage.value = "Dados sincronizados com sucesso"
            } catch (e: Exception) {
                _syncMessage.value = "Erro ao sincronizar dados offline"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
}