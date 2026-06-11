package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateNotificationDto
import com.example.athlodynamis.data.remote.dto.NotificationDto
import com.example.athlodynamis.data.remote.dto.UpdateNotificationReadDto
import com.example.athlodynamis.data.remote.dto.UserDto
import com.example.athlodynamis.data.remote.dto.toNotification
import com.example.athlodynamis.domain.model.Notification
import io.github.jan.supabase.postgrest.from

class NotificationRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getNotifications(
        currentUserId: String? = null
    ): List<Notification> {
        val currentUser = if (currentUserId.isNullOrBlank()) {
            null
        } else {
            getUserById(currentUserId)
        }
        val visibleFrom = currentUser?.approvedAt ?: currentUser?.createdAt

        return client
            .from("notifications")
            .select()
            .decodeList<NotificationDto>()
            .map { it.toNotification() }
            .filter { notification ->
                val isOwnNotification =
                    notification.userId == currentUserId

                val isGlobalAfterVisibleFrom =
                    notification.userId == null &&
                            visibleFrom != null &&
                            notification.createdAt != null &&
                            notification.createdAt >= visibleFrom

                isOwnNotification || isGlobalAfterVisibleFrom
            }
            .sortedWith(
                compareByDescending<Notification> { it.createdAt ?: "" }
                    .thenByDescending { it.id }
            )
    }

    suspend fun createNotification(
        title: String,
        message: String,
        userId: String? = null
    ) {
        client
            .from("notifications")
            .insert(
                CreateNotificationDto(
                    userId = userId,
                    title = title,
                    message = message,
                    isRead = false
                )
            )
    }

    suspend fun getUserCreatedAt(userId: String): String? {
        return SupabaseClientProvider.client
            .from("users")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<UserDto>()
            .createdAt
    }

    suspend fun getUserById(userId: String): UserDto {
        return client
            .from("users")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<UserDto>()
    }
    suspend fun markNotificationAsRead(notificationId: String) {
        client
            .from("notifications")
            .update(
                UpdateNotificationReadDto(
                    isRead = true
                )
            ) {
                filter {
                    eq("id", notificationId)
                }
            }
    }

    suspend fun getUserIdsByRole(role: String): List<String> {
        return SupabaseClientProvider.client
            .from("users")
            .select {
                filter {
                    eq("role", role)
                }
            }
            .decodeList<UserDto>()
            .map { it.id }
    }

    suspend fun createNotificationForUsers(
        userIds: List<String>,
        title: String,
        message: String
    ) {
        userIds
            .filter { it.isNotBlank() }
            .distinct()
            .forEach { userId ->
                createNotification(
                    title = title,
                    message = message,
                    userId = userId
                )
            }
    }


    /*suspend fun markAllNotificationsAsRead() {
        client
            .from("notifications")
            .update(
                UpdateNotificationReadDto(
                    isRead = true
                )
            ) {
                filter {
                    eq("is_read", false)
                }
            }
    }*/

    suspend fun markAllNotificationsAsRead(
        currentUserId: String? = null
    ) {
        val visibleUnreadNotifications = getNotifications(
            currentUserId = currentUserId
        ).filter { !it.isRead }

        visibleUnreadNotifications.forEach { notification ->
            markNotificationAsRead(notification.id)
        }
    }
}