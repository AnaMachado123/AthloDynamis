package com.example.athlodynamis.domain.model

data class Notification(
    val id: String,
    val userId: String?,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String?
)