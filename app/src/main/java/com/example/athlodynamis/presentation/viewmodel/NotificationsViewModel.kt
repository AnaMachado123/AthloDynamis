package com.example.athlodynamis.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlodynamis.data.repository.NotificationRepository
import com.example.athlodynamis.domain.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val repository = NotificationRepository()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    fun loadNotifications(
        currentUserId: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = repository.getNotifications(
                    currentUserId = currentUserId
                )

                _notifications.value = result
                _unreadCount.value = result.count { !it.isRead }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar notificações"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createNotification(
        title: String,
        message: String,
        userId: String? = null,
        notificationType: String? = null,
        data: Map<String, String>? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.createNotification(
                    title = title,
                    message = message,
                    userId = userId,
                    notificationType = notificationType,
                    data = data
                )

                loadNotifications()
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao criar notificação"
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.markNotificationAsRead(notificationId)

                val updatedList = _notifications.value.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(isRead = true)
                    } else {
                        notification
                    }
                }

                _notifications.value = updatedList
                _unreadCount.value = updatedList.count { !it.isRead }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao marcar notificação como lida"
            }
        }
    }

    fun markAllAsRead(
        currentUserId: String? = null
    ) {
        viewModelScope.launch {
            try {
                repository.markAllNotificationsAsRead(
                    currentUserId = currentUserId
                )

                val updatedList = _notifications.value.map { notification ->
                    notification.copy(isRead = true)
                }

                _notifications.value = updatedList
                _unreadCount.value = 0
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao marcar notificações como lidas"
            }
        }
    }
}