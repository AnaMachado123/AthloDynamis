package com.example.athlodynamis.data.remote.dto

import com.example.athlodynamis.domain.model.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,

    @SerialName("user_id")
    val userId: String? = null,

    val title: String,

    val message: String,

    @SerialName("notification_type")
    val notificationType: String? = null,

    val data: Map<String, String>? = null,

    @SerialName("is_read")
    val isRead: Boolean = false,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class CreateNotificationDto(
    @SerialName("user_id")
    val userId: String? = null,

    val title: String,

    val message: String,

    @SerialName("notification_type")
    val notificationType: String? = null,

    val data: Map<String, String>? = null,

    @SerialName("is_read")
    val isRead: Boolean = false
)

@Serializable
data class UpdateNotificationReadDto(
    @SerialName("is_read")
    val isRead: Boolean
)

fun NotificationDto.toNotification(): Notification {
    return Notification(
        id = id,
        userId = userId,
        title = title,
        message = message,
        notificationType = notificationType,
        data = data,
        isRead = isRead,
        createdAt = createdAt
    )
}