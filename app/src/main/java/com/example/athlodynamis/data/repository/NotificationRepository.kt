package com.example.athlodynamis.data.repository

import com.example.athlodynamis.data.remote.SupabaseClientProvider
import com.example.athlodynamis.data.remote.dto.CreateNotificationDto
import com.example.athlodynamis.data.remote.dto.NotificationDto
import com.example.athlodynamis.data.remote.dto.UpdateNotificationReadDto
import com.example.athlodynamis.data.remote.dto.toNotification
import com.example.athlodynamis.domain.model.Notification
import io.github.jan.supabase.postgrest.from

class NotificationRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getNotifications(): List<Notification> {
        return client
            .from("notifications")
            .select()
            .decodeList<NotificationDto>()
            .map { it.toNotification() }
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

    suspend fun markAllNotificationsAsRead() {
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
    }
}