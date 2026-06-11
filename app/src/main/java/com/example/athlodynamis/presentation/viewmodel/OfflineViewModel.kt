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

    private val offlineSyncRepository =
        OfflineSyncRepository(application)

    val isOnline = networkMonitor.isOnline

    private val _pendingOperationsCount = MutableStateFlow(0)
    val pendingOperationsCount: StateFlow<Int> = _pendingOperationsCount

    init {
        viewModelScope.launch {
            refreshPendingOperationsCount()
        }

        viewModelScope.launch {
            isOnline.collect { online ->
                if (online) {
                    offlineSyncRepository.syncPendingOperations()
                    refreshPendingOperationsCount()
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
}